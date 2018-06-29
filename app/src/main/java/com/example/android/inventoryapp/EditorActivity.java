package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.ProductDbHelper;

public class EditorActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mProductNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mProductPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mProductQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierTelephoneNoEditText = (EditText) findViewById(R.id.edit_supplier_telephone_no);
    }

    private void insertProduct(){
        String toastText;
        String productName = mProductNameEditText.getText().toString().trim();
        int productPrice = Integer.parseInt(mProductPriceEditText.getText().toString().trim());
        int productQuantity = Integer.parseInt(mProductQuantityEditText.getText().toString().trim());
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierTelephoneNo = mSupplierTelephoneNoEditText.getText().toString().trim();

        ProductDbHelper mDbHelper = new ProductDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        values.put(InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(InventoryContract.ProductEntry.COLUMN_SUPPLIER_TELEPHONE_NO, supplierTelephoneNo);

        long newRowId = db.insert(InventoryContract.ProductEntry.TABLE_NAME, null, values);
        if (newRowId != -1) {
            toastText = "Product saved with ID " + newRowId;
        } else toastText = "Error with saving product";
        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
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
                insertProduct();
                // Exit activity
                finish();
                return true;
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
