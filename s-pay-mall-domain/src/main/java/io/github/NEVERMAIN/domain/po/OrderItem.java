package io.github.NEVERMAIN.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private Long id;
    private String orderId;
    private String productId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double totalPrice;
    private Date createTime;
    private Date updateTime;

}
