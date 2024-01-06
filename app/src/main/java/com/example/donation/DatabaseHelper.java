package com.example.donation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 1;

    // Table name and column names
    public static final String TABLE_NAME = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";


    // Table names and column names for personal information
    public static final String TABLE_PERSONAL = "personal_info";
    public static final String COLUMN_ID_PERSONAL = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MOBILE = "mobile";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_TYPE = "type";

    // Table names and column names for home information
    public static final String TABLE_HOME = "home_info";
    public static final String COLUMN_ID_HOME = "id";
    public static final String COLUMN_NAME_HOME = "name";
    public static final String COLUMN_MOBILE_HOME = "mobile";
    public static final String COLUMN_ADDRESS_HOME = "address";
    public static final String COLUMN_MASJID_AMOUNT = "masjid_amount";
    public static final String COLUMN_MADRASSA_AMOUNT = "madrassa_amount";

    // Create table query
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL);";

    // Create table query for personal information
    private static final String CREATE_TABLE_PERSONAL =
            "CREATE TABLE " + TABLE_PERSONAL + " (" +
                    COLUMN_ID_PERSONAL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_MOBILE + " TEXT NOT NULL, " +
                    COLUMN_ADDRESS + " TEXT NOT NULL, " +
                    COLUMN_AMOUNT + " TEXT NOT NULL, " +
                    COLUMN_TYPE + " TEXT NOT NULL);";

    // Create table query for home information
    private static final String CREATE_TABLE_HOME =
            "CREATE TABLE " + TABLE_HOME + " (" +
                    COLUMN_ID_HOME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME_HOME + " TEXT NOT NULL, " +
                    COLUMN_MOBILE_HOME + " TEXT NOT NULL, " +
                    COLUMN_ADDRESS_HOME + " TEXT NOT NULL, " +
                    COLUMN_MASJID_AMOUNT + " TEXT NOT NULL, " +
                    COLUMN_MADRASSA_AMOUNT + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_PERSONAL);
        db.execSQL(CREATE_TABLE_HOME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement if needed
    }
}
