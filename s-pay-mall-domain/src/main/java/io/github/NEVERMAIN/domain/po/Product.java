package io.github.NEVERMAIN.domain.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    private Long id;
    private String productId;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
