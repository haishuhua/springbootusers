package com.techprimers.db.model;

public class Weather {

    long time;

    String condition;

    public Weather(long time, String condition) {
        this.time = time;
        this.condition = condition;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
