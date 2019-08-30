package com.example.cube.Components;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order {
    private int order_num;
    private List<HashMap<String, Object>> order_list;
    private Date order_time;
    private boolean standby;
    private boolean called;
    private boolean written;
    private String user;

    public Order(){}

    public Order(int order_num,
                 List<HashMap<String, Object>> order_list,
                 Date order_time,
                 boolean standby,
                 boolean called,
                 boolean written,
                 String user) {
        this.order_num = order_num;
        this.order_list = order_list;
        this.order_time = order_time;
        this.standby = standby;
        this.called = called;
        this.written = written;
        this.user = user;
    }

    @Override
    public String toString() {
        return "Order{" +
                "order_num=" + order_num +
                ", order_list=" + order_list +
                ", order_time=" + order_time +
                ", standby=" + standby +
                ", called=" + called +
                ", written=" + written +
                ", user='" + user + '\'' +
                '}';
    }

    public boolean isWritten() {
        return written;
    }

    public void setWritten(boolean written) {
        this.written = written;
    }

    public int getOrder_num() {
        return order_num;
    }

    public void setOrder_num(int order_num) {
        this.order_num = order_num;
    }

    public List<HashMap<String, Object>> getOrder_list() {
        return order_list;
    }

    public void setOrder_list(List<HashMap<String, Object>> order_list) {
        this.order_list = order_list;
    }

    public Date getOrder_time() {
        return order_time;
    }

    public void setOrder_time(Date order_time) {
        this.order_time = order_time;
    }

    public boolean isStandby() {
        return standby;
    }

    public void setStandby(boolean standby) {
        this.standby = standby;
    }

    public boolean isCalled() {
        return called;
    }

    public void setCalled(boolean called) {
        this.called = called;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
