package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class MenuPage extends AppCompatActivity implements View.OnClickListener, UpdateMenuRecyclerView {
    private ImageView logInImg, logOutImg, accountImg, homePageImg, shoppingCart, mangerReport;
    private Button addItemBtn, editItemBtn;
    ArrayList<MenuItemModel> items = new ArrayList<>();
    MenuItemAdapter menuItemAdapter;
    MenuCategoryAdapter menuCategoryAdapter;
    FirebaseAuth mAuth;
    RecyclerView recyclerViewCategories, recyclerViewItems;
    FirebaseDatabase database;

    @Override
    protected void onStart() {
        super.onStart();
        checkIfConnected();
        checkIfAdminConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        database = FirebaseDatabase.getInstance();
        addItemBtn = (Button) findViewById(R.id.menu_add_item_manager);
        editItemBtn = (Button) findViewById(R.id.menu_edit_item_manager);
        navBarInitializer();

        // Read categories models from database.
        ArrayList<MenuCategoryModel> categories = new ArrayList<>();
        DatabaseReference myRef = database.getReference("menuItems");
        ValueEventListener evl = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    // Give the default zuza logo as icon, could be expanded to have a unique image.
                    MenuCategoryModel md = new MenuCategoryModel(R.drawable.z_logo, child.getKey());
                    if (!categories.contains(md))
                        categories.add(md);
                }
                menuCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Data read from menu failed, err code: " + error.getCode());
            }
        };

        myRef.addValueEventListener(evl);

        // Dummy Node
        // Category viewer
        categories.add(new MenuCategoryModel(R.drawable.z_logo, "לחץ עליי"));

        recyclerViewCategories = findViewById(R.id.rv_1);

        // {this} is given twice as argument, as this class both is an activity and also
        // implements the interface updateMenuRecyclerView.
        menuCategoryAdapter = new MenuCategoryAdapter(categories, this, this);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategories.setAdapter(menuCategoryAdapter);

        // Initialize items and bind them to a recycle view.
        items = new ArrayList<>();
        recyclerViewItems = findViewById(R.id.rv_2);

        menuItemAdapter = new MenuItemAdapter(items, this);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewItems.setAdapter(menuItemAdapter);
    }


    private void checkIfAdminConnected() {
        if (mAuth.getCurrentUser() == null) {
            addItemBtn.setVisibility(View.INVISIBLE);
            editItemBtn.setVisibility(View.INVISIBLE);
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
                    addItemBtn.setVisibility(View.VISIBLE);
                    editItemBtn.setVisibility(View.VISIBLE);
                    shoppingCart.setVisibility(View.INVISIBLE);
                    mangerReport.setVisibility(View.VISIBLE);

                } else {
                    addItemBtn.setVisibility(View.INVISIBLE);
                    editItemBtn.setVisibility(View.INVISIBLE);
                    shoppingCart.setVisibility(View.VISIBLE);
                    mangerReport.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MenuPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        childRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public void navBarInitializer() {
        mAuth = FirebaseAuth.getInstance();
        /* Image views of navBar*/
        mangerReport = (ImageView) findViewById(R.id.report);
        shoppingCart = (ImageView) findViewById(R.id.shop_cart);
        logInImg = (ImageView) findViewById(R.id.logInImg);
        accountImg = (ImageView) findViewById(R.id.accountImg);
        logOutImg = (ImageView) findViewById(R.id.logOutImg);
        homePageImg = (ImageView) findViewById(R.id.homeImg);
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
            case R.id.menu_add_item_manager:
                startActivity(new Intent(this, MenuItemAddition.class));
                break;
            case R.id.menu_edit_item_manager:
                startActivity(new Intent(this, MenuItemEdit.class));
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

    /**
     * Notify recycler that the category changed, load new category.
     */
    public void callback(int position, ArrayList<MenuItemModel> items) {
        menuItemAdapter = new MenuItemAdapter(items, this);
        menuItemAdapter.notifyItemInserted(position);
        recyclerViewItems.setAdapter(menuItemAdapter);
    }

    /**
     * For debugging purposes.
     */
    private void showAll() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("menuItems");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    System.out.println("---Category start---");
                    System.out.println("{" + child + "}");
                    System.out.println("---Category end---");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Data read from menu failed, err code: " + error.getCode());
            }
        });
    }

}

