package com.techprimers.db.model;

import lombok.*;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
public class Item {
    private String name;
    private Double price;

}
