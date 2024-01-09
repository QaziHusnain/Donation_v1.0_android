package com.example.donation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.donation.R;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;




public class ShowAllDataActivity extends AppCompatActivity {

    private List<String> dataList;
    private static final int REQUEST_CODE_PICK_DIRECTORY = 2;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_DIRECTORY && resultCode == RESULT_OK) {
            Uri treeUri = data.getData();

            if (treeUri != null) {
                // Perform the export with the selected directory URI
                extractDataToCsv(getAllPersonalData(), treeUri);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);
        dataList = getAllPersonalData();

        // Display the data in a ListView
        displayData(dataList);

        // Set up the ListView item click listener
        ListView listView = findViewById(R.id.listViewAllData);
        Button extractButton = findViewById(R.id.buttonExtractExcel);
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
        extractButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open a system file picker to let the user choose a directory
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(intent, REQUEST_CODE_PICK_DIRECTORY);
            }
        });
        // Check and request write external storage permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
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

    private void extractDataToCsv(List<String> dataList, Uri treeUri) {
        try {
            // Use the selected directory URI for file creation
            Uri docUri = DocumentsContract.buildDocumentUriUsingTree(treeUri,
                    DocumentsContract.getTreeDocumentId(treeUri));

            // Get the document's display name
            String displayName = "personal_data.csv";

            // Create the CSV file within the selected directory
            Uri fileUri = DocumentsContract.createDocument(getContentResolver(), docUri, "text/csv", displayName);

            // Open an OutputStream to write data to the file
            try (OutputStream outputStream = getContentResolver().openOutputStream(fileUri)) {
                if (outputStream != null) {
                    // Write header
                    outputStream.write("Name,Mobile,Address,Amount,Type\n".getBytes());

                    // Write data
                    for (String data : dataList) {
                        outputStream.write((data + "\n").getBytes());
                    }

                    Toast.makeText(this, "Data exported to " + fileUri.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error exporting data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



}
