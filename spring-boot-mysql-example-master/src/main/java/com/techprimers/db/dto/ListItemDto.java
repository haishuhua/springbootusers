package com.techprimers.db.dto;


import java.io.Serializable;

public class ListItemDto implements Serializable {


    private String fieldName;

    private Object fieldValue;

    public ListItemDto() {}

    public ListItemDto(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    public Object getFieldValue() {
        return this.fieldValue;
    }
}