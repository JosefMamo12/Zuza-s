package com.example.myapplication;

import androidx.annotation.NonNull;

public class menuItem
{
    private String name;
    private String description;
//    private Image img;
    private String category;
    private double price;

    public menuItem()
    {

    }

    public menuItem(String name, String description, /*Image img,*/ String category, double price) {
        setCategory(category);
        setName(name);
        setPrice(price);
        setDescription(description);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    public String toString()
    {
        return "menuItem{" +
                "name=" + getName() +
                ", description=" + getDescription() +
                ", category=" + getCategory() +
                ", price=" + getPrice() +
                '}'+"\n";
    }
}
