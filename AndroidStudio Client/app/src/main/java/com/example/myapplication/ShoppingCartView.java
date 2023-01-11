package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.databinding.ActivityShoppingCartBinding;

public class ShoppingCartView extends AppCompatActivity {
    ShoppingCartModel shoppingCartModel;
    ShoppingCartController shoppingCartController;
    ActivityShoppingCartBinding cartBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        shoppingCartModel = new ShoppingCartModel();
        shoppingCartController = new ShoppingCartController(shoppingCartModel,this);




    }



}