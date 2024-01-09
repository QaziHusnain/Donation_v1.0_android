package com.example.donation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.os.Handler;

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


        shopNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Auto-fill other fields based on the entered name
                String enteredName = editable.toString();
                autofillShopData(enteredName,shopkeeperNameEditText, mobileEditText, shopAddressEditText, masjidAmountEditText, madrassaAmountEditText);
            }
        });





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
                clearAllPersonalData();
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
        clearAllPersonalData();
    }



    private void saveShopInfo() {
        String shopName = shopNameEditText.getText().toString().replace(",", ""); // Remove commas;
        String shopkeeperName = shopkeeperNameEditText.getText().toString().replace(",", ""); // Remove commas;
        String mobile = mobileEditText.getText().toString().replace(",", ""); // Remove commas;
        String shopAddress = shopAddressEditText.getText().toString().replace(",", ""); // Remove commas;
        String masjidAmount = masjidAmountEditText.getText().toString().replace(",", ""); // Remove commas;
        String madrassaAmount = madrassaAmountEditText.getText().toString().replace(",", ""); // Remove commas;

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
                sendThankYouSMS(mobile, shopName, madrassaAmount, masjidAmount);
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

    private void sendThankYouSMS(String phoneNumber, String shopName, String mudrassaAmount, String masjidAmount) {
        try {
            android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();

            String message = "Salam! AKF Islamic Centre Pindigheb k liay Aap ki shop " + shopName +
                    " k Atiyat Box se Masjid fund k liay Rs. " + masjidAmount +
                    " aur Madrassa fund k liay Rs. " + mudrassaAmount + " wasool huay hain.";

            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Main content SMS sent successfully", Toast.LENGTH_SHORT).show();

            // Introduce a delay of, for example, 5 seconds (5000 milliseconds)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Send the additional text after the delay
                        String additionalText = "Allah Taalah Aap k karobar main barkatain ata farmaey aur Aap k jitnay aziz bahalat e Eman wafat pa chukay hain sub ki behisab maghfirat farmaey.";
                        smsManager.sendTextMessage(phoneNumber, null, additionalText, null, null);
                        Toast.makeText(getApplicationContext(), "Additional text SMS sent successfully", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error sending additional text SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, 5000); // 5000 milliseconds (5 seconds)

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error sending main content SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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

    // Modify the autofillShopData method to handle spaces between words and trim leading/trailing spaces
    private void autofillShopData(String enteredName, EditText shopkeeperName, EditText mobileEditText,
                                  EditText addressEditText, EditText masjidAmountEditText, EditText madrassaAmountEditText) {
        // Check if the entered name is empty, and clear other fields if it is
        if (enteredName.isEmpty()) {
            clearAutofillFields(shopkeeperName, mobileEditText, addressEditText, masjidAmountEditText, madrassaAmountEditText);
            return;
        }

        // Normalize the entered name by converting to lowercase and trimming leading/trailing spaces
        String normalizedEnteredName = enteredName.toLowerCase().trim();

        // Retrieve data based on the normalized entered name
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_SHOP,
                null,
                "LOWER(TRIM(" + DatabaseHelper.COLUMN_NAME_SHOP + "))=?",
                new String[]{normalizedEnteredName},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            // Extract data and fill the fields
            @SuppressLint("Range") String shopkeeper_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SHOPKEEPER_NAME));
            @SuppressLint("Range") String mobile = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MOBILE_SHOP));
            @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS_SHOP));

            // Handle null values before setting the text
            shopkeeperName.setText(shopkeeper_name != null ? shopkeeper_name : "");
            mobileEditText.setText(mobile != null ? mobile : "");
            addressEditText.setText(address != null ? address : "");

            cursor.close();
        }

        db.close();
    }


    // Add a method to clear autofill fields
    private void clearAutofillFields(EditText shopkeeperName, EditText mobileEditText, EditText addressEditText,
                                     EditText masjidAmountEditText, EditText madrassaAmountEditText) {
        shopkeeperName.setText("");
        mobileEditText.setText("");
        addressEditText.setText("");
        masjidAmountEditText.setText("");
        madrassaAmountEditText.setText("");
    }

    private void clearAllPersonalData() {
        // Create a custom password dialog
        Dialog passwordDialog = new Dialog(this);
        passwordDialog.setContentView(R.layout.password_dialog);
        passwordDialog.setCancelable(true);

        EditText editTextPasswordDialog = passwordDialog.findViewById(R.id.editTextPasswordDialog);
        Button buttonSubmitPassword = passwordDialog.findViewById(R.id.buttonSubmitPassword);

        buttonSubmitPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve the entered password from the dialog
                String enteredPassword = editTextPasswordDialog.getText().toString();

                // Replace "YOUR_PASSWORD" with the actual password you want to use
                String correctPassword = "7035";

                if (enteredPassword.equals(correctPassword)) {
                    // Password is correct, proceed to clear data
                    clearData();
                    passwordDialog.dismiss();  // Dismiss the password dialog
                } else {
                    // Password is incorrect, show a message
                    Toast.makeText(getApplicationContext(), "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Show the password dialog
        passwordDialog.show();
    }
    private void clearData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Delete all rows from the "personalinfo" table
            int rowsDeleted = db.delete(DatabaseHelper.TABLE_PERSONAL, null, null);

            if (rowsDeleted > 0) {
                Toast.makeText(this, "All personal data cleared successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No data to clear", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to clear personal data", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

}
