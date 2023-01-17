package com.example.myapplication.Models;

import com.example.myapplication.Models.MenuItemModel;

import java.util.ArrayList;

public class Cart
{
    private ArrayList <MenuItemModel> items;
    private int count;
    private double price;
    private String userID;

    public Cart(){}

    public Cart(ArrayList<MenuItemModel> items, int count, double price, String uid)
    {
        this.items = items;
        this.count = count;
        this.price = price;
        this.userID = uid;
    }


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
