package com.example.inventoryapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.inventoryapp.database.Contract.Inventory;

/**
 * Created by Ferhat on 4.6.2018.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String Database_Inventory = "inventory";

    private static final int Database_Version = 1;

    public DbHelper(Context context) {
        super(context, Database_Inventory, null, Database_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String inventory_table = "CREATE TABLE " + Inventory.TABLE_NAME + " ("
                + Inventory._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Inventory.PRODUCT_NAME + " TEXT NOT NULL, "
                + Inventory.PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + Inventory.QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + Inventory.SUPPLIER_NAME + " TEXT, "
                + Inventory.PHONE_NUMBER + " INTEGER);";

        db.execSQL(inventory_table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
