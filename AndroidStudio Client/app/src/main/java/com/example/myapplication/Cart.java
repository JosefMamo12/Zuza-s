package com.example.myapplication;

import java.util.ArrayList;

public class Cart
{
    ArrayList <MenuItemModel> items;
    int count;
    double price;
    String userID;
//    double totalPrice;

    public Cart(){}

    public Cart(ArrayList<MenuItemModel> items, int count, double price, String uid)
    {
        this.items = items;
        this.count = count;
        this.price = price;
        this.userID = uid;
    }

//    public double getTotalPrice() {
//        return totalPrice;
//    }
//
//    public void setTotalPrice(double totalPrice) {
//        this.totalPrice = totalPrice;
//    }

    @Override
    public String toString() {
        return "Cart{" +
                "items=" + items +
                ", count=" + count +
                ", price=" + price +
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

}
