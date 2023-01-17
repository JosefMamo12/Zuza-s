package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class OrdersPage extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    RecyclerView ordersRecycle;
    OrdersPageAdapter ordersAdapter;
    ArrayList<Order> items;
    HashMap<String, Integer> itemsCount;
    private ImageView logInImg;
    private ImageView accountImg;
    private ImageView shoppingCart;
    private ImageView managerReport;
    //        private TextView totalPrice;
    private Order order;

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
        setContentView(R.layout.activity_orders);
        navBarInitializer();
        checkIfConnected();
        checkIfAdminConnected();
        items = new ArrayList<>();
        itemsCount = new HashMap<>();

        Context temp_ctx = this;
        updateData(temp_ctx);
    }

    private void updateData(Context temp_ctx) {
//        Query getOrders = database.getReference().child("Orders");
        database.getReference().child("Orders").orderByChild("userID").equalTo(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            order = data.getValue(Order.class);

                            if (order != null) {
                                items.add(order);
                            }
                            ordersRecycle = findViewById(R.id.cart_items);
                            ordersAdapter = new OrdersPageAdapter(items, temp_ctx);
                            ordersRecycle.setLayoutManager(new LinearLayoutManager(temp_ctx, LinearLayoutManager.VERTICAL, false));
                            ordersRecycle.setAdapter(ordersAdapter);
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
                assert user != null;
                if (user.isAdmin()) {
                    shoppingCart.setVisibility(View.INVISIBLE);
                    managerReport.setVisibility(View.VISIBLE);

                } else {
                    shoppingCart.setVisibility(View.VISIBLE);
                    managerReport.setVisibility(View.INVISIBLE);
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
        managerReport = findViewById(R.id.report);
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
        managerReport.setOnClickListener(this);

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
            case R.id.menu_page:
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
                } else {
                    Toast.makeText(this, "Please login before logout", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.accountImg:
                startActivity(new Intent(getApplicationContext(), Profile.class));
                break;
        }
    }


    public void callback(int position, ArrayList<Order> items) {
        ordersAdapter = new OrdersPageAdapter(items, this);
        ordersAdapter.notifyItemInserted(position);
        ordersRecycle.setAdapter(ordersAdapter);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    static class OrdersPageAdapter extends RecyclerView.Adapter<OrdersPageAdapter.SingleOrderHolder> {
        //        double totalPrice = 0;
        Context c;
        ArrayList<Order> items;
        TextView text;


        public OrdersPageAdapter(ArrayList<Order> items, Context c) {
            this.items = items;
            this.c = c;
//            this.text = text;

        }

        @NonNull
        @Override
        public SingleOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_single_layout, parent, false);
            return new OrdersPageAdapter.SingleOrderHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrdersPageAdapter.SingleOrderHolder holder,
                                     int position) {
            View confirm = holder.constraintLayout.findViewById(R.id.order_complete);
            View DelAll = holder.constraintLayout.findViewById(R.id.CartDelAll);

            // Get item parameters.
            Order currentItem = items.get(position);
            int pos = holder.getAdapterPosition();
            int amount = currentItem.getCount();
            String amountText = amount + " פריטים";


            // Can be changed to get an image based on item's name
//            holder.imageView.setImageResource(R.drawable.z_logo);
//        holder.name.setText(currentItem.getName()); //TODO: need an order name/id
            holder.amount.setText(amountText);
            DecimalFormat df = new DecimalFormat("0.00");
            String totalPrice = df.format(currentItem.getPrice());
            holder.price.setText(totalPrice);
//            totalPrice.setText(String.valueOf(jCart.getPrice()));

            String id = FirebaseAuth.getInstance().getUid();
            Query orders = FirebaseDatabase.getInstance().getReference().
                    child("Orders");
            //confirm order => make complete
            confirm.setOnClickListener(view -> orders.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Order order = ds.getValue(Order.class);
                        if (order != null && Objects.equals(order, currentItem)) {

                            String key = ds.getKey();
                            order.setComplete(true);
                            order.setComplete_time(System.currentTimeMillis());

                            FirebaseDatabase.getInstance().getReference().child("Orders").
                                    child(key)
                                    .setValue(order);

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
        public static class SingleOrderHolder extends RecyclerView.ViewHolder {
            //            public ImageView imageView;
            public TextView name;
            public TextView amount;
            public TextView price;
            public TextView status;
            public Button confirm;
            public Button viewDetails;
            ConstraintLayout constraintLayout;

            public SingleOrderHolder(@NonNull View itemView) {
                super(itemView);
//                imageView = itemView.findViewById(R.id.cart_zuza_logo);
                name = itemView.findViewById(R.id.OrderName);
                amount = itemView.findViewById(R.id.OrderItemsAmount);
                price = itemView.findViewById(R.id.TotalOrderPrice);
                status = itemView.findViewById(R.id.order_status);
                confirm = itemView.findViewById(R.id.order_complete);
                viewDetails = itemView.findViewById(R.id.show_order);
                constraintLayout = itemView.findViewById(R.id.constraintLayout);
            }
        }
    }
}

