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

import com.example.myapplication.Models.Cart;
import com.example.myapplication.Models.MenuItemModel;
import com.example.myapplication.Models.Order;
import com.example.myapplication.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CartPage extends AppCompatActivity implements View.OnClickListener, UpdateMenuRecyclerView {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    RecyclerView cartItemsRecyc;
    CartPageAdapter cartAdapter;
    Cart cart;
    ArrayList<MenuItemModel> items;
    HashMap<String, Integer> itemsCount;
    private ImageView logInImg;
    private ImageView accountImg;
    private ImageView shoppingCart;
    private ImageView managerReport;
    private ImageView menuPage;
    private TextView totalPrice;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        shoppingCart.setImageResource(R.drawable.ic_baseline_shopping_cart_36_black);
        Context temp_ctx = this;
        updateData(temp_ctx);
    }

    private void updateData(Context temp_ctx) {
        database.getReference().child("Carts").orderByChild("userID").equalTo(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            cart = data.getValue(Cart.class);

                            if (cart != null) {
                                for (MenuItemModel item : cart.getItems()) {
                                    if (itemsCount.containsKey(item.getName())) {

                                        itemsCount.put(item.getName(), itemsCount.get(item.getName()) + 1);
                                    } else {
                                        itemsCount.put(item.getName(), 1);
                                        items.add(item);
                                    }
                                }
                            }
                            cartItemsRecyc = findViewById(R.id.cart_items);
                            cartAdapter = new CartPageAdapter(items, itemsCount, temp_ctx, cart, totalPrice);
                            cartItemsRecyc.setLayoutManager(new LinearLayoutManager(temp_ctx, LinearLayoutManager.VERTICAL, false));
                            cartItemsRecyc.setAdapter(cartAdapter);
                            totalPrice.setText("תשלום: " + cart.getPrice());
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
            managerReport.setVisibility(View.INVISIBLE);
            return;
        }
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = database.getReference("Users");
        DatabaseReference childRef = userRef.child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    if (user.isAdmin()) {
                        shoppingCart.setVisibility(View.INVISIBLE);
                        managerReport.setVisibility(View.VISIBLE);

                    } else {
                        managerReport.setVisibility(View.INVISIBLE);
                        shoppingCart.setVisibility(View.VISIBLE);
                    }
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
        managerReport = findViewById(R.id.report);
        shoppingCart = findViewById(R.id.shop_cart);
        logInImg = findViewById(R.id.logInImg);
        accountImg = findViewById(R.id.accountImg);
        menuPage = findViewById(R.id.menuFoodImg);
        ImageView logOutImg = findViewById(R.id.logOutImg);
        ImageView homePageImg = findViewById(R.id.homeImg);

        /* OnClick Listeners  */
        logInImg.setOnClickListener(this);
        accountImg.setOnClickListener(this);
        logOutImg.setOnClickListener(this);
        homePageImg.setOnClickListener(this);
        shoppingCart.setOnClickListener(this);
        managerReport.setOnClickListener(this);
        menuPage.setOnClickListener(this);

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
            case R.id.make_order:
                //take all cart contents and make an order
                initOrder();
                startActivity(new Intent(this, OrdersPage.class));
                break;
            case R.id.view_user_orders:
                startActivity(new Intent(this, OrdersPage.class));
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
                    startActivity(new Intent(getApplicationContext(), HomePage.class));
                } else {
                    Toast.makeText(this, "Please login before logout", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.accountImg:
                startActivity(new Intent(getApplicationContext(), Profile.class));
                break;
            case R.id.menuFoodImg:
                startActivity(new Intent(getApplicationContext(), MenuPage.class));
                break;
        }
    }

    private void initOrder() {
        Query myCart = FirebaseDatabase.getInstance().getReference().child("Carts");
        String id = FirebaseAuth.getInstance().getUid();
        Context temp_ctx = this;
        myCart.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    cart = ds.getValue(Cart.class);
                    if (cart != null && Objects.equals(cart.getUserID(), id)) {
                        //make an order
                        long timestamp = System.currentTimeMillis();
                        Order order = new Order(cart, timestamp);
                        database.getReference().child("Orders").getRef().push().setValue(order);

                        //delete cart
                        String key = ds.getKey();
                        FirebaseDatabase.getInstance().getReference().child("Carts").
                                child(key)
                                .removeValue();

                        Toast.makeText(temp_ctx, "הזמנה נקלטה בהצלחה", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void callback(int position, ArrayList<MenuItemModel> items) {
        cartAdapter = new CartPageAdapter(items, itemsCount, this, cart, totalPrice);

        cartAdapter.notifyItemInserted(position);
        cartItemsRecyc.setAdapter(cartAdapter);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    // ADAPTER


    static class CartPageAdapter extends RecyclerView.Adapter<CartPageAdapter.CartItemHolder> {
        //        double totalPrice = 0;
        Context c;
        HashMap<String, Integer> itemsCount;
        ArrayList<MenuItemModel> items;
        Cart cart;
        TextView text;


        public CartPageAdapter(ArrayList<MenuItemModel> items, HashMap<String, Integer> itemsCount, Context c, Cart cart, TextView text) {
            this.items = items;
            this.itemsCount = itemsCount;
            this.c = c;
            this.cart = cart;
            this.text = text;

        }

        @NonNull
        @Override
        public CartItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_single_item, parent, false);
            return new CartPageAdapter.CartItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CartPageAdapter.CartItemHolder holder,
                                     int position) {
            View DelOne = holder.constraintLayout.findViewById(R.id.CartDelOne);
            View DelAll = holder.constraintLayout.findViewById(R.id.CartDelAll);

            // Get item parameters.
            MenuItemModel currentItem = items.get(position);
            int pos = holder.getAdapterPosition();
            int amount = itemsCount.get(currentItem.getName());
            String amountText = "כמות " + amount;


            // Can be changed to get an image based on item's name
//            holder.imageView.setImageResource(R.drawable.z_logo);
            holder.name.setText(currentItem.getName());
            holder.amount.setText(amountText);
            holder.price.setText(currentItem.getPrice());
//            totalPrice.setText(String.valueOf(jCart.getPrice()));

            String id = FirebaseAuth.getInstance().getUid();
            Query myCart = FirebaseDatabase.getInstance().getReference().
                    child("Carts");

            DelOne.setOnClickListener(view -> myCart.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        cart = ds.getValue(Cart.class);
                        if (cart != null && Objects.equals(cart.getUserID(), id)) {
                            cart.getItems().remove(currentItem);

                            double price = Double.parseDouble(currentItem.getPrice());
                            cart.setPrice(cart.getPrice() - price);
                            cart.setCount(cart.getCount() - 1);
                            text.setText("תשלום: " + cart.getPrice());
                            String key = ds.getKey();


                            if (cart.getCount() > 0) {
                                FirebaseDatabase.getInstance().getReference().child("Carts").
                                        child(key)
                                        .setValue(cart);
                            } else {

                                FirebaseDatabase.getInstance().getReference().child("Carts").
                                        child(key)
                                        .removeValue();
                            }

                            if (amount == 1) {
                                itemsCount.remove(currentItem.getName());
                                items.remove(currentItem);
                                notifyItemRemoved(pos);
                            } else {
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

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        cart = ds.getValue(Cart.class);
                        if (cart != null && Objects.equals(cart.getUserID(), id)) {
                            while (cart.getItems().contains(currentItem)) {
                                cart.getItems().remove(currentItem);

                                double price = Double.parseDouble(currentItem.getPrice());
                                cart.setPrice(cart.getPrice() - price);
                                cart.setCount(cart.getCount() - 1);
                                text.setText(String.format("תשלום: %s", cart.getPrice()));
                            }

                            String key = ds.getKey();
                            FirebaseDatabase.getInstance().getReference().child("Carts").
                                    child(key)
                                    .setValue(cart);

                            itemsCount.remove(currentItem.getName());
                            items.remove(currentItem);
                            notifyItemRemoved(pos);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError
                                                error) {

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
        public static class CartItemHolder extends RecyclerView.ViewHolder {
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
