package io.github.NEVERMAIN.service;

import com.alibaba.fastjson2.JSONObject;
import io.github.NEVERMAIN.domain.req.ShopCartItem;
import io.github.NEVERMAIN.domain.req.ShopCartReq;
import io.github.NEVERMAIN.domain.res.UserOrderRes;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ApiTest {

    private static final Logger log = LoggerFactory.getLogger(ApiTest.class);
    @Resource
    private IUserOrderService userOrderService;

    @Test
    public void test_createOrder() {
        List<String> orderIds = List.of("150588119613", "650668483292");
        ShopCartReq shopCartReq = new ShopCartReq();
        ArrayList<ShopCartItem> shopCartItems = new ArrayList<>();
        shopCartReq.setShopCartItems(shopCartItems);
        shopCartReq.setUserId("10086");
        shopCartItems.add(new ShopCartItem("150588119613", 1));
        shopCartItems.add(new ShopCartItem("650668483292", 1));

        UserOrderRes order = userOrderService.createOrder(shopCartReq);

        log.info("{}", JSONObject.toJSONString(order));
    }


}
