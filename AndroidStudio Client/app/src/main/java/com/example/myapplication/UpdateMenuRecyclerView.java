package com.example.myapplication;

import com.example.myapplication.Models.MenuItemModel;

import java.util.ArrayList;

public interface UpdateMenuRecyclerView
{
    /**
     *
     */
    void callback(int position, ArrayList<MenuItemModel> items);
}
