package io.github.NEVERMAIN.service;

import com.alipay.api.AlipayApiException;
import io.github.NEVERMAIN.domain.po.OrderItem;
import io.github.NEVERMAIN.domain.po.User;
import io.github.NEVERMAIN.domain.po.UserOrder;
import io.github.NEVERMAIN.domain.req.ShopCartReq;
import io.github.NEVERMAIN.domain.res.UserOrderRes;

import java.util.Date;
import java.util.List;

public interface IUserOrderService {

    UserOrderRes createOrder(ShopCartReq shopCartReq) throws AlipayApiException;

    int changeOrderPaySuccess(String orderId, String paymentTransactionId, Date payTime);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String userOrderId);
}
