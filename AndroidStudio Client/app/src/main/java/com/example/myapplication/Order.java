package com.example.myapplication;

import java.util.ArrayList;
import java.util.Date;

public class Order {
    private ArrayList<MenuItemModel> items;
    private int count;
    private double price;
    private String userID;
    private Date order_time;
    private Date complete_time;
    private boolean complete;


    public Order() {
    }


    public Order(Cart cart, Date timeStamp) {
        this.items = cart.getItems();
        this.count = cart.getCount();
        this.price = cart.getPrice();
        this.userID = cart.getUserID();
        this.order_time = timeStamp;
        this.complete_time = null;
        this.complete = false;
    }

    @Override
    public String toString() {
        return "Order{" +
                "items=" + items +
                ", count=" + count +
                ", price=" + price +
                ", ordered on=" + order_time +
                ", completed on=" + complete_time + //if not completed yet it's null
                ", userID='" + userID + '\'' +
                '}';
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<MenuItemModel> getItems() {
        return items;
    }

    public Date getOrder_time() {
        return order_time;
    }

    public void setOrder_time(Date order_time) {
        this.order_time = order_time;
    }

    public void setItems(ArrayList<MenuItemModel> items) {
        this.items = items;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Date getComplete_time() {
        return complete_time;
    }

    public void setComplete_time(Date complete_time) {
        this.complete_time = complete_time;
    }
}


