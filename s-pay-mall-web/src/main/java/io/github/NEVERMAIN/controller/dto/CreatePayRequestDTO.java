package io.github.NEVERMAIN.controller.dto;

import io.github.NEVERMAIN.domain.req.ShopCartItem;
import lombok.Data;

import java.util.List;

@Data
public class CreatePayRequestDTO {

    // 用户ID 【实际产生中会通过登录模块获取，不需要透彻】
    private String userId;
    // 产品编号
    private List<ShopCartItem> shopCartItemList;

}