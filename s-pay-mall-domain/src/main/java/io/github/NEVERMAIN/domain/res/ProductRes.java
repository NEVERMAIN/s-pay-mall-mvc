package io.github.NEVERMAIN.domain.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRes {

    private Long id;
    private String productId;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private Integer status;
}
