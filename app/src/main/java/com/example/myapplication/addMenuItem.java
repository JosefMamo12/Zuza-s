package com.example.myapplication;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class addMenuItem extends AppCompatActivity implements View.OnClickListener
{

    private TextView banner;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private EditText editName, editCategory;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        editName = (EditText) findViewById(R.id.fullName);
        editCategory =(EditText) findViewById(R.id.age);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View view)
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

    }

}
