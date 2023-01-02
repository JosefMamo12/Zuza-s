package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Class to pull items from database and display them into a view holder.
 * Only one category is loaded each time.
 */
public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MenuItemHolder>
{
    public ArrayList<MenuItemModel> menuItemModels;
    Context c;
    public MenuItemAdapter(ArrayList<MenuItemModel> menuItemModels, Context c)
    {
        this.menuItemModels = menuItemModels;
        this.c = c;
    }

    /**
     * Class for a single item to load into the view holder.
     */
    public static class MenuItemHolder extends RecyclerView.ViewHolder
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
        // Binds XML items to current activity. (Menu page)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_single_item_layout, parent, false);
        return new MenuItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemHolder holder, int position)
    {
        // Get item parameters.
        MenuItemModel currentItem = menuItemModels.get(position);
        View addCart =  holder.constraintLayout.findViewById(R.id.addToCart);
        addCart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                updateCart(currentItem);
            }
        });
        // Can be changed to get an image based on item's name
        holder.imageView.setImageResource(R.drawable.z_logo);
        holder.name.setText(currentItem.getName());
        holder.desc.setText(currentItem.getDesc());
        holder.price.setText(currentItem.getPrice());


    }

    private void updateCart(MenuItemModel item)
    {
        if (item == null)
            return;

        final String userID = FirebaseAuth.getInstance().getUid();


        if (userID == null)
        {
            Toast.makeText(this.c, "Please login to order.", Toast.LENGTH_SHORT).show();
            return;
        }

        Query cart = FirebaseDatabase.getInstance()
                .getReference().child("Carts")
                .orderByChild("userID").equalTo(userID);

        try // Try to get cart and update it.
        {
            cart.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    String key = null;
                    Cart data = null;
                    for (DataSnapshot d : snapshot.getChildren())
                    {
                        data = d.getValue(Cart.class);
                        key = d.getKey();
                        break;
                    }

                    if (data == null || key == null)
                    {
                        initCart(item, cart, userID);
                        return;
                    }

                    ArrayList<MenuItemModel> temp = data.getItems();

                    if (temp == null)
                    {
                        temp = new ArrayList<>();
                    }
                    temp.add(item);
                    double price = Double.parseDouble(item.getPrice()) + data.price;
                    Cart updated = new Cart(temp, data.count + 1, price, data.userID);

                    FirebaseDatabase.getInstance().getReference().child("Carts").
                            child(key)
                            .setValue(updated);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        catch (NullPointerException e) // No cart, make new one
        {
            initCart(item, cart, userID);
        }

        catch (Exception e) // Unexpected error
        {
            e.printStackTrace();
        }

        String message = " הוסף בהצלחה";
        message = item.getName() + message;
        Toast.makeText(this.c, message, Toast.LENGTH_SHORT).show();
    }

    private void initCart(MenuItemModel item, Query cart, String userID)
    {
        ArrayList<MenuItemModel> temp = new ArrayList<>();
        temp.add(item);
        double price = Double.parseDouble(item.getPrice());

        Cart updated = new Cart(temp, 1, price, userID);
        cart.getRef().push().setValue(updated);
    }


    @Override
    public int getItemCount() {
        return menuItemModels.size();
    }
}