package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity implements View.OnClickListener{

    private ImageSlider imageSlider;
    private ImageView loginImg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        loginImg = (ImageView) findViewById(R.id.logInImg);
        loginImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    findViewById(R.id.loginText).setVisibility(View.VISIBLE);
                    return true;
                }
                return true;
            }
        });

        imageSlider = (ImageSlider) findViewById(R.id.image_slider);

        ArrayList<SlideModel> images = new ArrayList<>();

        images.add(new SlideModel(R.drawable.a1, null));
        images.add(new SlideModel(R.drawable.a2, null));
        images.add(new SlideModel(R.drawable.a3, null));
        images.add(new SlideModel(R.drawable.a4, null));
        images.add(new SlideModel(R.drawable.a5, null));
        images.add(new SlideModel(R.drawable.a6, null));

        imageSlider.setImageList(images, ScaleTypes.FIT);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menuFoodImg:
                startActivity(new Intent(this, MenuPage.class));
                break;
            case R.id.logInImg:
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.homeImg:
                startActivity(new Intent(this, HomePage.class));
                break;

        }
    }



}

