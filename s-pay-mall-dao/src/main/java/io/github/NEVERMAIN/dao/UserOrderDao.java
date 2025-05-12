package io.github.NEVERMAIN.dao;

import io.github.NEVERMAIN.domain.po.UserOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserOrderDao {

    int insert(UserOrder userOrder);

    UserOrder queryUnPayOrder(UserOrder userOrder );

    int updateOrderPayInfo(UserOrder userOrder);

    int updateOrderPaySuccess(UserOrder userOrder);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    int changeOrderClose(UserOrder userOrder);
}
