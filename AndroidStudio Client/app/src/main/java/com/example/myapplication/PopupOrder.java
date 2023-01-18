package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Models.Cart;
import com.example.myapplication.Models.MenuItemModel;
import com.example.myapplication.Models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class PopupOrder extends AppCompatActivity implements View.OnClickListener, UpdateMenuRecyclerView{
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    RecyclerView ItemsRecyc;
    SingleOrderAdapter itemAdapter;
    ArrayList<MenuItemModel> items;
    HashMap<String, Integer> itemsCount;
    private TextView orderDate, confirmDate;
    Order order;
    Long orderTime;
    String currUserID;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();

        orderTime = b.getLong("date");
        currUserID = b.getString("id");

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getUid() == null) return;
        database = FirebaseDatabase.getInstance();
        setContentView(R.layout.popup_order_details);
        orderDate = findViewById(R.id.order_date);
        confirmDate = findViewById(R.id.confirm_date);
        items = new ArrayList<>();
        itemsCount = new HashMap<>();
        Context temp_ctx = getParent();

        //popup dimensions
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int height = (int) (0.8 * dm.heightPixels);
        int width = (int) (0.8 * dm.widthPixels);

        getWindow().setLayout(width,height);
        updateData(temp_ctx);

        findViewById(R.id.arrow).setOnClickListener(view ->
        {
            finish();
        });
    }

    private void updateData(Context temp_ctx)
    {
        database.getReference().child("Orders").orderByChild("userID").equalTo(currUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            order = data.getValue(Order.class);


                            if (order != null && order.getOrder_time() == orderTime)
                            {
                                for (MenuItemModel item : order.getItems()) {
                                    if (itemsCount.containsKey(item.getName())) {

                                        itemsCount.put(item.getName(), itemsCount.get(item.getName()) + 1);
                                    } else {
                                        itemsCount.put(item.getName(), 1);
                                        items.add(item);
                                    }
                                }
                            }

                            else
                            {
                                continue;
                            }

                            ItemsRecyc = findViewById(R.id.order_items);
                            Cart tempCart = new Cart(order.getItems(), order.getCount(), order.getPrice(), order.getUserID());
                            itemAdapter = new SingleOrderAdapter(items, itemsCount, temp_ctx, tempCart, orderDate);
                            ItemsRecyc.setLayoutManager(new LinearLayoutManager(temp_ctx, LinearLayoutManager.VERTICAL, false));
                            ItemsRecyc.setAdapter(itemAdapter);

                            String pattern = "dd-MM-yy HH:mm";
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                            String date = simpleDateFormat.format(new Date(order.getOrder_time()));
                            orderDate.setText(String.format("ת. הזמנה: %s", date));

                            if (order.isComplete())
                            {
                                String finishDate = simpleDateFormat.format(new Date(order.getComplete_time()));
                                confirmDate.setText(String.format("ת. אישור: %s", finishDate));
                            }
                            else
                            {
                                confirmDate.setText("בהכנה");
                            }
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onClick(View view)
    {
    }

    @Override
    public void callback(int position, ArrayList<MenuItemModel> items) {

    }

    static class SingleOrderAdapter extends RecyclerView.Adapter<PopupOrder.SingleOrderAdapter.OrderItemHolder>
    {
        //        double totalPrice = 0;
        Context c;
        HashMap<String, Integer> itemsCount;
        ArrayList<MenuItemModel> items;
        Cart cart;
        TextView text;


        public SingleOrderAdapter(ArrayList<MenuItemModel> items, HashMap<String, Integer> itemsCount, Context c, Cart cart, TextView text) {
            this.items = items;
            this.itemsCount = itemsCount;
            this.c = c;
            this.cart = cart;
            this.text = text;

        }

        @NonNull
        @Override
        public PopupOrder.SingleOrderAdapter.OrderItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_single_item, parent, false);
            return new PopupOrder.SingleOrderAdapter.OrderItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderItemHolder holder,
                                     int position) {
            // Get item parameters.
            MenuItemModel currentItem = items.get(position);
            int pos = holder.getAdapterPosition();
            int amount = itemsCount.get(currentItem.getName());
            String amountText = "כמות " + amount;

            holder.constraintLayout.findViewById(R.id.CartDelAll).setVisibility(View.INVISIBLE);
            holder.constraintLayout.findViewById(R.id.CartDelOne).setVisibility(View.INVISIBLE);

            // Can be changed to get an image based on item's name
//            holder.imageView.setImageResource(R.drawable.z_logo);
            holder.name.setText(currentItem.getName());
            holder.amount.setText(amountText);
            holder.price.setText(currentItem.getPrice());
//            totalPrice.setText(String.valueOf(jCart.getPrice()));

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        /**
         * Class for a single item to load into the view holder.
         */
        public static class OrderItemHolder extends RecyclerView.ViewHolder {
            //            public ImageView imageView;
            public TextView name;
            public TextView amount;
            public TextView price;
            ConstraintLayout constraintLayout;

            public OrderItemHolder(@NonNull View itemView) {
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
