package com.example.myapplication;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class CartPage extends AppCompatActivity implements View.OnClickListener, UpdateMenuRecyclerView
{

    private ImageView logInImg;
    private ImageView accountImg;
    private ImageView menuFoodImg;
    private ImageView mangerReport;
    private TextView amount;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    RecyclerView cartItemsRecyc;
    CartPageAdapter cartAdapter;

    ArrayList<MenuItemModel> items;
    HashMap<String, Integer> itemsCount;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getUid() == null) return;
        database = FirebaseDatabase.getInstance();
        setContentView(R.layout.activity_shopping_cart);
        navBarInitializer();
        checkIfConnected();
        checkIfAdminConnected();

        items = new ArrayList<>();
        itemsCount = new HashMap<>();

        amount = findViewById(R.id.CartAmount);
        amount.setEnabled(false);
        amount.setClickable(false);

        Context temp_ctx = this;
        updateData(temp_ctx);
    }
    private void updateCartSum(double sum)
    {
        String price = "לתשלום: " + sum;
        amount.setText(price);
    }
    private void updateData(Context temp_ctx)
    {
        database.getReference().child("Carts").orderByChild("userID").equalTo(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        for (DataSnapshot data : snapshot.getChildren())
                        {
                            Cart c = data.getValue(Cart.class);

                            if (c != null)
                            {
                                String price = "לתשלום: " + c.getPrice();
                                amount.setText(price);
                                for (MenuItemModel item : c.getItems())
                                {
                                    if (itemsCount.containsKey(item.getName()))
                                    {
                                        itemsCount.put(item.getName(), itemsCount.get(item.getName()) + 1);
                                    }
                                    else
                                    {
                                        itemsCount.put(item.getName(), 1);
                                        items.add(item);
                                    }
                                }
                            }



                            cartItemsRecyc = findViewById(R.id.cart_items);
                            cartAdapter = new CartPageAdapter(items, itemsCount, temp_ctx);
                            cartItemsRecyc.setLayoutManager(new LinearLayoutManager(temp_ctx, LinearLayoutManager.VERTICAL, false));
                            cartItemsRecyc.setAdapter(cartAdapter);
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkIfAdminConnected() {
        if (mAuth.getCurrentUser() == null) {
            mangerReport.setVisibility(View.INVISIBLE);
            return;
        }
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = database.getReference("Users");
        DatabaseReference childRef = userRef.child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                if (user.isAdmin()) {
                    mangerReport.setVisibility(View.VISIBLE);

                } else {
                    mangerReport.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        childRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public void navBarInitializer() {
        mAuth = FirebaseAuth.getInstance();
        /* Image views of navBar*/
        mangerReport = (ImageView) findViewById(R.id.report);
        menuFoodImg = (ImageView) findViewById(R.id.menuFoodImg);
        logInImg = (ImageView) findViewById(R.id.logInImg);
        accountImg = (ImageView) findViewById(R.id.accountImg);
        ImageView logOutImg = (ImageView) findViewById(R.id.logOutImg);
        ImageView homePageImg = (ImageView) findViewById(R.id.homeImg);
        /* OnClick Listeners  */
        logInImg.setOnClickListener(this);
        accountImg.setOnClickListener(this);
        logOutImg.setOnClickListener(this);
        homePageImg.setOnClickListener(this);
        menuFoodImg.setOnClickListener(this);
        mangerReport.setOnClickListener(this);

    }

    private void checkIfConnected() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            logInImg.setVisibility(View.INVISIBLE);
            accountImg.setVisibility(View.VISIBLE);
        } else {
            logInImg.setVisibility(View.VISIBLE);
            accountImg.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menuFoodImg:
                startActivity(new Intent(this, MenuPage.class));
                break;
            case R.id.homeImg:
                finish();
                checkIfConnected();
                startActivity(new Intent(this, HomePage.class));
                break;
            case R.id.logInImg:
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.logOutImg:
                if (mAuth.getCurrentUser() != null) {
                    mAuth.signOut();
                    checkIfAdminConnected();
                    checkIfConnected();
                    finish();
                } else {
                    Toast.makeText(this, "Please login before logout", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.accountImg:
                startActivity(new Intent(getApplicationContext(), Profile.class));
                break;
        }
    }

    @Override
    public void callback(int position, ArrayList<MenuItemModel> items)
    {
        cartAdapter = new CartPageAdapter(items, itemsCount, this);
        cartAdapter.notifyItemInserted(position);
        cartItemsRecyc.setAdapter(cartAdapter);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    // ADAPTER

    static class CartPageAdapter extends RecyclerView.Adapter<CartPageAdapter.CartItemHolder>
    {
        Context c;
        CartPage cartPage;
        HashMap<String, Integer> itemsCount;
        ArrayList<MenuItemModel> items;

        public CartPageAdapter(ArrayList<MenuItemModel> items, HashMap<String, Integer> itemsCount, Context c)
        {
            this.items = items;
            this.itemsCount = itemsCount;
            this.c = c;
            cartPage = (CartPage) c;
        }

        @NonNull
        @Override
        public CartItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_single_item, parent, false);
            return new CartPageAdapter.CartItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CartPageAdapter.CartItemHolder holder, int position)
        {
            View DelOne =  holder.constraintLayout.findViewById(R.id.CartDelOne);
            View DelAll =  holder.constraintLayout.findViewById(R.id.CartDelAll);

            // Get item parameters.
            MenuItemModel currentItem = items.get(position);
            int pos = holder.getAdapterPosition();
            int amount = itemsCount.get(currentItem.getName());
            String amountText = "כמות "+amount;

            // Can be changed to get an image based on item's name
//            holder.imageView.setImageResource(R.drawable.z_logo);
            holder.name.setText(currentItem.getName());
            holder.amount.setText(amountText);
            holder.price.setText(currentItem.getPrice());

            String id = FirebaseAuth.getInstance().getUid();
            Query myCart = FirebaseDatabase.getInstance().getReference().
                    child("Carts");


            DelOne.setOnClickListener(view -> myCart.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren())
                    {
                        Cart current = ds.getValue(Cart.class);

                        /*Search for cart matching our user id*/
                        if (current != null && Objects.equals(current.userID, id))
                        {
                            /*Remove item from cart and update*/
                            current.getItems().remove(currentItem);
                            double price = Double.parseDouble(currentItem.getPrice());
                            price = current.getPrice() - price;
                            current.setPrice(price);
                            current.setCount(current.getCount() - 1);
                            String key = ds.getKey();

                            /*If cart still has items, update it*/
                            if (current.getCount() > 0)
                            {
                                FirebaseDatabase.getInstance().getReference().child("Carts").
                                        child(key)
                                        .setValue(current);
                                cartPage.updateCartSum(price);
                            }
                            /*If no items, remove cart entirely*/
                            else
                            {
                                FirebaseDatabase.getInstance().getReference().child("Carts").
                                        child(key)
                                        .removeValue();
                                cartPage.updateCartSum(0);
                            }

                            /*If item was removed entirely, remove it from local memory and from binders*/
                            if (amount == 1)
                            {
                                itemsCount.remove(currentItem.getName());
                                items.remove(currentItem);
                                notifyItemRemoved(pos);
                            }
                            /*Only one of the item was removed, and there are still some left*/
                            else
                            {
                                itemsCount.put(currentItem.getName(), amount - 1);
                                notifyItemChanged(pos);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }));

            DelAll.setOnClickListener(view -> myCart.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren())
                    {
                        Cart current = ds.getValue(Cart.class);
                        if (current != null && Objects.equals(current.userID, id))
                        {
                            double price = 0;

                            /*Remove all occurrences of selected item*/
                            while (current.getItems().contains(currentItem))
                            {
                                current.getItems().remove(currentItem);
                                price = Double.parseDouble(currentItem.getPrice());
                                price = current.getPrice() - price;
                                current.setPrice(price);
                                current.setCount(current.getCount() - 1);
                            }

                            /*With current key, update cart*/
                            String key = ds.getKey();
                            if (current.getCount() > 0)
                            {
                                FirebaseDatabase.getInstance().getReference().child("Carts").
                                        child(key)
                                        .setValue(current);
                                cartPage.updateCartSum(price);
                            }
                            /*Or delete cart if all items are deleted*/
                            else
                            {
                                FirebaseDatabase.getInstance().getReference().child("Carts").
                                        child(key)
                                        .removeValue();
                                cartPage.updateCartSum(0);
                            }

                            /*Notify item deleted to binders*/
                            itemsCount.remove(currentItem.getName());
                            items.remove(currentItem);
                            notifyItemRemoved(pos);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }));

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        /**
         * Class for a single item to load into the view holder.
         */
        public static class CartItemHolder extends RecyclerView.ViewHolder
        {
//            public ImageView imageView;
            public TextView name;
            public TextView amount;
            public TextView price;
            ConstraintLayout constraintLayout;

            public CartItemHolder(@NonNull View itemView) {
                super(itemView);
//                imageView = itemView.findViewById(R.id.cart_zuza_logo);
                name = itemView.findViewById(R.id.CartItemName);
                amount = itemView.findViewById(R.id.CartItemsAmount);
                price = itemView.findViewById(R.id.CartItemsPrice);
                constraintLayout = itemView.findViewById(R.id.constraintLayout);
            }
        }
    }
}
