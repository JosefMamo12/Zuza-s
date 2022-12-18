package com.example.myapplication;

public class MenuItemModel
{
    private String name;
    private String desc;
    private String price;

    public MenuItemModel()
    {
        // This is to avoid this error:
        //com.google.firebase.database.DatabaseException:
        // For having no empty constructor
    }

    public MenuItemModel(String name, String desc, String price)
    {
        this.name = name;
        this.desc = desc;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
