package io.github.NEVERMAIN.service.order;

import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.google.common.eventbus.EventBus;
import io.github.NEVERMAIN.dao.OrderItemDao;
import io.github.NEVERMAIN.dao.ProductDao;
import io.github.NEVERMAIN.dao.UserOrderDao;
import io.github.NEVERMAIN.domain.po.OrderItem;
import io.github.NEVERMAIN.domain.po.Product;
import io.github.NEVERMAIN.domain.po.UserOrder;
import io.github.NEVERMAIN.domain.req.ShopCartItem;
import io.github.NEVERMAIN.domain.req.ShopCartReq;
import io.github.NEVERMAIN.domain.res.UserOrderRes;
import io.github.NEVERMAIN.domain.vo.UserOrderStatusVO;
import io.github.NEVERMAIN.service.IUserOrderService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserOrderService implements IUserOrderService {

    private static final Logger log = LoggerFactory.getLogger(UserOrderService.class);

    @Value("${alipay.notify_url}")
    private String notifyUrl;
    @Value("${alipay.return_url}")
    private String returnUrl;

    private AlipayClient alipayClient;

    private UserOrderDao userOrderDao;

    private OrderItemDao orderItemDao;

    private ProductDao productDao;

    private EventBus eventBus;


    public UserOrderService(UserOrderDao userOrderDao, OrderItemDao orderItemDao, ProductDao productDao,
                            AlipayClient alipayClient, EventBus eventBus) {
        this.userOrderDao = userOrderDao;
        this.orderItemDao = orderItemDao;
        this.productDao = productDao;
        this.alipayClient = alipayClient;
        this.eventBus = eventBus;
    }

    @Override
    @Transactional
    public UserOrderRes createOrder(ShopCartReq shopCartReq) throws AlipayApiException {

        UserOrder userOrderReq = new UserOrder();
        userOrderReq.setUserId(shopCartReq.getUserId());

        List<ShopCartItem> shopCartItems = shopCartReq.getShopCartItems();
        List<String> productIds = shopCartItems.stream().map(ShopCartItem::getProductId).toList();

        UserOrder unpaidOrder = userOrderDao.queryUnPayOrder(userOrderReq);

        if (unpaidOrder != null) {
            if (UserOrderStatusVO.PAY_WAIT.getCode().equals(unpaidOrder.getStatus()) && unpaidOrder.getPayUrl() != null) {
                log.info("创建订单-存在,已经存在未支付订单 userId:{}", shopCartReq.getUserId());
                return UserOrderRes.builder()
                        .orderId(unpaidOrder.getOrderId())
                        .payUrl(unpaidOrder.getPayUrl())
                        .build();
            }
            if (UserOrderStatusVO.CREATE.getCode().equals(unpaidOrder.getStatus()) && unpaidOrder.getPayUrl() == null) {
                log.info("创建订单-存在，存在未创建支付单订单，创建支付单开始 userId:{} orderId:{}", shopCartReq.getUserId(),
                        unpaidOrder.getOrderId());
                List<Product> productList = productDao.queryProductByIds(productIds);
                String payUrl = doPrepareOrder(unpaidOrder, productList);
                return UserOrderRes.builder()
                        .orderId(unpaidOrder.getOrderId())
                        .payUrl(payUrl)
                        .build();
            }
        }

        // 创建订单
        // 将 shopCartItem 转成一个 map 结构 productId -> quantity
        Map<String, Integer> collect = shopCartItems.stream().collect(Collectors.toMap(ShopCartItem::getProductId,
                ShopCartItem::getQuantity));

        String orderId = RandomStringUtils.randomNumeric(12);

        // 1.查询商品信息
        List<Product> productList = productDao.queryProductByIds(productIds);

        // 计算每一个产品的总价
        List<OrderItem> orderItemList = calculateOrderItems(productList, collect, orderId);

        // 1.1 计算产品总价
        double totalAmount = orderItemList.stream().mapToDouble(OrderItem::getTotalPrice).sum();

        // 2.创建订单
        UserOrder userOrder = createUserOrder(shopCartReq, orderId, totalAmount);

        // 3.保存订单和订单项
        saveOrderAndItems(userOrder, orderItemList);

        // 4.创建支付单
        // Todo 对接支付宝,创建支付单
        String payUrl = doPrepareOrder(userOrder, productList);

        // 5.返回结果
        UserOrderRes userOrderRes = new UserOrderRes();
        userOrderRes.setPayUrl(payUrl);
        userOrderRes.setOrderId(userOrder.getOrderId());
        return userOrderRes;
    }

    @Override
    public int changeOrderPaySuccess(String orderId, String paymentTransactionId, Date payTime) {
        UserOrder userOrder = new UserOrder();
        userOrder.setOrderId(orderId);
        userOrder.setStatus(UserOrderStatusVO.PAY_SUCCESS.getCode());
        userOrder.setPayTime(payTime);
        userOrder.setPaymentTransactionId(paymentTransactionId);
        userOrder.setUpdateTime(new Date());
        int result = userOrderDao.updateOrderPaySuccess(userOrder);

        eventBus.post(JSONObject.toJSONString(userOrder));

        return result;

    }

    @Override
    public List<String> queryNoPayNotifyOrder() {
        return userOrderDao.queryNoPayNotifyOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return userOrderDao.queryTimeoutCloseOrderList();
    }

    @Override
    public boolean changeOrderClose(String userOrderId) {
        UserOrder userOrder = new UserOrder();
        userOrder.setOrderId(userOrderId);
        userOrder.setStatus(UserOrderStatusVO.CLOSE.getCode());
        userOrder.setUpdateTime(new Date());

        int result = userOrderDao.changeOrderClose(userOrder);

        return result == 1;
    }

    private static List<OrderItem> calculateOrderItems(List<Product> productList, Map<String, Integer> collect,
                                                       String orderId) {
        return productList.stream().map(product -> {
            OrderItem orderItem = new OrderItem();
            Integer quantity = collect.get(product.getProductId());
            orderItem.setOrderId(orderId);
            orderItem.setProductId(product.getProductId());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(quantity);
            orderItem.setProductPrice(product.getPrice());
            orderItem.setTotalPrice(product.getPrice() * quantity);
            orderItem.setCreateTime(new Date());
            orderItem.setUpdateTime(new Date());
            return orderItem;
        }).toList();
    }

    private void saveOrderAndItems(UserOrder userOrder, List<OrderItem> orderItemList) {
        userOrderDao.insert(userOrder);
        if (!orderItemList.isEmpty()) {
            orderItemDao.batchInsert(orderItemList);
        }
    }

    private static UserOrder createUserOrder(ShopCartReq shopCartReq, String orderId, double totalAmount) {
        UserOrder userOrder = new UserOrder();
        userOrder.setOrderId(orderId);
        userOrder.setUserId(shopCartReq.getUserId());
        userOrder.setTotalAmount(totalAmount);
        userOrder.setPaidAmount(totalAmount);
        userOrder.setOrderTime(new Date());
        userOrder.setStatus(UserOrderStatusVO.CREATE.getCode());
        userOrder.setCreateTime(new Date());
        userOrder.setUpdateTime(new Date());
        return userOrder;
    }

    private String doPrepareOrder(UserOrder userOrder, List<Product> productList) throws AlipayApiException {

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);

        String productNameList = productList.stream().map(Product::getName).collect(Collectors.joining(","));

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", userOrder.getOrderId());
        bizContent.put("total_amount", userOrder.getTotalAmount());
        bizContent.put("subject", productNameList);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        String form = alipayClient.pageExecute(request).getBody();

        // 更新支付订单状态
        userOrder.setStatus(UserOrderStatusVO.PAY_WAIT.getCode());
        userOrder.setPayUrl(form);

        userOrderDao.updateOrderPayInfo(userOrder);

        return form;
    }


}
