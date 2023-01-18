package com.example.myapplication.Models;

import java.util.ArrayList;

public class Order {
    private ArrayList<MenuItemModel> items;
    private int count;
    private double price;
    private String userID;
    private long order_time;
    private long complete_time;
    private boolean complete;


    public Order() {
    }


    public Order(Cart cart, long timeStamp) {
        this.items = cart.getItems();
        this.count = cart.getCount();
        this.price = cart.getPrice();
        this.userID = cart.getUserID();
        this.order_time = timeStamp;
        this.complete_time = 0;
        this.complete = false;
    }

    @Override
    public String toString() {
        String str = "Order{" +
                "userID='" + userID + '\'' +
                "items=" + items +
                ", count=" + count +
                ", price=" + price +
                ", ordered on=" + getOrder_time();
        if (complete)
            str += ", completed on=" + getComplete_time() + '}';
        else
            str += ", completed on= 0 }";

        return str;
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

    public void setOrder_time(long order_time) {
        this.order_time = order_time;
    }

    public long getOrder_time() {
        return order_time;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setComplete_time(long complete_time) {
        this.complete_time = complete_time;
    }

    public long getComplete_time() {
        return complete_time;
    }
}


