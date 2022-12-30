package com.example.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to pull categories from database and display them into a view holder.
 * Based on recent clicked, category will be highlighted and items displayed.
 */
public class MenuCategoryAdapter extends RecyclerView.Adapter<MenuCategoryAdapter.MenuCategoryViewHolder>
{

    private final ArrayList<MenuCategoryModel> categories;
    private ArrayList<MenuItemModel> items;

    // Implement data display updater.
    UpdateMenuRecyclerView updateMenuRecyclerView;
    Activity activity;
    boolean firstViewed = true;
    int selectedPos = -1;

    public MenuCategoryAdapter(ArrayList<MenuCategoryModel> items, Activity activity, UpdateMenuRecyclerView updateMenuRecyclerView)
    {
        this.categories = items;
        this.activity = activity;
        this.updateMenuRecyclerView = updateMenuRecyclerView;
    }

    @NonNull
    @Override
    public MenuCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // Binds XML categories to current activity. (Menu page)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_category_item, parent, false);
        return new MenuCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuCategoryViewHolder holder, int position)
    {
        // Click position
        int pos = holder.getAdapterPosition();
        MenuCategoryModel currentItem = categories.get(pos);
        holder.imageView.setImageResource(currentItem.getImage());
        holder.textView.setText(currentItem.getText());
        holder.itemView.setSelected(selectedPos == position);

        // Highlight current selected category.
        if (pos == selectedPos)
            holder.linearLayout.setBackgroundResource(R.drawable.menu_category_selected_bg);
        else
            holder.linearLayout.setBackgroundResource(R.drawable.menu_category_bg);

        // Once viewed, remove "press me" button.
        if (firstViewed)
        {
            categories.remove(0);
            updateData(currentItem, pos);
            updateMenuRecyclerView.callback(pos, items);
            firstViewed = false;
        }

        // Dump category items into screen.
        holder.linearLayout.setOnClickListener(view ->
        {
            notifyItemChanged(selectedPos);
            selectedPos = pos;
            notifyItemChanged(selectedPos);
            updateData(currentItem, pos);
        });
    }

    /** Loads all items from a given category and updates screen.
     *
     * @param currentCategory category to load into screen from database.
     * @param pos position to use callback on.
     */
    private void updateData(MenuCategoryModel currentCategory, int pos)
    {
        items = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("menuItems").child(currentCategory.getText());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot child : snapshot.getChildren())
                {
                    // Check if category has no values (empty)
                    if (child.getValue() instanceof String)
                        continue;

                    try
                    {
                        Map<String,String> td=(HashMap<String, String>)child.getValue();
                        assert td != null;
                        String price = td.get("price");
                        String name = td.get("name");
                        String desc = td.get("desc");
                        String url = td.get("imageUrl");
                        MenuItemModel singleItem = new MenuItemModel(name, desc, price);
                        items.add(singleItem);

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                updateMenuRecyclerView.callback(pos, items);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Data read from menu failed, err code: " + error.getCode());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    /**
     * Class for a single category to load into the view holder.
     */
    public static class MenuCategoryViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView;
        ImageView imageView;
        LinearLayout linearLayout;

        public MenuCategoryViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.text);
            linearLayout = itemView.findViewById(R.id.linearLayoutMenuItem);
        }
    }
}
