package com.emard.batch.writter;

import java.util.List;

import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.stereotype.Component;

@Component
public class ConsoleItemWritter extends AbstractItemStreamItemWriter<Integer>{

    @Override
    public void write(List<? extends Integer> items) throws Exception {
        //items.stream().forEach(System.out::println);   
        items.forEach(System.out::println);
        System.out.println("************** Writting each chunk **************");
    }
    
}
