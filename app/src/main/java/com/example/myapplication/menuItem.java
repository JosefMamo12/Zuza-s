package com.example.myapplication;

import androidx.annotation.NonNull;

public class menuItem
{
    private String name;
//    private Image img;
    private String category;

    public menuItem()
    {

    }

    public menuItem(String name, /*Image img,*/ String category) {
        this.name = name;
//        this.img = img;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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

    public void setCategory(String category) {
        this.category = category;
    }

    @NonNull
    public String toString()
    {
        return name + " from " + category;
    }
}
