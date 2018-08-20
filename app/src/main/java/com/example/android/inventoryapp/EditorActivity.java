package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.ProductDbHelper;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;

    /** EditText field to enter the product name */
    private EditText mProductNameEditText;

    /** EditText field to enter the product price */
    private EditText mProductPriceEditText;

    /** EditText field to enter the product quantity */
    private EditText mProductQuantityEditText;

    /** EditText field to enter the supplier's name */
    private EditText mSupplierNameEditText;

    /** EditText field to enter the supplier's telephone number */
    private EditText mSupplierTelephoneNoEditText;

    private Uri mCurrentProductUri;

    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new product or editing an existing one.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        // If the intent DOES NOT contain a product content URI, then we know that we are
        // creating a new product.
        if (mCurrentProductUri == null) {
            // This is a new product, so change the app bar to say "Add a Product"
            setTitle(getString(R.string.editor_activity_title_new_product));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit Product"
            setTitle(getString(R.string.editor_activity_title_edit_product));

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mProductNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mProductPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mProductQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierTelephoneNoEditText = (EditText) findViewById(R.id.edit_supplier_telephone_no);

        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductPriceEditText.setOnTouchListener(mTouchListener);
        mProductQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierTelephoneNoEditText.setOnTouchListener(mTouchListener);
    }

    private void saveProduct(){
        int productQuantity;
        int productPrice;
        String productName = mProductNameEditText.getText().toString().trim();
        String priceStr = mProductPriceEditText.getText().toString().trim();
        productPrice = 0;
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierTelephoneNo = mSupplierTelephoneNoEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(priceStr)){
            productPrice = Integer.parseInt(priceStr);
        }
//
//        while (TextUtils.isEmpty(productName)
//                || TextUtils.isEmpty(priceStr)
//                || productPrice == 0
//                || TextUtils.isEmpty(supplierName)
//                || TextUtils.isEmpty(supplierTelephoneNo)){
//
//        }

        if(!TextUtils.isEmpty(productName)
                && !TextUtils.isEmpty(priceStr)
                && productPrice > 0
                && !TextUtils.isEmpty(supplierName)
                && !TextUtils.isEmpty(supplierTelephoneNo)) {

            if (TextUtils.isEmpty(mProductQuantityEditText.getText().toString().trim())){
                productQuantity = 0;
            } else {
                productQuantity = Integer.parseInt(mProductQuantityEditText.getText().toString().trim());
                if (productQuantity < 0){
                    productQuantity = 0;
                }
            }


            ContentValues values = new ContentValues();
            values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME, productName);
            values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
            values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
            values.put(InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
            values.put(InventoryContract.ProductEntry.COLUMN_SUPPLIER_TELEPHONE_NO, supplierTelephoneNo);

            // Determine if this is a new or existing product by checking if mCurrentPetUri is null or not
            if (mCurrentProductUri == null) {
                Uri newUri = getContentResolver().insert(InventoryContract.ProductEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.product_not_saved), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, getString(R.string.product_saved), Toast.LENGTH_LONG).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.product_not_saved), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, getString(R.string.product_saved), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            if (TextUtils.isEmpty(productName)){
                Toast.makeText(this, getString(R.string.no_product_name), Toast.LENGTH_LONG).show();
            }
            if (TextUtils.isEmpty(priceStr)){
                Toast.makeText(this, getString(R.string.no_price), Toast.LENGTH_LONG).show();
            }
            if (TextUtils.isEmpty(supplierName)){
                Toast.makeText(this, getString(R.string.no_supplier), Toast.LENGTH_LONG).show();
            }
            if (TextUtils.isEmpty(supplierTelephoneNo)){
                Toast.makeText(this, getString(R.string.no_supplier_tel_no), Toast.LENGTH_LONG).show();
            }
            if (productPrice == 0){
                Toast.makeText(this, getString(R.string.price_is_zero), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                saveProduct();
                // Exit activity
                finish();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
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
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
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
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierTel = cursor.getString(supplierTelColumnIndex);

            // Update the views on the screen with the values from the database

            mProductNameEditText.setText(name);
            mProductPriceEditText.setText(Integer.toString(price));
            mProductQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierTelephoneNoEditText.setText(supplierTel);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mProductPriceEditText.setText(Integer.toString(0));
        mProductQuantityEditText.setText(Integer.toString(0));
        mSupplierNameEditText.setText("");
        mSupplierTelephoneNoEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        } else {
            // Otherwise if there are unsaved changes, setup a dialog to warn the user.
            // Create a click listener to handle the user confirming that changes should be discarded.
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, close the current activity.
                            finish();
                        }
                    };

            // Show dialog that there are unsaved changes
            showUnsavedChangesDialog(discardButtonClickListener);
        }
    }

}
