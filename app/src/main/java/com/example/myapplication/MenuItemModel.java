package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

public class MenuItemModel {
    private String id;
    private String name;
    private String desc;
    private String price;
    private String imageUrl;

    public MenuItemModel() {
        // This is to avoid this error:
        //com.google.firebase.database.DatabaseException:
        // For having no empty constructor
    }

    public MenuItemModel(String name, String desc, String price) {
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.imageUrl = null;
    }


    public MenuItemModel(String id, String name, String desc, String price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "MenuItemModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", price='" + price + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItemModel that = (MenuItemModel) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(desc, that.desc) && Objects.equals(price, that.price) && Objects.equals(imageUrl, that.imageUrl);
    }


    public static DiffUtil.ItemCallback<MenuItemModel> itemCallback = new DiffUtil.ItemCallback<MenuItemModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull MenuItemModel oldItem, @NonNull MenuItemModel newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull MenuItemModel oldItem, @NonNull MenuItemModel newItem) {
            return oldItem.equals(newItem);
        }
    };
}
