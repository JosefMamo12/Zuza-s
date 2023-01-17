package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Models.MenuItemModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class MenuItemEdit extends AppCompatActivity implements View.OnClickListener {
    private EditText editCategory, editDescription, editPrice;
    private AutoCompleteTextView editName;
    private MenuItemModel selectedItem;

    private HashMap<String, String> itemToCategory;
    private HashMap<String, MenuItemModel> allItemsModels;
    private ArrayList<String> allItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit_item);

        TextView banner = findViewById(R.id.edit_item_header);
        banner.setOnClickListener(this);

        editName = findViewById(R.id.edit_item_name);
        editDescription = findViewById(R.id.edit_item_description);
        editCategory = findViewById(R.id.edit_item_category);
        editPrice = findViewById(R.id.edit_item_price);

        // Pull all items from database into an arraylist to autocomplete names.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference categoriesRef = database.getReference("menuItems");
        allItems = new ArrayList<>();
        allItemsModels = new HashMap<>();
        itemToCategory = new HashMap<>();
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot category : snapshot.getChildren()) {
                    addItems(category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Data read from menu failed, err code: " + error.getCode());
            }
        });
        initCategories();

        // Create the adapter and set it to the AutoCompleteTextView.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allItems);
        editName.setAdapter(adapter);

        // Based on item clicked, autofill rest of info to ease on the manager.
        editName.setOnItemClickListener((adapterView, view, i, id) ->
        {
            String itemName = adapter.getItem(i);
            String itemCategory = itemToCategory.get(itemName);
            selectedItem = getItem(itemName);

            editCategory.setText(itemCategory);
            editDescription.setText(selectedItem.getDesc());
            editPrice.setText(selectedItem.getPrice());
        });
    }

    private void initCategories() {
        // Read categories which don't have an item yet.
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("menuItems");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String categoryName = child.getKey();
                    if (!itemToCategory.containsValue(categoryName))
                        itemToCategory.put(categoryName, categoryName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Data read from menu failed, err code: " + error.getCode());
            }
        });
    }

    private MenuItemModel getItem(String itemName) {
        return allItemsModels.get(itemName);
    }

    /**
     * Adds all current items into local memory to perform edit or removal of items.
     */
    private void addItems(DataSnapshot category) {
        for (DataSnapshot item : category.getChildren()) {
            // Check if category has no values (empty)
            if (item.getValue() instanceof String)
                continue;

            try {
                Map<String, String> td = (HashMap<String, String>) item.getValue();
                assert td != null;
                String price = td.get("price");
                String name = td.get("name");
                String desc = td.get("desc");
                MenuItemModel singleItem = new MenuItemModel(name, desc, price);

                allItemsModels.put(name, singleItem);
                allItems.add(name);
                itemToCategory.put(name, category.getKey());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_item_finish:
                updateItem();
                break;
            case R.id.edit_item_remove:
                removeSelectedItem();
                break;
            case R.id.edit_item_abort:
                finish();
                break;
        }
    }

    /**
     * Opens an alert dialog to remove category.
     */
    public void removeCategoryActivity(View view) {
        // Autocomplete categories box
        final AutoCompleteTextView getCategory = new AutoCompleteTextView(this);
        getCategory.setHint("שם קטגוריה.");

        // From item container, copy all unique values of categories to autocomplete.
        Set<String> categories = new TreeSet<>(itemToCategory.values());
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                        categories.toArray(new String[0]));
        getCategory.setAdapter(adapter);

        // Get valid category from user
        new AlertDialog.Builder(this).setTitle("")
                .setMessage("בחירת קטגוריה קיימת").setView(getCategory)
                .setPositiveButton("מחיקה", (dialog, whichButton) -> {
                    String categoryName = getCategory.getText().toString().trim();

                    if (!itemToCategory.containsValue(categoryName)) {
                        finishMessage("אנא בחרי קטגוריה קיימת.", false);
                        return;
                    }
                    removeCategory(categoryName);
                    finishMessage("מחיקת קטגוריה הושלמה.", false);
                })
                .setNegativeButton("ביטול", (dialog, whichButton) -> {
                }).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void updateItem() {
        if (selectedItem == null)
            return;

        String name = editName.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String category = editCategory.getText().toString().trim();
        String price = editPrice.getText().toString().trim();

        if (invalidInput(name, category, price))
            return;

        // Define new item object and add into database under menuItems under new\old category
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("menuItems").child(category).child(name);

        MenuItemModel itemAdapter = new MenuItemModel(name, description, price);

        // if name is changed, remove old item.
        if (!name.equals(selectedItem.getName())) {
            removeItem(selectedItem.getName());
        }

        myRef.setValue(itemAdapter);
        finishMessage("עריכת פריט הושלמה.", true);
    }

    /**
     * First function of edit category, ask user which old category to rename.
     */
    private void editCategoryGetOld() {
        final AutoCompleteTextView getCategory = new AutoCompleteTextView(this);
        getCategory.setHint("שם ישן.");

        Set<String> categories = new TreeSet<>(itemToCategory.values());
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                        categories.toArray(new String[0]));
        getCategory.setAdapter(adapter);

        new AlertDialog.Builder(this).setTitle("")
                .setMessage("בחירת קטגוריה קיימת").setView(getCategory)
                .setPositiveButton("אוקיי את זה לשנות", (dialog, whichButton) -> {
                    String toChange = getCategory.getText().toString().trim();

                    if (!itemToCategory.containsValue(toChange)) {
                        finishMessage("אנא בחרי קטגוריה קיימת.", false);
                        return;
                    }
                    editCategoryGetNew(toChange);
                })
                .setNegativeButton("ביטול", (dialog, whichButton) -> {
                }).show();
    }

    /**
     * Second function of edit category, ask user the new name for the category, then deep copy and remove old one.
     */
    private void editCategoryGetNew(String toChange) {
        final EditText changeCategory = new EditText(this);
        changeCategory.setHint("שם חדש.");

        new AlertDialog.Builder(this).setTitle("")
                .setMessage("בחירת שם חדש").setView(changeCategory)
                .setPositiveButton("אוקיי לשנות את השם", (dialog, whichButton) -> {
                    String afterChange = changeCategory.getText().toString().trim();

                    if (afterChange.isEmpty() || afterChange.equals(toChange)) {
                        finishMessage("אנא בחרי שם חדש.", false);
                        return;
                    }

                    if (invalidNamePath(afterChange)) {
                        finishMessage("אנא בחרי שם תקין.", false);
                        return;
                    }

                    copyCategory(toChange, afterChange);
                    removeCategory(toChange);
                    finishMessage("עריכת קטגוריה הושלמה.", true);
                })
                .setNegativeButton("ביטול", (dialog, whichButton) -> {
                }).show();
    }

    private void copyCategory(String oldName, String newName) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("menuItems").child(newName);
        myRef.setValue(newName);

        database.getReference("menuItems").child(oldName).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item : snapshot.getChildren()) {
                            assert item.getKey() != null;
                            myRef.child(item.getKey()).setValue(item.getValue());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void removeCategory(String name) {
        FirebaseDatabase.getInstance().getReference("menuItems")
                .child(name).removeValue();
    }

    /**
     * Removes the latest item written in the text view under 'name'.
     */
    private void removeSelectedItem() {
        String name = editName.getText().toString().trim();
        try {
            if (name.isEmpty()) {
                editName.setError("אנא בחרי שם.");
                editName.requestFocus();
                return;
            }

            if (!allItems.contains(name)) {
                editName.setError("אנא בחרי פריט קיים.");
                editName.requestFocus();
                return;
            }
        } catch (Exception ignored) {

        }
        removeItem(name);
        finishMessage("הסרת פריט הושלמה.", true);
    }

    private void finishMessage(String s, boolean quit) {
        Toast t = new Toast(this);
        t.setText(s);
        t.show();

        if (quit)
            finish();
    }

    private void removeItem(String item) {
        String category = itemToCategory.get(item);
        assert category != null;
        DatabaseReference menu = FirebaseDatabase.getInstance().getReference("menuItems");
        menu.child(category).child(item).removeValue();
    }

    /**
     * Validates input of item to be added.
     *
     * @param name     product name
     * @param category product category
     * @param price    product price
     * @return false if only all the given inputs are valid and can be used to create a new item.
     */
    private boolean invalidInput(String name, String category, String price) {
        String invalid = MenuItemAddition.INVALID;
        try {
            if (name.length() < 2) {
                editName.setError("אנא בחרי שם ארוך יותר.");
                editName.requestFocus();
                return true;
            }
            if (invalidNamePath(name)) {
                editName.setError("אנא כתבי שם תקין (בלי " + invalid + ")");
                editName.requestFocus();
                return true;
            }
            if (category.isEmpty()) {
                editCategory.setError("אנא בחרי קטגוריה.");
                editCategory.requestFocus();
                return true;
            }
            if (invalidNamePath(category)) {
                editCategory.setError("אנא כתבי קטגוריה תקינה (בלי " + invalid + ")");
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
            }

            try {
                if (Double.parseDouble(price) < 0) {
                    editPrice.setError("אנא בחרי מחיר לא שלילי.");
                    editPrice.requestFocus();
                    return true;
                }
            } catch (NumberFormatException e) {
                editPrice.setError("אנא כתבי מספר תקין.");
                editPrice.requestFocus();
                return true;
            }
        } catch (Exception ignored) {
            return true;
        }
        return false;
    }

    private boolean invalidNamePath(String name) {
        String invalid = MenuItemAddition.INVALID;
        for (int i = 0; i < invalid.length(); i++) {
            if (name.indexOf(invalid.charAt(i)) != -1) {
                return true;
            }
        }
        return false;
    }

    public void editCategory(View view) {
        editCategoryGetOld();
    }
}
