package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class OrdersPage extends AppCompatActivity implements View.OnClickListener, UpdateMenuRecyclerView
    {

        private ImageView logInImg;
        private ImageView accountImg;
        private ImageView shoppingCart;
        private ImageView mangerReport;
        private TextView totalPrice;
        private Cart cart;

        FirebaseAuth mAuth;
        FirebaseDatabase database;
        RecyclerView cartItemsRecyc;
        com.example.myapplication.CartPage.CartPageAdapter cartAdapter;



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
            totalPrice = findViewById(R.id.CartAmount);
            navBarInitializer();
            checkIfConnected();
            checkIfAdminConnected();
            items = new ArrayList<>();
            itemsCount = new HashMap<>();

            Context temp_ctx = this;
            updateData(temp_ctx);
        }

        private void updateData(Context temp_ctx)
        {
            database.getReference().child("Orders").orderByChild("userID").equalTo(mAuth.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            for (DataSnapshot data : snapshot.getChildren())
                            {
                                cart = data.getValue(Cart.class);

                                if (cart != null && cart.getCount()>0)
                                {
                                    for (MenuItemModel item : cart.getItems())
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
                                cartAdapter = new com.example.myapplication.CartPage.CartPageAdapter(items, itemsCount, temp_ctx,cart,totalPrice);
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
                shoppingCart.setVisibility(View.VISIBLE);
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
                        shoppingCart.setVisibility(View.INVISIBLE);
                        mangerReport.setVisibility(View.VISIBLE);

                    } else {
                        shoppingCart.setVisibility(View.VISIBLE);
                        mangerReport.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(OrdersPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            childRef.addListenerForSingleValueEvent(valueEventListener);
        }

        public void navBarInitializer() {
            mAuth = FirebaseAuth.getInstance();
            /* Image views of navBar*/
            mangerReport = findViewById(R.id.report);
            shoppingCart = findViewById(R.id.shop_cart);
            logInImg = findViewById(R.id.logInImg);
            accountImg = findViewById(R.id.accountImg);
            ImageView logOutImg = findViewById(R.id.logOutImg);
            ImageView homePageImg = findViewById(R.id.homeImg);
            /* OnClick Listeners  */
            logInImg.setOnClickListener(this);
            accountImg.setOnClickListener(this);
            logOutImg.setOnClickListener(this);
            homePageImg.setOnClickListener(this);
            shoppingCart.setOnClickListener(this);
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
                case R.id.shop_cart:
                    startActivity(new Intent(this, CartPage.class));
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
            cartAdapter = new com.example.myapplication.CartPage.CartPageAdapter(items, itemsCount, this,cart, totalPrice);
            cartAdapter.notifyItemInserted(position);
            cartItemsRecyc.setAdapter(cartAdapter);
        }

        @Override
        public void onPointerCaptureChanged(boolean hasCapture) {
            super.onPointerCaptureChanged(hasCapture);
        }

    }
