package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
import java.util.Iterator;

public class MenuPage extends AppCompatActivity implements View.OnClickListener, UpdateMenuRecyclerView {
    private ImageView logInImg, logOutImg, accountImg, homePageImg;
    private Button addItemBtn;
    boolean isAdmin;
    ArrayList<MenuItemModel> items = new ArrayList<>();
    MenuItemAdapter menuItemAdapter;
    FirebaseAuth mAuth;
    RecyclerView recyclerViewCategories, recyclerViewItems;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        showAll();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        database = FirebaseDatabase.getInstance();
        addItemBtn = (Button) findViewById(R.id.menu_add_item_manager);
        navBarInitializer();


        ArrayList<MenuCategoryModel> categories = new ArrayList<>();

        DatabaseReference myRef = database.getReference("menuItems");




        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    categories.add(new MenuCategoryModel(R.drawable.z_logo, child.getKey()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Data read from menu failed, err code: " + error.getCode());
            }
        });

        recyclerViewCategories = findViewById(R.id.rv_1);
        MenuCategoryAdapter menuCategoryAdapter = new MenuCategoryAdapter(categories, this, this); // might be wrong
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategories.setAdapter(menuCategoryAdapter);

        items = new ArrayList<>();
        recyclerViewItems = findViewById(R.id.rv_2);
        menuItemAdapter = new MenuItemAdapter(items);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewItems.setAdapter(menuItemAdapter);
    }

    private void checkIfAdminConnected() {
        DatabaseReference userRef = database.getReference("Users");

        if (mAuth.getCurrentUser() == null) {
            addItemBtn.setVisibility(View.INVISIBLE);
            return;

        }
        String uid = mAuth.getCurrentUser().getUid();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshotChild : snapshot.getChildren()) {
                    User user = dataSnapshotChild.getValue(User.class);
                    if(user.getUid() == null){
                        user.setUid(dataSnapshotChild.getKey());
                    }
                    if (user.getUid().equals(uid) && user.isAdmin()) {
                        addItemBtn.setVisibility(View.VISIBLE);
                        break;
                    }
                    addItemBtn.setVisibility(View.INVISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void navBarInitializer() {
        mAuth = FirebaseAuth.getInstance();
        /* Image views of navBar*/
        logInImg = (ImageView) findViewById(R.id.logInImg);
        accountImg = (ImageView) findViewById(R.id.accountImg);
        logOutImg = (ImageView) findViewById(R.id.logOutImg);
        homePageImg = (ImageView) findViewById(R.id.homeImg);
        /* OnClick Listeners  */
        logInImg.setOnClickListener(this);
        accountImg.setOnClickListener(this);
        logOutImg.setOnClickListener(this);
        homePageImg.setOnClickListener(this);

        checkIfConnected();
        checkIfAdminConnected();
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
            case R.id.get_items_menu:
                showAll();
                break;
            case R.id.homeImg:
                System.out.println("home page from menu pressed");
                finish();
                break;
            case R.id.logInImg:
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.logOutImg:
                mAuth.signOut();
                checkIfAdminConnected();
                checkIfConnected();

        }

    }

    public void callback(int position, ArrayList<MenuItemModel> items) {
        menuItemAdapter = new MenuItemAdapter(items);
        menuItemAdapter.notifyDataSetChanged();
        recyclerViewItems.setAdapter(menuItemAdapter);
    }

    /**
     * For debuging purposes.
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
//                    getChildrenOf(child);
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
