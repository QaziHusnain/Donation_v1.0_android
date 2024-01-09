// home_activity.java
package com.example.donation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class home_activity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        EditText nameEditText = findViewById(R.id.editTextNameHome);
        EditText mobileEditText = findViewById(R.id.editTextMobileHome);
        EditText addressEditText = findViewById(R.id.editTextAddressHome);
        EditText masjidAmountEditText = findViewById(R.id.editTextMasjidAmount);
        EditText madrassaAmountEditText = findViewById(R.id.editTextMadrassaAmount);
        Button saveButton = findViewById(R.id.buttonSaveHome);


        // Add a TextWatcher to the nameEditText to listen for changes
        nameEditText.addTextChangedListener(new TextWatcher() {
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
                autofillHomeData(enteredName, mobileEditText, addressEditText, masjidAmountEditText, madrassaAmountEditText);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered data
                String name = nameEditText.getText().toString().replace(",", "");
                String mobile = mobileEditText.getText().toString().replace(",", "");
                String address = addressEditText.getText().toString().replace(",", "");
                String masjidAmount = masjidAmountEditText.getText().toString().replace(",", "");
                String madrassaAmount = madrassaAmountEditText.getText().toString().replace(",", "");

                // Validate if any of the fields are empty
                if (name.isEmpty() || mobile.isEmpty() || address.isEmpty() || masjidAmount.isEmpty() || madrassaAmount.isEmpty()) {
                    Toast.makeText(home_activity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Save the data (you can implement this method)
                    saveHomeInfo(name, mobile, address, masjidAmount, madrassaAmount);
                }
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

    private void saveHomeInfo(String name, String mobile, String address, String masjidAmount, String madrassaAmount) {
        // Get an instance of the database helper
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Get a writable database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Create a ContentValues object to store the data
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NAME_HOME, name);
            values.put(DatabaseHelper.COLUMN_MOBILE_HOME, mobile);
            values.put(DatabaseHelper.COLUMN_ADDRESS_HOME, address);
            values.put(DatabaseHelper.COLUMN_MASJID_AMOUNT, masjidAmount);
            values.put(DatabaseHelper.COLUMN_MADRASSA_AMOUNT, madrassaAmount);
            String currentDate = getCurrentDate();
            values.put(DatabaseHelper.COLUMN_DATE_HOME, currentDate);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(DatabaseHelper.TABLE_HOME, null, values);

            if (newRowId != -1) {
                // Data is successfully inserted
                Toast.makeText(this, "Home information saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Failed to insert data
                Toast.makeText(this, "Failed to save home information", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Handle exceptions, if any
            e.printStackTrace();
            Toast.makeText(this, "Failed to save home information", Toast.LENGTH_SHORT).show();
        } finally {
            // Close the database connection
            db.close();
            sendThankYouSMS(mobile,name,madrassaAmount,masjidAmount);
        }
    }

    private void sendThankYouSMS(String phoneNumber, String name, String mudrassaAmount, String masjidAmount) {
        try {
            android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();


            String message = "Salam! AKF Islamic Centre Pindigheb k liay Aap k Ghar k atiat box se Masjid fund k liay Rs. " + masjidAmount +
                    " aur Madrassa fund k liay Rs. " + mudrassaAmount + " wasool huay hain.";

            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Main content SMS sent successfully", Toast.LENGTH_SHORT).show();

            // Introduce a delay of, for example, 5 seconds (5000 milliseconds)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Send the additional text after the delay
                        String additionalText = "Allah Taalah Aap k ghar per khasoosi barkaton ka nazol farmaey aur Aap k jitnay aziz bahalat e Eman wafat pa chukay hain sub ki behisab maghfirat farmaey.";
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
            File file = new File(myDonationsDir, "Home_data.csv");
            FileWriter writer = new FileWriter(file);

            // Write header
            writer.write("Name,Mobile,Address,Masjid Amount,Mudrassa Amount\n");

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

    private List<String> getAllHomeData() {
        // Retrieve all home data from the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> dataList = new ArrayList<>();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_HOME,
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
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME_HOME));
                @SuppressLint("Range") String mobile = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MOBILE_HOME));
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS_HOME));
                @SuppressLint("Range") String masjidAmount = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MASJID_AMOUNT));
                @SuppressLint("Range") String madrassaAmount = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MADRASSA_AMOUNT));

                String rowData = name + "," + mobile + "," + address + "," + masjidAmount + "," + madrassaAmount;
                dataList.add(rowData);

            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        return dataList;
    }

    public void onClearDataButtonClickHome(View view) {
        clearAllPersonalData();
    }



    private void autofillHomeData(String enteredName, EditText mobileEditText, EditText addressEditText,
                                  EditText masjidAmountEditText, EditText madrassaAmountEditText) {

        // Check if the entered name is empty, and clear other fields if it is
        if (enteredName.isEmpty()) {
            mobileEditText.setText("");
            addressEditText.setText("");
            masjidAmountEditText.setText("");
            madrassaAmountEditText.setText("");
            return;
        }
        // Retrieve data based on the entered name
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_HOME,
                null,
                DatabaseHelper.COLUMN_NAME_HOME + "=?",
                new String[]{enteredName},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            // Extract data and fill the fields
            @SuppressLint("Range") String mobile = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MOBILE_HOME));
            @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS_HOME));


            mobileEditText.setText(mobile);
            addressEditText.setText(address);


            cursor.close();
        }

        db.close();
    }
    private void openShowAllDataActivity() {
        Intent intent = new Intent(this, ShowHomeDataActivity.class);
        startActivity(intent);
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
            int rowsDeleted = db.delete(DatabaseHelper.TABLE_HOME, null, null);

            if (rowsDeleted > 0) {
                Toast.makeText(this, "All home data cleared successfully", Toast.LENGTH_SHORT).show();
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
    private String getCurrentDate() {
        // Get current date
        Date currentDate = new Date();

        // Format the date as a string without time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(currentDate);
    }





}
