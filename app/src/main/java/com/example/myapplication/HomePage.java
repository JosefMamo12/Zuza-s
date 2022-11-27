package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    ImageView login,menu,settings,foodMenu;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
//        menu = (ImageView) findViewById(R.id.menuImg);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view)
    {
        switch(view.getId()){
            case R.id.menuFoodImg:
                startActivity(new Intent(this, MenuPage.class));
                break;

        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}