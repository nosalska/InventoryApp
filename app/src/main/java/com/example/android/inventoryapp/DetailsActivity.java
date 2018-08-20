package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.ProductDbHelper;

import static android.net.Uri.parse;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;

    /** TextView field to populate the product name */
    private TextView mProductNameTextView;

    /** TextView field to populate the product price */
    private TextView mProductPriceTextView;

    /** TextView field to populate the product quantity */
    private TextView mProductQuantityTextView;

    /** TextView field to populate the supplier's name */
    private TextView mSupplierNameTextView;

    /** TextView field to populate the supplier's telephone number */
    private TextView mSupplierTelephoneNoTextView;

    private Button callSupplierButton;

    private Button decreaseButton;

    private Button increaseButton;

    private Uri mCurrentProductUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Examine the intent that was used to launch this activity,

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);

        callSupplierButton = (Button) findViewById(R.id.call_supplier);
        decreaseButton = (Button) findViewById(R.id.decrease_quantity);
        increaseButton = (Button) findViewById(R.id.increase_quantity);

        mProductNameTextView = (TextView) findViewById(R.id.view_product_name);
        mProductPriceTextView = (TextView) findViewById(R.id.view_product_price);
        mProductQuantityTextView = (TextView) findViewById(R.id.view_product_quantity);
        mSupplierNameTextView = (TextView) findViewById(R.id.view_supplier_name);
        mSupplierTelephoneNoTextView = (TextView) findViewById(R.id.view_supplier_telephone_no);

        callSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tel = mSupplierTelephoneNoTextView.getText().toString().trim();
                Intent callSupplierIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
                startActivity(callSupplierIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_edit:
                Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);
                intent.setData(mCurrentProductUri);
                startActivity(intent);
                // Exit activity
                finish();
                return true;
            case R.id.action_delete:
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, close the current activity.
                                finish();
                            }
                        };
                showDeleteConfirmationDialog(discardButtonClickListener);
                return true;
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the products table
        String[] projection = {
                InventoryContract.ProductEntry._ID,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.ProductEntry.COLUMN_SUPPLIER_TELEPHONE_NO};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, final Cursor cursor) {
        // Return if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierTelColumnIndex = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_SUPPLIER_TELEPHONE_NO);


            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            final int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierTel = cursor.getString(supplierTelColumnIndex);

            // Update the views on the screen with the values from the database

            mProductNameTextView.setText(name);
            mProductPriceTextView.setText(Integer.toString(price));
            mProductQuantityTextView.setText(Integer.toString(quantity));
            mSupplierNameTextView.setText(supplierName);
            mSupplierTelephoneNoTextView.setText(supplierTel);

            decreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    decreaseQuantity(quantity);
                }
            });

            increaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    increaseQuantity(quantity);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mProductNameTextView.setText("");
        mProductPriceTextView.setText(Integer.toString(0));
        mProductQuantityTextView.setText(Integer.toString(0));
        mSupplierNameTextView.setText("");
        mSupplierTelephoneNoTextView.setText("");
    }

    private void showDeleteConfirmationDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue viewing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteProduct() {
        int numberOfDeletedRows = 0;
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the product that we want.
            numberOfDeletedRows = getContentResolver().delete(mCurrentProductUri, null, null);

        if (numberOfDeletedRows == 0){
            Toast.makeText(this, getString(R.string.delete_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.delete_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void decreaseQuantity(int quantity){
        if (quantity != 0){
            int newQuantity = quantity - 1;

            if (newQuantity == 0) {
                Toast.makeText(this, R.string.product_gone, Toast.LENGTH_SHORT).show();
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
            getContentResolver().update(mCurrentProductUri, contentValues, null, null);

        } else{
            Toast.makeText(this, R.string.product_gone, Toast.LENGTH_SHORT).show();
        }
    }

    private void increaseQuantity(int quantity){
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity + 1);
        getContentResolver().update(mCurrentProductUri, contentValues, null, null);
    }

}
