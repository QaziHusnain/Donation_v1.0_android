package com.example.donation;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class shop_activity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText shopNameEditText, shopkeeperNameEditText,mobileEditText, shopAddressEditText, masjidAmountEditText, madrassaAmountEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_activity);

        dbHelper = new DatabaseHelper(this);

        shopNameEditText = findViewById(R.id.editTextShopName);
        shopkeeperNameEditText = findViewById(R.id.editTextShopkeeperName);
        mobileEditText = findViewById(R.id.editTextMobileShop);
        shopAddressEditText = findViewById(R.id.editTextShopAddress);
        masjidAmountEditText = findViewById(R.id.editTextMasjidAmount);
        madrassaAmountEditText = findViewById(R.id.editTextMadrassaAmount);

        Button saveButton = findViewById(R.id.buttonSaveShop);
        Button extractButton = findViewById(R.id.buttonExtractExcel);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveShopInfo();
            }
        });
        Button clearDataButton = findViewById(R.id.buttonClearDataShop);
        clearDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAllShopData();
            }
        });
        extractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extractDataToCsv(getAllShopData());
            }
        });
        Button showAllDataButton = findViewById(R.id.buttonShowAllData);
        showAllDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openShowAllDataActivity();
            }
        });
    }
    public void onClearDataButtonClickShop(View view) {
        clearAllShopData();
    }
    private void clearAllShopData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Delete all rows from the "shop_info" table
            int rowsDeleted = db.delete(DatabaseHelper.TABLE_SHOP, null, null);

            if (rowsDeleted > 0) {
                Toast.makeText(this, "All shop data cleared successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No data to clear", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to clear shop data", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }


    private void saveShopInfo() {
        String shopName = shopNameEditText.getText().toString();
        String shopkeeperName = shopkeeperNameEditText.getText().toString();
        String mobile = mobileEditText.getText().toString();
        String shopAddress = shopAddressEditText.getText().toString();
        String masjidAmount = masjidAmountEditText.getText().toString();
        String madrassaAmount = madrassaAmountEditText.getText().toString();

        // Validate if any of the fields are empty
        if (shopName.isEmpty() || shopkeeperName.isEmpty()|| mobile.isEmpty() || shopAddress.isEmpty() || masjidAmount.isEmpty() || madrassaAmount.isEmpty()) {
            Toast.makeText(shop_activity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            try {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_NAME_SHOP, shopName);
                values.put(DatabaseHelper.COLUMN_SHOPKEEPER_NAME, shopkeeperName);
                values.put(DatabaseHelper.COLUMN_MOBILE_SHOP, mobile);
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
                sendThankYouSMS(mobile);
            }
        }
    }

    private void clearFields() {
        shopNameEditText.setText("");
        shopkeeperNameEditText.setText("");
        mobileEditText.setText("");
        shopAddressEditText.setText("");
        masjidAmountEditText.setText("");
        madrassaAmountEditText.setText("");
    }

    private void sendThankYouSMS(String phoneNumber) {
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        String message = "Thank you for your donation!";
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private List<String> getAllShopData() {
        // Retrieve all home data from the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> dataList = new ArrayList<>();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_SHOP,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Extract data and add to the list
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME_SHOP));
                @SuppressLint("Range") String shopkeeper_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SHOPKEEPER_NAME));
                @SuppressLint("Range") String mobile = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MOBILE_SHOP));
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS_SHOP));
                @SuppressLint("Range") String masjidAmount = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MASJID_AMOUNT_SHOP));
                @SuppressLint("Range") String madrassaAmount = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MADRASSA_AMOUNT_SHOP));

                String rowData = name + "," + shopkeeper_name +"," + mobile + "," + address + "," + masjidAmount + "," + madrassaAmount;
                dataList.add(rowData);

            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        return dataList;
    }
    private void extractDataToCsv(List<String> dataList) {
        try {
            // Get the "Downloads" directory
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            // Create a directory named "MyDonations" within the "Downloads" directory
            File myDonationsDir = new File(downloadsDir, "MyDonations");
            if (!myDonationsDir.exists()) {
                myDonationsDir.mkdirs();
            }

            // Create the CSV file within the "MyDonations" directory
            File file = new File(myDonationsDir, "Shop_data.csv");
            FileWriter writer = new FileWriter(file);

            // Write header
            writer.write("Name,Shopkeeper Name,Mobile,Address,Masjid Amount,Mudrassa Amount\n");

            // Write data
            for (String data : dataList) {
                writer.write(data + "\n");
            }

            writer.flush();
            writer.close();

            Toast.makeText(this, "Data exported to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error exporting data", Toast.LENGTH_SHORT).show();
        }
    }
    private void openShowAllDataActivity() {
        Intent intent = new Intent(this, ShowShopDataActivity.class);
        startActivity(intent);
    }
}
