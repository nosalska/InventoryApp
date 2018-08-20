package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;

public class ProductCursorAdapter extends CursorAdapter {

    int clicked = 0;
    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     */

    @Override
     public View newView(Context context, Cursor cursor, ViewGroup parent) {
     return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
     }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvName = (TextView) view.findViewById(R.id.product_name);
        TextView tvPrice = (TextView) view.findViewById(R.id.product_price);
        TextView tvQuantity = (TextView) view.findViewById(R.id.product_quantity);
        final Button button = view.findViewById(R.id.sale_button);

        // Extract properties from cursor
        final int columnIndex = cursor.getInt(cursor.getColumnIndex(InventoryContract.ProductEntry._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));


        // Populate fields with extracted properties
        tvName.setText(name);
        tvPrice.setText(Integer.toString(price));
        tvQuantity.setText(Integer.toString(quantity));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = ContentUris.withAppendedId(InventoryContract.ProductEntry.CONTENT_URI, columnIndex);
                if (quantity != 0) {
                    int newQuantity = quantity - 1;

                    if (newQuantity == 0) {
                        Toast.makeText(context, R.string.product_gone, Toast.LENGTH_SHORT).show();
                    }

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                    context.getContentResolver().update(uri, contentValues, null, null);

                } else{
                    Toast.makeText(context, R.string.product_gone, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
