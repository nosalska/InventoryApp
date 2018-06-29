package com.example.android.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.ProductDbHelper;

public class CatalogActivity extends AppCompatActivity {

    private ProductDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = new ProductDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
    }

        @Override
        protected void onStart(){
            super.onStart();
            queryData();
        }

        private void queryData () {
            // To access our database, we instantiate our subclass of SQLiteOpenHelper
            // and pass the context, which is the current activity.
            ProductDbHelper mDbHelper = new ProductDbHelper(this);

            // Create and/or open a database to read from it
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            String[] projection = {InventoryContract.ProductEntry._ID,
                    InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME,
                    InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                    InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                    InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                    InventoryContract.ProductEntry.COLUMN_SUPPLIER_TELEPHONE_NO};
            Cursor cursor = db.query(InventoryContract.ProductEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null);

            TextView displayView = (TextView) findViewById(R.id.text_view_item);

            try {
                displayView.append("\n \n" + InventoryContract.ProductEntry._ID + " - " +
                        InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME + " - " +
                        InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE + " - " +
                        InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                        InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME + " - " +
                        InventoryContract.ProductEntry.COLUMN_SUPPLIER_TELEPHONE_NO + "\n");

                // Figure out the index of each column
                int idColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry._ID);
                int productNameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME);
                int productPriceColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE);
                int productQuantityColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
                int supplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME);
                int supplierTelephoneNoColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_SUPPLIER_TELEPHONE_NO);

                // Iterate through all the returned rows in the cursor
                while (cursor.moveToNext()) {
                    // Use that index to extract the String or Int value of the word
                    // at the current row the cursor is on.
                    int currentID = cursor.getInt(idColumnIndex);
                    String currentProductName = cursor.getString(productNameColumnIndex);
                    int currentProductPrice = cursor.getInt(productPriceColumnIndex);
                    int currentProductQuantity = cursor.getInt(productQuantityColumnIndex);
                    String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                    String currentSupplierTelephoneNo = cursor.getString(supplierTelephoneNoColumnIndex);

                    // Display the values from each column of the current row in the cursor in the TextView
                    displayView.append(("\n" + currentID + " - " +
                            currentProductName + " - " + currentProductPrice + " - " + currentProductQuantity + " - " +
                            currentSupplierName + " - " + currentSupplierTelephoneNo));
                }
            } finally {
                // Always close the cursor when you're done reading from it. This releases all its
                // resources and makes it invalid.
                cursor.close();
            }
        }
}
