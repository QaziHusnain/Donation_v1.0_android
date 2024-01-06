package com.example.donation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.donation.R;

import java.util.ArrayList;
import java.util.List;




public class ShowAllDataActivity extends AppCompatActivity {

    private List<String> dataList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        dataList = getAllPersonalData();

        // Display the data in a ListView
        displayData(dataList);

        // Set up the ListView item click listener
        ListView listView = findViewById(R.id.listViewAllData);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteConfirmationDialog(position);
            }
        });
        // Set up the Total Collection button click listener
        Button btnTotalCollection = findViewById(R.id.btnTotalCollection);
        btnTotalCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTotalAmount();
            }
        });
    }


    private List<String> getAllPersonalData() {
        // Retrieve all personal data from the database
        DatabaseHelper dbHelper = new DatabaseHelper(this); // Instantiate DatabaseHelper
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> dataList = new ArrayList<>();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_PERSONAL,
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
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
                @SuppressLint("Range") String mobile = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MOBILE));
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS));
                @SuppressLint("Range") String amount = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT));
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE));

                String rowData = name + "," + mobile + "," + address + "," + amount + "," + type;
                dataList.add(rowData);

            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();

        return dataList;
    }


    private void displayData(List<String> dataList) {
        // Use a ListView to display the data
        ListView listView = findViewById(R.id.listViewAllData);

        // Create an ArrayAdapter to populate the ListView
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                dataList
        );

        // Set the adapter for the ListView
        listView.setAdapter(adapter);
    }
    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Item");
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(position);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteItem(int position) {
        // Delete the item from the database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] rowData = dataList.get(position).split(",");
        String name = rowData[0];
        String mobile = rowData[1];
        String address = rowData[2];
        String amount = rowData[3];
        String type = rowData[4];

        String whereClause = DatabaseHelper.COLUMN_NAME + "=? AND " +
                DatabaseHelper.COLUMN_MOBILE + "=? AND " +
                DatabaseHelper.COLUMN_ADDRESS + "=? AND " +
                DatabaseHelper.COLUMN_AMOUNT + "=? AND " +
                DatabaseHelper.COLUMN_TYPE + "=?";

        String[] whereArgs = {name, mobile, address, amount, type};

        db.delete(DatabaseHelper.TABLE_PERSONAL, whereClause, whereArgs);
        db.close();

        // Remove the item from the list and update the adapter
        dataList.remove(position);
        adapter.notifyDataSetChanged();
    }

    public void calculateTotalAmount() {
        // Calculate the total sum of the "amount" column
        double totalAmount = 0;

        for (String rowData : dataList) {
            String[] values = rowData.split(",");
            if (values.length > 3) {
                // Assuming the amount is at index 3 in the rowData
                String amountString = values[3].trim();
                try {
                    double amount = Double.parseDouble(amountString);
                    totalAmount += amount;
                } catch (NumberFormatException e) {
                    // Handle parsing errors if needed
                    e.printStackTrace();
                }
            }
        }

        // Display the total sum in a toast
        String totalAmountMessage = "Total Collection: " + totalAmount;
        Toast.makeText(this, totalAmountMessage, Toast.LENGTH_SHORT).show();
    }



}
