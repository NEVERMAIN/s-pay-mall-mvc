package io.github.NEVERMAIN.domain.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopCartItem {

    private String productId;

    private Integer quantity;

}
