package com.techprimers.db.dto;


import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class HistoryDto<T> implements Serializable {

    @XmlElement
    String commandType;

    @XmlElement
    String modifiedBy;

    @XmlElement
    Date modifiedOn;

    @XmlAnyElement
    T entity;


    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public String getCommandType() {
        return this.commandType;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedBy() {
        return this.modifiedBy;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Date getModifiedOn() {
        return this.modifiedOn;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return this.entity;
    }

}