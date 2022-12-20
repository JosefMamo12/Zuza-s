package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuItemEdit extends AppCompatActivity implements View.OnClickListener
{
    private EditText editCategory, editDescription, editPrice;
    private AutoCompleteTextView editName;
    private HashMap<String, String> itemToCategory;

    private HashMap<String, MenuItemModel> allItemsModels;
    private ArrayList<String> allItems;

    private MenuItemModel selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit_item);

        TextView banner = (TextView) findViewById(R.id.edit_item_header);
        banner.setOnClickListener(this);

        editName = (AutoCompleteTextView) findViewById(R.id.edit_item_name);
        editDescription = (EditText) findViewById(R.id.edit_item_description);
        editCategory = (EditText) findViewById(R.id.edit_item_category);
        editPrice = (EditText)  findViewById(R.id.edit_item_price);

            /*
            Pull from database items into an arraylist.
            (Unless this is done here, and not a separate function, the array list will be empty
            even though there are categories).
             */
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("menuItems");
        ArrayList<String> currentItems = new ArrayList<>();
        allItems = new ArrayList<>();
        allItemsModels = new HashMap<>();
        itemToCategory = new HashMap<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot category : snapshot.getChildren())
                {
                    addItems(category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Data read from menu failed, err code: " + error.getCode());
            }
        });

        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allItems);
        editName.setAdapter(adapter);

        // Based on item clicked, autofill rest of info to ease on the manager.
        editName.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id)
            {
                String itemName = adapter.getItem(i);
                String itemCategory = itemToCategory.get(itemName);
                selectedItem = getItem(itemName);

                editCategory.setText(itemCategory);
                editDescription.setText(selectedItem.getDesc());
                editPrice.setText(selectedItem.getPrice());
            }
        });
}

    private MenuItemModel getItem(String itemName)
    {
        return allItemsModels.get(itemName);
    }

    private void addItems(DataSnapshot category)
    {
        for (DataSnapshot item : category.getChildren())
        {
            Map<String,String> td=(HashMap<String, String>)item.getValue();
            assert td != null;
            String price = td.get("price");
            String name = td.get("name");
            String desc = td.get("desc");
            MenuItemModel singleItem = new MenuItemModel(name, desc, price);

            allItemsModels.put(name, singleItem);
            allItems.add(name);
            itemToCategory.put(name, category.getKey());
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.edit_item_finish:
                updateItem();
                break;
            case R.id.edit_item_abort:
                finish();
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void updateItem()
    {
        String name = editName.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String category = editCategory.getText().toString().trim();
        String price = editPrice.getText().toString().trim();

        if (invalidInput(name, category, price))
            return;

        // Define new item object and add into database under menuItems under categories
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("menuItems").child(category).child(name);

        MenuItemModel itemAdapter = new MenuItemModel(name, description, price);

        if (!name.equals(selectedItem.getName()))
        {
            removeItem(selectedItem.getName());
        }

        myRef.setValue(itemAdapter);
        Toast t = new Toast(this);
        t.setText("עריכת פריט הושלמה.");
        t.show();
        finish();
    }

    private void removeItem(String item)
    {
        String category = itemToCategory.get(item);
        assert category != null;
        FirebaseDatabase.getInstance().getReference("menuItems")
                .child(category).child(selectedItem.getName()).removeValue();
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
        try {
            if (name.isEmpty()) {
                editName.setError("אנא בחרי שם.");
                editName.requestFocus();
                return true;
            }
            if (category.isEmpty()) {
                editCategory.setError("אנא בחרי קטגוריה.");
                editCategory.requestFocus();
                return true;
            }
            if (!itemToCategory.containsValue(category)) {
                editCategory.setError("אנא בחרי קטגוריה קיימת.");
                editCategory.requestFocus();
                return true;
            }
            if (price.isEmpty()) {
                editPrice.setError("אנא בחרי מחיר.");
                editPrice.requestFocus();
                return true;
            } else if (Double.parseDouble(price) < 0) {
                editPrice.setError("אנא בחרי מחיר לא שלילי.");
                editPrice.requestFocus();
                return true;
            }
        }
        catch (Exception ignored)
        {

        }
        return false;
    }
}
