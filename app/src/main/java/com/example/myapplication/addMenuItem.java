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

    private EditText editName, editCategory, editPrice;

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
        editPrice = (EditText)  findViewById(R.id.add_item_price);

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
        String price = editPrice.getText().toString().trim();

        if (invalidInput(name, category, price))
            return;

        // Define new item object and add into database under menuItems.
        menuItem newItem = new menuItem(name, category, Double.parseDouble(price));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("menuItems");

        myRef.setValue(newItem);
        Toast t = new Toast(this);
        t.setText("הוספת פריט הושלמה.");
        t.show();
        finish();
    }

    /**
     * Validates input of item to be added.
     * @param name product name
     * @param category product category
     * @param price product price
     * @return false if only all the given inputs are valid and can be used to create a new item.
     */
    private boolean invalidInput(String name, String category, String price)
    {
        if (name.isEmpty())
        {
            editName.setError("Name required.");
            editName.requestFocus();
            return true;
        }
        if (category.isEmpty())
        {
            editCategory.setError("Category required.");
            editCategory.requestFocus();
            return true;
        }
        if (price.isEmpty())
        {
            editPrice.setError("Price required.");
            editPrice.requestFocus();
            return true;
        }
        else if (Double.parseDouble(price) < 0)
        {
            editPrice.setError("Please input a non-negative price.");
            editPrice.requestFocus();
            return true;
        }
        return false;
    }

}
