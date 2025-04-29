package io.github.NEVERMAIN.dao;

import io.github.NEVERMAIN.domain.po.OrderItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderItemDao {

    /**
     * 批量插入订单项
     * @param orderItemList
     * @return
     */
    int batchInsert(List<OrderItem> orderItemList);

}
