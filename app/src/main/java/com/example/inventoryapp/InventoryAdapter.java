package com.example.inventoryapp;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.inventoryapp.database.Contract.Inventory;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ferhat on 5.6.2018.
 */

public class InventoryAdapter extends CursorAdapter {

    @BindView(R.id.getProductName) TextView productName;

    @BindView(R.id.getPrice) TextView price;

    @BindView(R.id.getQuantity) TextView quantity;

    @BindView(R.id.getSupplierName) TextView supplierName;

    @BindView(R.id.getSupplierPhoneNumber) TextView supplierPhoneNumber;

    public InventoryAdapter(Activity context, Cursor cursor) {

        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        final int productId = cursor.getInt(cursor.getColumnIndex(Inventory._ID));
        int getProductName = cursor.getColumnIndex(Inventory.PRODUCT_NAME);
        int getPrice = cursor.getColumnIndex(Inventory.PRICE);
        int getQuantity = cursor.getColumnIndex(Inventory.QUANTITY);
        int getSupplierName = cursor.getColumnIndex(Inventory.SUPPLIER_NAME);
        int getSupplierPhoneNumber = cursor.getColumnIndex(Inventory.PHONE_NUMBER);

        String mProductName = cursor.getString(getProductName);
        int mPrice = cursor.getInt(getPrice);
        final int mQuantity = cursor.getInt(getQuantity);
        String mSupplierName = cursor.getString(getSupplierName);
        int mSupplierNumber = cursor.getInt(getSupplierPhoneNumber);

        ButterKnife.bind(this, view);

        Button saleButton = view.findViewById(R.id.sale);

        if(mQuantity < 1) {

            saleButton.setEnabled(false);

        } else {

            saleButton.setEnabled(true);

        }

        saleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Uri productUri = ContentUris.withAppendedId(Inventory.CONTENT_URI, productId);

                saleProduct(productUri, mQuantity, context);

            }

        });

        productName.setText(mProductName);

        price.setText("$"+mPrice);

        quantity.setText(""+mQuantity);

        supplierName.setText(mSupplierName);

        supplierPhoneNumber.setText(""+mSupplierNumber);

    }

    private void saleProduct(Uri uri, int totalQuantity, Context context) {

        ContentValues values = new ContentValues();

        int getQuantity = 0;

        if(totalQuantity != 0) {

            getQuantity = totalQuantity - 1;

        }

        values.put(Inventory.QUANTITY, getQuantity);

        context.getContentResolver().update(uri, values, null, null);

    }

}