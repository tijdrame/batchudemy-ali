package com.emard.batch.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.emard.batch.model.Product;

@Component
public class ProductMapper implements RowMapper<Product>{

    @Override
    public Product mapRow(ResultSet rs, int arg1) throws SQLException {
        Product product = new Product();
        return product
        .productID(rs.getInt("product_id"))
        .productName(rs.getString("product_name"))
        .productDesc(rs.getString("product_desc"))
        .price(rs.getBigDecimal("price"))
        .unit(rs.getInt("unit"))
        ;
    }
    
}
