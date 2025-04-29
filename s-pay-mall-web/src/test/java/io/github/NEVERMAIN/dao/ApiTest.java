package io.github.NEVERMAIN.dao;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.github.NEVERMAIN.domain.po.OrderItem;
import io.github.NEVERMAIN.domain.po.Product;
import io.github.NEVERMAIN.domain.po.UserOrder;
import io.netty.util.internal.SuppressJava6Requirement;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
public class ApiTest {

    private static final Logger log = LoggerFactory.getLogger(ApiTest.class);
    @Resource
    private UserOrderDao userOrderDao;

    @Resource
    private ProductDao productDao;

    @Resource
    private OrderItemDao orderItemDao;


    @Test
    public void test_insert_userOrder(){
        UserOrder userOrder = new UserOrder();
        userOrder.setOrderId(RandomStringUtils.randomNumeric(12));
        userOrder.setPaymentTransactionId(RandomStringUtils.randomNumeric(12));
        userOrder.setUserId(RandomStringUtils.randomNumeric(12));
        userOrder.setOrderTime(new Date());
        userOrder.setTotalAmount(0.0D);
        userOrder.setPaidAmount(0.0D);
        userOrder.setStatus("create");
        userOrder.setPayUrl("");
        userOrder.setPayTime(new Date());
        userOrder.setCancelTime(new Date());
        userOrder.setFinishTime(new Date());
        userOrder.setCreateTime(new Date());
        userOrder.setUpdateTime(new Date());

        userOrderDao.insert(userOrder);
    }


    @Test
    public void test_insert_product(){
        Product product = new Product();
        product.setProductId(RandomStringUtils.randomNumeric(12));
        product.setName("产品03");
        product.setDescription("这是产品03");
        product.setPrice(1.0D);
        product.setStock(10);
        product.setImageUrl("");
        product.setStatus(0);
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());

        productDao.insert(product);
    }


    @Test
    public void test_queryAllProduct(){
        List<Product> products = productDao.queryAllProducts();
        // [
        // {"createTime":"2025-04-27 00:00:00","description":"","id":1,"imageUrl":"","name":"","price":0.0,"productId":"150588119613","status":0,"stock":0,"updateTime":"2025-04-27 00:00:00"},
        // {"createTime":"2025-04-29 00:00:00","description":"这是产品02","id":2,"imageUrl":"","name":"产品02","price":1.0,"productId":"406980200734","status":0,"stock":10,"updateTime":"2025-04-29 00:00:00"},
        // {"createTime":"2025-04-29 00:00:00","description":"这是产品03","id":3,"imageUrl":"","name":"产品03","price":1.0,"productId":"650668483292","status":0,"stock":10,"updateTime":"2025-04-29 00:00:00"}
        // ]
        log.info("{}", JSONObject.toJSONString(products));
    }

    @Test
    public void test_queryByIds(){
        List<Product> productList = productDao.queryProductByIds(List.of("150588119613", "650668483292"));
        log.info("{}", JSONObject.toJSONString(productList));
    }

    @Test
    public void test_bath_insert_orderItem(){
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(RandomStringUtils.randomNumeric(12));
        orderItem.setProductId(RandomStringUtils.randomNumeric(12));
        orderItem.setProductName("产品02");
        orderItem.setProductPrice(1.0D);
        orderItem.setQuantity(1);
        orderItem.setTotalPrice(1.0D);
        orderItem.setCreateTime(new Date());
        orderItem.setUpdateTime(new Date());

        OrderItem orderItem02 = new OrderItem();
        orderItem02.setOrderId(RandomStringUtils.randomNumeric(12));
        orderItem02.setProductId(RandomStringUtils.randomNumeric(12));
        orderItem02.setProductName("产品02");
        orderItem02.setProductPrice(1.0D);
        orderItem02.setQuantity(1);
        orderItem02.setTotalPrice(1.0D);
        orderItem02.setCreateTime(new Date());
        orderItem02.setUpdateTime(new Date());

        List<OrderItem> orderItemList = List.of(orderItem, orderItem02);

        orderItemDao.batchInsert(orderItemList);

    }


}
