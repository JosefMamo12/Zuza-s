package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartModel {

    private List<MenuItemModel> items;
    private double totalCost;

    public ShoppingCartModel() {
        items = new ArrayList<>();
        totalCost = 0;
    }

    public void addItem(MenuItemModel item) {
        items.add(item);
        totalCost += Double.parseDouble(item.getPrice());
    }

    public void removeItem(MenuItemModel item) {
        items.remove(item);
        totalCost -= Double.parseDouble(item.getPrice());
    }

    public List<MenuItemModel> getItems() {
        return items;
    }

    public double getTotalCost() {
        return totalCost;
    }
}