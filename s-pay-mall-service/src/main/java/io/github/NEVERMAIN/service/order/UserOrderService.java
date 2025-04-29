package io.github.NEVERMAIN.service.order;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserOrderService implements IUserOrderService {

    private static final Logger log = LoggerFactory.getLogger(UserOrderService.class);
    private UserOrderDao userOrderDao;

    private OrderItemDao orderItemDao;

    private ProductDao productDao;

    public UserOrderService(UserOrderDao userOrderDao, OrderItemDao orderItemDao, ProductDao productDao) {
        this.userOrderDao = userOrderDao;
        this.orderItemDao = orderItemDao;
        this.productDao = productDao;
    }

    @Override
    @Transactional
    public UserOrderRes createOrder(ShopCartReq shopCartReq) {

        UserOrder userOrderReq = new UserOrder();
        userOrderReq.setUserId(shopCartReq.getUserId());

        UserOrder unpaidOrder = userOrderDao.queryUnPayOrder(userOrderReq);
        if (unpaidOrder != null) {
            if (UserOrderStatusVO.PAY_WAIT.getCode().equals(unpaidOrder.getStatus()) && unpaidOrder.getPayUrl() != null) {
                UserOrderRes userOrderRes = new UserOrderRes();
                BeanUtils.copyProperties(unpaidOrder, userOrderRes);
                return userOrderRes;
            }
            if (UserOrderStatusVO.CREATE.getCode().equals(unpaidOrder.getStatus()) && unpaidOrder.getPayUrl() == null) {
                // todo 创建支付单
            }
        }

        // 创建订单
        List<ShopCartItem> shopCartItems = shopCartReq.getShopCartItems();
        List<String> productIds = shopCartItems.stream().map(ShopCartItem::getProductId).toList();
        // 将 shopCartItem 转成一个 map 结构 productId -> quantity
        Map<String, Integer> collect = shopCartItems.stream().collect(Collectors.toMap(ShopCartItem::getProductId, ShopCartItem::getQuantity));

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

        // 5.返回结果
        UserOrderRes userOrderRes = new UserOrderRes();
        BeanUtils.copyProperties(userOrder, userOrderRes);

        return userOrderRes;
    }

    private static List<OrderItem> calculateOrderItems(List<Product> productList, Map<String, Integer> collect, String orderId) {
        List<OrderItem> orderItemList = productList.stream().map(product -> {
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
        return orderItemList;
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


}
