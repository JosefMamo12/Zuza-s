package com.example.myapplication;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class addMenuItem extends AppCompatActivity implements View.OnClickListener
{

    private EditText editName, editCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.out.println("Entered menu add page");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_add_item);

        TextView banner = (TextView) findViewById(R.id.add_item_header);
        banner.setOnClickListener(this);

        editName = (EditText) findViewById(R.id.add_item_name);
        editCategory =(EditText) findViewById(R.id.add_item_category);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.add_item_finish:
                addItem();
                break;
            case R.id.add_item_abort:
                finish();
                break;
        }
    }

    private void addItem()
    {
        String name = editName.getText().toString().trim();
        String category = editCategory.getText().toString().trim();
        if (name.isEmpty())
        {
            editName.setError("Name required.");
            editName.requestFocus();
            return;
        }
        if (category.isEmpty())
        {
            editCategory.setError("category required.");
            editCategory.requestFocus();
            return;
        }

        // Define new item object and add into database under menuItems.
        menuItem newItem = new menuItem(name, category);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("menuItems");

        myRef.setValue(newItem);
        Toast t = new Toast(this);
        t.setText("OK!");
        t.show();
        finish();
    }

}
