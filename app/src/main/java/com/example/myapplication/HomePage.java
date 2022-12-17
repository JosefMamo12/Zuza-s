package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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

    public void openLocation(View view) {
        double latitude = 32.71438650765835, longitude = 35.56366019755522;
        String name = "ZUZA";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=" +latitude +"," + longitude + "(" + name + ")"));
        startActivity(intent);
    }
}
//32.71438650765835, 35.56366019755522