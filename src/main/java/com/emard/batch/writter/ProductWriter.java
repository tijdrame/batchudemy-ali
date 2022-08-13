package com.emard.batch.writter;

import java.util.List;

import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.stereotype.Component;

import com.emard.batch.model.Product;

@Component
public class ProductWriter extends AbstractItemStreamItemWriter<Product>{

    @Override
    public void write(List<? extends Product> items) throws Exception {
        //items.stream().forEach(System.out::println);   
        items.forEach(System.out::println);
        System.out.println("************** Writting each chunk **************");
    }
    
}
