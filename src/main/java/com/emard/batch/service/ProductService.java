package com.emard.batch.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.emard.batch.model.Product;

@Service
public class ProductService {

    public List<Product> getProducts(){
        String url = "http://localhost:8081/api/products";
        RestTemplate restTemplate = new RestTemplate();
        Product[] products = restTemplate.getForObject(url, Product[].class);
        List<Product> list =new ArrayList<>();
        for (Product product : products) {
            list.add(product);
        }
        return list;
    }
}
