package com.example.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventoryapp.database.Contract.Inventory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    InventoryAdapter items;

    @BindView(R.id.listItems) ListView listView;

    @BindView(R.id.noItem) TextView noItem;

    private static final int Product_Loader = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        ButterKnife.bind(this);

        items = new InventoryAdapter(this, null);

        listView.setAdapter(items);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(InventoryActivity.this, InventoryEditor.class);

                Uri productUri = ContentUris.withAppendedId(Inventory.CONTENT_URI, id);

                intent.setData(productUri);

                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(Product_Loader, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.insert:
                Intent intent = new Intent(InventoryActivity.this, InventoryEditor.class);
                startActivity(intent);
                return true;

            case R.id.deleteAll:
                deleteAllProducts();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] elements = {
                Inventory._ID,
                Inventory.PRODUCT_NAME,
                Inventory.PRICE,
                Inventory.QUANTITY,
                Inventory.SUPPLIER_NAME,
                Inventory.PHONE_NUMBER};

        return new CursorLoader(this,
                Inventory.CONTENT_URI,
                elements,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data.getCount() == 0) {

            noItem.setVisibility(View.VISIBLE);

        } else {

            noItem.setVisibility(View.GONE);

        }

        items.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        items.swapCursor(null);
    }

    private void deleteAllProducts() {

        getContentResolver().delete(Inventory.CONTENT_URI, null, null);

        getContentResolver().notifyChange(Inventory.CONTENT_URI,null);

        Toast.makeText(this, R.string.allDeleted, Toast.LENGTH_SHORT).show();
    }
}
