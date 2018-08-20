package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProductDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + InventoryContract.ProductEntry.TABLE_NAME + " (" +
            InventoryContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
            InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, " +
            InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER DEFAULT 0, " +
            InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
            InventoryContract.ProductEntry.COLUMN_SUPPLIER_TELEPHONE_NO + " TEXT NOT NULL);";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + InventoryContract.ProductEntry.TABLE_NAME + ";";

    public ProductDbHelper (Context context){
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
}

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
