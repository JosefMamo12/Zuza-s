package com.example.myapplication;

import androidx.annotation.NonNull;

public class menuItem
{
    private String name;
//    private Image img;
    private String category;
    private double price;

    public menuItem()
    {

    }



    public menuItem(String name, /*Image img,*/ String category, double price) {
        setCategory(category);
        setName(name);
        setPrice(price);
    }

    public String getName() {
        return name;
    }

    public void setName(String name)
    {
        assert (!name.isEmpty());
        this.name = name;
    }

//    public Image getImg() {
//        return img;
//    }
//
//    public void setImg(Image img) {
//        this.img = img;
//    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category)
    {
        assert(!category.isEmpty());
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        assert (price >= 0);
        this.price = price;
    }

    @NonNull
    public String toString()
    {
        return "menuItem{" +
                "name=" + getName() +
                ", category=" + getCategory() +
                ", price=" + getPrice() +
                '}';
    }
}
