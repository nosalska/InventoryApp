package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProductCursorAdapter extends CursorAdapter {

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
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvName = (TextView) view.findViewById(R.id.product_name);
        TextView tvPrice = (TextView) view.findViewById(R.id.product_price);
        TextView tvQuantity = (TextView) view.findViewById(R.id.product_quantity);

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));

        // Populate fields with extracted properties
        tvName.setText(name);
        tvPrice.setText(Integer.toString(price));
        tvQuantity.setText(Integer.toString(quantity));

    }
}
