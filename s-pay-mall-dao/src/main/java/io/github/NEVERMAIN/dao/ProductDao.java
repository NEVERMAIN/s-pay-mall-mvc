package io.github.NEVERMAIN.dao;

import io.github.NEVERMAIN.domain.po.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductDao {

    int insert(Product product);

    /**
     * 通过 Id 查询产品
     * @param productIds
     * @return
     */
    List<Product> queryProductByIds(List<String> productIds);

    List<Product> queryAllProducts();

}
