package com.example.inventoryapp.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.inventoryapp.database.Contract.Inventory;

public class Provider extends ContentProvider {

    public static final String LOG_TAG = Provider.class.getSimpleName();

    private static final int product = 100;

    private static final int id_product = 101;

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        matcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_INVENTORY, product);

        matcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_INVENTORY + "/#", id_product);
    }

    private DbHelper helper;

    @Override
    public boolean onCreate() {
        helper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor;

        int match = matcher.match(uri);
        switch (match) {
            case product:

                cursor = database.query(Inventory.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                break;
            case id_product:

                selection = Inventory._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(Inventory.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = matcher.match(uri);
        switch (match) {
            case product:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {

        SQLiteDatabase database = helper.getWritableDatabase();

        long id = database.insert(Inventory.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = matcher.match(uri);
        switch (match) {
            case product:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case id_product:
                selection = Inventory._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = helper.getWritableDatabase();

        int updated = database.update(Inventory.TABLE_NAME, values, selection, selectionArgs);

        if(updated != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return updated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = helper.getWritableDatabase();

        final int match = matcher.match(uri);
        switch (match) {
            case product:

                return database.delete(Inventory.TABLE_NAME, selection, selectionArgs);
            case id_product:

                selection = Inventory._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(Inventory.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = matcher.match(uri);
        switch (match) {
            case product:
                return Inventory.CONTENT_LIST_TYPE;
            case id_product:
                return Inventory.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}