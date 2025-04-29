package io.github.NEVERMAIN.dao;

import io.github.NEVERMAIN.domain.po.UserOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserOrderDao {

    int insert(UserOrder userOrder);

    UserOrder queryUnPayOrder(UserOrder userOrder );

}
