package com.example.donation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;

import java.io.FileOutputStream;
import java.io.IOException;
import android.Manifest;
import java.text.SimpleDateFormat;
import java.util.Date;




public class personal_activity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 123; // You can use any integer value here

    private EditText nameEditText, mobileEditText, addressEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity);

        dbHelper = new DatabaseHelper(this);

        EditText nameEditText = findViewById(R.id.editTextName);
        EditText mobileEditText = findViewById(R.id.editTextMobile);
        EditText addressEditText = findViewById(R.id.editTextAddress);
        final EditText amountEditText = findViewById(R.id.editTextAmount);
        final Spinner typeSpinner = findViewById(R.id.spinnerType);
        Button saveButton = findViewById(R.id.buttonSave);


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
                // Auto-fill address and mobile based on the entered name
                String enteredName = editable.toString();
                autofillData(enteredName, mobileEditText, addressEditText);
            }
        });



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String mobile = mobileEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String amount = amountEditText.getText().toString();
                String type = typeSpinner.getSelectedItem().toString();

                // Validate if any of the fields are empty
                if (name.isEmpty() || mobile.isEmpty() || address.isEmpty() || amount.isEmpty()) {
                    Toast.makeText(personal_activity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    savePersonalInfo(name, mobile, address, amount, type);
                }
            }
        });




        Button clearDataButton = findViewById(R.id.buttonClearData);
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

    private void savePersonalInfo(String name, String mobile, String address, String amount, String type) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NAME, name);
            values.put(DatabaseHelper.COLUMN_MOBILE, mobile);
            values.put(DatabaseHelper.COLUMN_ADDRESS, address);
            values.put(DatabaseHelper.COLUMN_AMOUNT, amount);
            values.put(DatabaseHelper.COLUMN_TYPE, type);
            String currentDate = getCurrentDate();
            values.put(DatabaseHelper.COLUMN_DATE, currentDate);
            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(DatabaseHelper.TABLE_PERSONAL, null, values);

            if (newRowId != -1) {
                Toast.makeText(this, "Personal information saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save personal information", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save personal information", Toast.LENGTH_SHORT).show();
        } finally {
            db.close();

            // Send Thank You SMS
            sendThankYouSMS(mobile,name,amount,type);
        }
    }

    private void sendThankYouSMS(final String phoneNumber, final String name, final String amount, final String type) {
        try {
            // Check if SMS permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the SMS permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            } else {
                // Permission already granted, proceed with sending SMS
                sendThankYouSMSAfterPermission(phoneNumber, name, amount, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error checking SMS permission: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void sendThankYouSMSAfterPermission(final String phoneNumber, final String name, final String amount, final String type) {
        try {
            android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();

            String message = "Salam " + name + " Sahib! AKF Islamic Centre Pindigheb k liay Aap ki taraf se Rs. " + amount + "(" + type + ")  atia wasool hua hai.";

            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Main content SMS sent successfully", Toast.LENGTH_SHORT).show();

            // Introduce a delay of, for example, 5 seconds (5000 milliseconds)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Send the additional text after the delay
                        String additionalText = "Allah Taalah Aap k Rizq main Barkatain ata farmaey aur Aap k jitnay aziz bahalat e Eman wafat pa chukay hain sub ki behisab maghfirat farmaey.";
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

    private void openShowAllDataActivity() {
        Intent intent = new Intent(this, ShowAllDataActivity.class);
        startActivity(intent);
    }
    private void autofillData(String enteredName, EditText mobileEditText, EditText addressEditText) {
        // Retrieve data based on the entered name
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PERSONAL,
                null,
                DatabaseHelper.COLUMN_NAME + "=?",
                new String[]{enteredName},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            // Extract data and fill the address and mobile fields
            @SuppressLint("Range") String mobile = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MOBILE));
            @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS));

            mobileEditText.setText(mobile);
            addressEditText.setText(address);

            cursor.close();
        }

        db.close();
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
    private String getCurrentDate() {
        // Get current date
        Date currentDate = new Date();

        // Format the date as a string without time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(currentDate);
    }


}