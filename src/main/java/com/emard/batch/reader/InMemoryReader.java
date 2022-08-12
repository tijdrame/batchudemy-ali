package com.emard.batch.reader;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.stereotype.Component;

@Component
public class InMemoryReader extends AbstractItemStreamItemReader<Integer> {
    List<Integer> myList = new ArrayList<>();
    Integer index = 0;

    public InMemoryReader() {
        for (int i = 1; i < 11; i++) {
            myList.add(i);
        }
    }

    @Override
    public Integer read() throws Exception, UnexpectedInputException, 
    ParseException, NonTransientResourceException {
        Integer nextItem = null;
        if(index < myList.size()) {
            nextItem = myList.get(index);
            index++;
        }else {
            index =0;
        }
        return nextItem;
    }

}
