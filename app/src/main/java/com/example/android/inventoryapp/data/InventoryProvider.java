package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class InventoryProvider extends ContentProvider {
    //Constant variable, so all log messages from the InventoryProvider will have the same log tag identifier when you are reading the system logs.
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private ProductDbHelper mDbHelper;

    /** URI matcher code for the content URI for the inventory table */
    private static final int PRODUCTS = 100;

    /** URI matcher code for the content URI for a single item in the inventory table */
    private static final int PRODUCT_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    //Static block is used for initializing the static variables.This block gets executed when the class is loaded in the memory.
    // A class can have multiple Static blocks, which will execute in the same sequence in which they have been written into the program.
    //The static initialization block is only executed once (when the class is loaded), no matter how many instances of the class there are.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return InventoryContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return InventoryContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = InventoryContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch(match){
            case PRODUCTS:
                cursor = database.query(InventoryContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = InventoryContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URi" + uri);
        }
        //Set notification URI on the Cursor,
        //so we know what content URI the Cursor was created for.
        //If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        // Track the number of rows that were deleted
        int rowsDeleted;
        switch (match){
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(InventoryContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI,
                // so we know which row to delete. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = InventoryContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                // Delete a single row given by the ID in the URI
                rowsDeleted = database.delete(InventoryContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Check that the name is not null
        String name = values.getAsString(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product has to be named.");
        }

        Integer price = values.getAsInteger(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Price has to be inserted and has to be a positive number.");
        }

        Integer quantity = values.getAsInteger(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity < 0 ){
            throw new IllegalArgumentException("Quantity has to be a non-negative value.");
        }

        String supplierName = values.getAsString(InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Supplier name has to be inserted.");
        }

        String supplierTelephoneNo = values.getAsString(InventoryContract.ProductEntry.COLUMN_SUPPLIER_TELEPHONE_NO);
        if (name == null) {
            throw new IllegalArgumentException("Supplier telephone no has to be inserted.");
        }

        // Insert the new product with the given values
        long id = database.insert(InventoryContract.ProductEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify all listeners that the data has changed for the product content URI.
        //uri: content://com.example.android.products/products
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Update products in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more products).
     * Return the number of rows that were successfully updated.
     */
    private int updateProduct (Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        if (contentValues.containsKey(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME)){
            String name = contentValues.getAsString(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product has to be named.");
            }
        }
        if (contentValues.containsKey(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE)){
            Integer price = contentValues.getAsInteger(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || price <= 0) {
                throw new IllegalArgumentException("Price has to be inserted and has to be a positive number.");
            }
        }
        if(contentValues.containsKey(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)){
            Integer quantity = contentValues.getAsInteger(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null && quantity < 0 ){
                throw new IllegalArgumentException("Quantity has to be inserted and must be a non-negative value.");
            }
        }
        if (contentValues.containsKey(InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME)){
            String supplierName = contentValues.getAsString(InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Supplier name has to be inserted.");
            }
        }
        if (contentValues.containsKey(InventoryContract.ProductEntry.COLUMN_SUPPLIER_TELEPHONE_NO)){
            String supplierTelephoneNo = contentValues.getAsString(InventoryContract.ProductEntry.COLUMN_SUPPLIER_TELEPHONE_NO);
            if (supplierTelephoneNo == null) {
                throw new IllegalArgumentException("Supplier telephone no has to be inserted.");
            }
        }

        if (contentValues.size() == 0){
            return 0;
        }

        int numberOfUpdatedRows = database.update(InventoryContract.ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (numberOfUpdatedRows == 0) {
            Log.e(LOG_TAG, "No updated rows");
        }
        else {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfUpdatedRows;
    }
}
