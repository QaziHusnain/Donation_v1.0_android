package com.example.donation;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.donation.DatabaseHelper;

public class SignUpActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dbHelper = new DatabaseHelper(this);

        final EditText newUsernameEditText = findViewById(R.id.editTextNewUsername);
        final EditText newPasswordEditText = findViewById(R.id.editTextNewPassword);

        Button signUpButton = findViewById(R.id.buttonSignUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = newUsernameEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();

                // Validate input and insert into the database
                if (!newUsername.isEmpty() && !newPassword.isEmpty()) {
                    saveCredentialsToDatabase(newUsername, newPassword);
                    finish(); // Finish the activity or navigate to login
                } else {
                    // Handle validation error (e.g., show a toast)
                }
            }
        });
    }

    private void saveCredentialsToDatabase(String username, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, username);
        values.put(DatabaseHelper.COLUMN_PASSWORD, password);
        db.insert(DatabaseHelper.TABLE_NAME, null, values);
        db.close();
    }
}
