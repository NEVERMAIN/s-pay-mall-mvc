package io.github.NEVERMAIN.service;

import com.alipay.api.AlipayApiException;
import io.github.NEVERMAIN.domain.po.OrderItem;
import io.github.NEVERMAIN.domain.po.UserOrder;
import io.github.NEVERMAIN.domain.req.ShopCartReq;
import io.github.NEVERMAIN.domain.res.UserOrderRes;

import java.util.List;

public interface IUserOrderService {

    UserOrderRes createOrder(ShopCartReq shopCartReq) throws AlipayApiException;
}
