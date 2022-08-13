package com.emard.batch.reader;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emard.batch.model.Product;
import com.emard.batch.service.ProductService;

@Component
public class ProductServiceAdapter implements InitializingBean {

    @Autowired
    private ProductService service;

    private List<Product> products;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        this.products = service.getProducts();
    }

    public Product nextProduct(){
        //eviter la boucle infinie
        if ( products.size() >0){
            return products.remove(0);
        }else
            return null;

    }

    /*public List<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }*/
}

