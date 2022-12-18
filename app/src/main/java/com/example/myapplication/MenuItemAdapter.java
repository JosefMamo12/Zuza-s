package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MenuItemHolder>
{
    public ArrayList<MenuItemModel> menuItemModels;

    public MenuItemAdapter(ArrayList<MenuItemModel> menuItemModels)
    {
        this.menuItemModels = menuItemModels;
    }
    public class MenuItemHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageView;
        public TextView name;
        public TextView desc;
        public TextView price;
        ConstraintLayout constraintLayout;

        public MenuItemHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.menuItemName);
            desc = itemView.findViewById(R.id.menuItemsDetails);
            price = itemView.findViewById(R.id.menuItemsPrice);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }

    @NonNull
    @Override
    public MenuItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_single_item_layout, parent, false);
        return new MenuItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemHolder holder, int position)
    {
        MenuItemModel currentItem = menuItemModels.get(position);
        holder.imageView.setImageResource(R.drawable.z_logo);
        holder.name.setText(currentItem.getName());
        holder.desc.setText(currentItem.getDesc());
        holder.price.setText(currentItem.getPrice());
    }

    @Override
    public int getItemCount() {
        return menuItemModels.size();
    }
}