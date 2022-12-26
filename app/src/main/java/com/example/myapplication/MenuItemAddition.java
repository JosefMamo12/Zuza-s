package com.example.myapplication;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MenuItemAddition extends AppCompatActivity implements View.OnClickListener
{
    private EditText editName, editDescription, editPrice;
    private AutoCompleteTextView editCategory;
    private ArrayList<String> currentCategories;

    public static final String INVALID = ".#$[]";

    public MenuItemAddition()
    {
        // For testing.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_add_item);

        TextView banner = (TextView) findViewById(R.id.add_item_header);
        banner.setOnClickListener(this);

        editName = (EditText) findViewById(R.id.add_item_name);
        editDescription = (EditText) findViewById(R.id.add_item_description);

        // Get a reference to the AutoCompleteTextView in the layout
        // Make category section autocomplete based on existing categories.
            editCategory = (AutoCompleteTextView) findViewById(R.id.add_item_category);


        //Pull from database categories into an arraylist.
        //(Unless this is done here, and not a separate function, the array list will be empty
        //even though there are categories).
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("menuItems");
        currentCategories = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot child : snapshot.getChildren())
                {
                    String category = child.getKey();

                    if (!currentCategories.contains(category))
                        currentCategories.add(category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Data read from menu failed, err code: " + error.getCode());
            }
        });

        // Create the adapter and set it to the AutoCompleteTextView
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currentCategories);
        editCategory.setAdapter(adapter);
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
            case R.id.add_item_new_category:
                addCategory();
                break;
        }
    }

    /**
     * Opens an alert box to inquire user about new category's name, check validity and writes to data.
     */
    private void addCategory()
    {
        final EditText newCategory = new EditText(this);
        Context temp_context = this;
        new AlertDialog.Builder(this).setTitle("")
                .setMessage("שם הקטגוריה").setView(newCategory)
                .setPositiveButton("יאללה זורם", (dialog, whichButton) -> {
                    String category = newCategory.getText().toString().trim();

                        // Add category
                        // Define new item object and add into database under menuItems.
                        String endMessage;
                        if (addCategory(category))
                        {
                            endMessage = "הוספת קטגוריה הושלמה.";
                        }
                        else
                        {
                            endMessage = "לא הושלמה הוספת קטגוריה.";
                        }
                        Toast t = new Toast(temp_context);
                        t.setText(endMessage);
                        t.show();
                })
                .setNegativeButton("ביטול", (dialog, whichButton) -> {
                }).show();
    }

    private boolean addCategory(String category)
    {
        try
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("menuItems").child(category);
            myRef.setValue(category);
        }
        catch(Exception e)
        {
            return false;
        }
        return true;
    }


    /**
     * Checks all input boxes and writes a new item into database if it's valid.
     */
    private void addItem()
    {
        String name = editName.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String category = editCategory.getText().toString().trim();
        String price = editPrice.getText().toString().trim();

        if (invalidInput(name, category, price))
            return;

        // Define new item object and add into database under menuItems under categories


        // Add decimal points to price if there is none.
        DecimalFormat df = new DecimalFormat("0.00");
        MenuItemModel itemAdapter = new MenuItemModel(name, description,
                df.format(Double.parseDouble(price))+"");


        String endMessage;
        if (addItem(itemAdapter, category))
        {
            endMessage = "הוספת פריט הושלמה.";
        }
        else
        {
            endMessage = "לא הושלמה הוספת פריט";
        }
        Toast t = new Toast(this);
        t.setText(endMessage);
        t.show();
        finish();
    }

    protected boolean addItem(MenuItemModel item, String category)
    {
        try
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference categoryRef = database.getReference("menuItems").child(category).child(item.getName());
            categoryRef.setValue(item);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
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
        try
        {
            if (name.length() < 2) {
                editName.setError("אנא בחרי שם ארוך יותר.");
                editName.requestFocus();
                return true;
            }
            if (invalidNamePath(name))
            {
                editName.setError("אנא כתבי שם תקין (בלי " + INVALID + ")");
                editName.requestFocus();
                return true;
            }
            if (category.isEmpty())
            {
                editCategory.setError("אנא בחרי קטגוריה.");
                editCategory.requestFocus();
                return true;
            }
            if (invalidNamePath(category))
            {
                editCategory.setError("אנא כתבי קטגוריה תקינה (בלי " + INVALID + ")");
                editCategory.requestFocus();
                return true;
            }
            if (!currentCategories.contains(category)) {
                editCategory.setError("אנא בחרי קטגוריה קיימת.");
                editCategory.requestFocus();
                return true;
            }
            if (price.isEmpty())
            {
                editPrice.setError("אנא בחרי מחיר.");
                editPrice.requestFocus();
                return true;
            }

            try
            {
                if (Double.parseDouble(price) < 0)
                {
                    editPrice.setError("אנא בחרי מחיר לא שלילי.");
                    editPrice.requestFocus();
                    return true;
                }
            }
            catch (NumberFormatException e)
            {
                editPrice.setError("אנא כתבי מספר תקין.");
                editPrice.requestFocus();
                return true;
            }
        }
        catch (Exception ignored)
        {
            return true;
        }
        return false;
    }

    private boolean invalidNamePath(String name)
    {
        for (int i = 0; i < INVALID.length(); i++)
        {
            if (name.indexOf(INVALID.charAt(i)) != -1)
            {
                editName.setError("אנא כתבי שם תקין (בלי .#$[])");
                editName.requestFocus();
                return true;
            }
        }
        return false;
    }

}
