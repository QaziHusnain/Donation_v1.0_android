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
    public static final String COLUMN_DATE = "date";

    // Table names and column names for home information
    public static final String TABLE_HOME = "home_info";
    public static final String COLUMN_ID_HOME = "id";
    public static final String COLUMN_NAME_HOME = "name";
    public static final String COLUMN_MOBILE_HOME = "mobile";
    public static final String COLUMN_ADDRESS_HOME = "address";
    public static final String COLUMN_MASJID_AMOUNT = "masjid_amount";
    public static final String COLUMN_MADRASSA_AMOUNT = "madrassa_amount";
    public static final String COLUMN_DATE_HOME = "date";

    public static final String TABLE_SHOP = "shop_info";
    public static final String COLUMN_ID_SHOP = "id";
    public static final String COLUMN_NAME_SHOP = "shop_name";
    public static final String COLUMN_SHOPKEEPER_NAME = "shopkeeper_name";
    public static final String COLUMN_MOBILE_SHOP = "mobile";
    public static final String COLUMN_ADDRESS_SHOP = "shop_address";
    public static final String COLUMN_MASJID_AMOUNT_SHOP = "masjid_amount";
    public static final String COLUMN_MADRASSA_AMOUNT_SHOP = "madrassa_amount";
    public static final String COLUMN_DATE_SHOP = "date";

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
                    COLUMN_TYPE + " TEXT NOT NULL, " +
                    COLUMN_DATE + " TEXT NOT NULL);";

    // Create table query for home information
    private static final String CREATE_TABLE_HOME =
            "CREATE TABLE " + TABLE_HOME + " (" +
                    COLUMN_ID_HOME + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME_HOME + " TEXT NOT NULL, " +
                    COLUMN_MOBILE_HOME + " TEXT NOT NULL, " +
                    COLUMN_ADDRESS_HOME + " TEXT NOT NULL, " +
                    COLUMN_MASJID_AMOUNT + " TEXT NOT NULL, " +
                    COLUMN_MADRASSA_AMOUNT + " TEXT NOT NULL, " +
                    COLUMN_DATE_HOME + " TEXT NOT NULL);";


    // Create table SQL statement for Shop
    private static final String CREATE_TABLE_SHOP =
            "CREATE TABLE " + TABLE_SHOP + " (" +
                    COLUMN_ID_SHOP + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME_SHOP + " TEXT NOT NULL, " +
                    COLUMN_SHOPKEEPER_NAME + " TEXT NOT NULL, " +
                    COLUMN_MOBILE_SHOP + " TEXT NOT NULL, " +
                    COLUMN_ADDRESS_SHOP + " TEXT NOT NULL, " +
                    COLUMN_MASJID_AMOUNT_SHOP + " TEXT NOT NULL, " +
                    COLUMN_MADRASSA_AMOUNT_SHOP + " TEXT NOT NULL, " +
                    COLUMN_DATE_SHOP + " TEXT NOT NULL);";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_PERSONAL);
        db.execSQL(CREATE_TABLE_HOME);
        db.execSQL(CREATE_TABLE_SHOP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSONAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOP);

        // Create tables again
        onCreate(db);
    }
}
