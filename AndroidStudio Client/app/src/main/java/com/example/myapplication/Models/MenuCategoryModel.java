package com.example.myapplication.Models;

public class MenuCategoryModel
{
    private int image;
    private String text;



    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MenuCategoryModel(int image, String text) {
        this.image = image;
        this.text = text;
    }
}
