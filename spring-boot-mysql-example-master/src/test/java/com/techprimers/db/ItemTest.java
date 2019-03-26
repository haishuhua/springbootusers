package com.techprimers.db;

import com.techprimers.db.model.Item;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ItemTest {


    @Test
    public void testItemWithlombok() {
       Item item = Item.builder().name("name").price(10.00).build();
       assertEquals("name",item.getName());


    }
}
