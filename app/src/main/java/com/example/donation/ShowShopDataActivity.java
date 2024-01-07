package com.example.donation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class ShowShopDataActivity extends AppCompatActivity {
    private List<String> dataList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        dataList = getAllShopData();

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

    private List<String> getAllShopData() {
        // Retrieve all home data from the database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
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

                String rowData = name + "," + shopkeeper_name+ "," + mobile + "," + address + "," + masjidAmount + "," + madrassaAmount;
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
        String shopkeeper_name=rowData[1];
        String mobile = rowData[2];
        String address = rowData[3];
        String masjidAmount = rowData[4];
        String madrassaAmount = rowData[5];

        String whereClause = DatabaseHelper.COLUMN_NAME_SHOP + "=? AND " +
                DatabaseHelper.COLUMN_SHOPKEEPER_NAME+ "=? AND " +
                DatabaseHelper.COLUMN_MOBILE_SHOP + "=? AND " +
                DatabaseHelper.COLUMN_ADDRESS_SHOP + "=? AND " +
                DatabaseHelper.COLUMN_MASJID_AMOUNT_SHOP + "=? AND " +
                DatabaseHelper.COLUMN_MADRASSA_AMOUNT_SHOP + "=?";

        String[] whereArgs = {name,shopkeeper_name,mobile, address, masjidAmount, madrassaAmount};

        db.delete(DatabaseHelper.TABLE_SHOP, whereClause, whereArgs);
        db.close();

        // Remove the item from the list and update the adapter
        dataList.remove(position);
        adapter.notifyDataSetChanged();
    }

    private void calculateTotalAmount() {
        // Calculate the total sum of the "masjid_amount" and "madrassa_amount" columns
        double totalMasjidAmount = 0;
        double totalMadrassaAmount = 0;

        for (String rowData : dataList) {
            String[] values = rowData.split(",");
            if (values.length > 4) {
                // Assuming the masjid_amount is at index 3 and madrassa_amount is at index 4 in the rowData
                String masjidAmountString = values[4].trim();
                String madrassaAmountString = values[5].trim();

                try {
                    double masjidAmount = Double.parseDouble(masjidAmountString);
                    double madrassaAmount = Double.parseDouble(madrassaAmountString);
                    totalMasjidAmount += masjidAmount;
                    totalMadrassaAmount += madrassaAmount;
                } catch (NumberFormatException e) {
                    // Handle parsing errors if needed
                    e.printStackTrace();
                }
            }
        }

        // Display the total sums in a single toast
        String totalAmountMessage = "Total Masjid Amount: " + totalMasjidAmount +
                "\nTotal Madrassa Amount: " + totalMadrassaAmount;

        Toast.makeText(this, totalAmountMessage, Toast.LENGTH_SHORT).show();
    }

}
