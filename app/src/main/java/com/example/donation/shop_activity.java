package com.example.donation;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class shop_activity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText shopNameEditText, shopkeeperNameEditText, shopAddressEditText, masjidAmountEditText, madrassaAmountEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity);

        dbHelper = new DatabaseHelper(this);

        shopNameEditText = findViewById(R.id.editTextShopName);
        shopkeeperNameEditText = findViewById(R.id.editTextShopkeeperName);
        shopAddressEditText = findViewById(R.id.editTextShopAddress);
        masjidAmountEditText = findViewById(R.id.editTextMasjidAmount);
        madrassaAmountEditText = findViewById(R.id.editTextMadrassaAmount);

        Button saveButton = findViewById(R.id.buttonSaveShop);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveShopInfo();
            }
        });
    }

    private void saveShopInfo() {
        String shopName = shopNameEditText.getText().toString();
        String shopkeeperName = shopkeeperNameEditText.getText().toString();
        String shopAddress = shopAddressEditText.getText().toString();
        String masjidAmount = masjidAmountEditText.getText().toString();
        String madrassaAmount = madrassaAmountEditText.getText().toString();

        // Validate if any of the fields are empty
        if (shopName.isEmpty() || shopkeeperName.isEmpty() || shopAddress.isEmpty() || masjidAmount.isEmpty() || madrassaAmount.isEmpty()) {
            Toast.makeText(shop_activity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            try {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_NAME_SHOP, shopName);
                values.put(DatabaseHelper.COLUMN_SHOPKEEPER_NAME, shopkeeperName);
                values.put(DatabaseHelper.COLUMN_ADDRESS_SHOP, shopAddress);
                values.put(DatabaseHelper.COLUMN_MASJID_AMOUNT_SHOP, masjidAmount);
                values.put(DatabaseHelper.COLUMN_MADRASSA_AMOUNT_SHOP, madrassaAmount);

                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(DatabaseHelper.TABLE_SHOP, null, values);

                if (newRowId != -1) {
                    Toast.makeText(this, "Shop information saved successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(this, "Failed to save shop information", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save shop information", Toast.LENGTH_SHORT).show();
            } finally {
                db.close();
            }
        }
    }

    private void clearFields() {
        shopNameEditText.setText("");
        shopkeeperNameEditText.setText("");
        shopAddressEditText.setText("");
        masjidAmountEditText.setText("");
        madrassaAmountEditText.setText("");
    }
}
