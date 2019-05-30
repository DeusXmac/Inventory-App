package com.example.inventoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventoryapp.database.Contract.Inventory;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.inventoryapp.database.Contract.Inventory.PHONE_NUMBER;

/**
 * Created by Ferhat on 23.6.2018.
 */

public class InventoryEditor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int Product_Loader = 0;

    @BindView(R.id.productName)
    EditText productNameEditor;

    @BindView(R.id.price)
    EditText priceEditor;

    @BindView(R.id.decrease)
    Button quantityDecrease;

    @BindView(R.id.increase)
    Button quantityIncrease;

    @BindView(R.id.quantity)
    TextView textQuantity;

    @BindView(R.id.supplierName)
    EditText supplierNameEditor;

    @BindView(R.id.supplierNumber)
    EditText supplierNumberEditor;

    private Uri productUri;

    private boolean changedData = false;

    private int quantity;

    private MenuItem order;

    private String mSupplierNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        productUri = intent.getData();

        productNameEditor.setOnTouchListener(touch);
        priceEditor.setOnTouchListener(touch);

        quantityDecrease.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (quantity != 0) {

                    quantity = quantity - 1;

                    textQuantity.setText("" + quantity);

                }

                if (quantity < 1) {

                    quantityDecrease.setEnabled(false);

                }

            }
        });

        quantityIncrease.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                quantity = quantity + 1;

                textQuantity.setText("" + quantity);

                if (quantity != 0) {

                    quantityDecrease.setEnabled(true);

                }

            }

        });

        supplierNameEditor.setOnTouchListener(touch);
        supplierNumberEditor.setOnTouchListener(touch);

        if (productUri == null) {

            setTitle(getString(R.string.addProduct));

            invalidateOptionsMenu();

            textQuantity.setText("0");

            if (quantity == 0) {

                quantityDecrease.setEnabled(false);

            }

        } else {

            setTitle(getString(R.string.editProduct));

            getLoaderManager().initLoader(Product_Loader, null, this);

        }

    }

    private void unsavedChanged(

            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsavedChanges);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keepEditing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private View.OnTouchListener touch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            changedData = true;
            return false;
        }
    };

    private void saveProduct() {

        ContentValues values = new ContentValues();

        String getProductName = productNameEditor.getText().toString().trim();
        String getPrice = priceEditor.getText().toString().trim();
        String getSupplierName = supplierNameEditor.getText().toString().trim();
        String getSupplierNumber = supplierNumberEditor.getText().toString().trim();

        int price = 0;
        if (!TextUtils.isEmpty(getPrice)) {
            price = Integer.parseInt(getPrice);
        }

        if (!TextUtils.isEmpty(getProductName) && !TextUtils.isEmpty(getSupplierName)) {

            values.put(Inventory.PRODUCT_NAME, getProductName);
            values.put(Inventory.PRICE, price);
            values.put(Inventory.QUANTITY, quantity);
            values.put(Inventory.SUPPLIER_NAME, getSupplierName);
            values.put(Inventory.PHONE_NUMBER, getSupplierNumber);

            if (productUri == null) {

                Uri newUri = getContentResolver().insert(Inventory.CONTENT_URI, values);

                if (newUri == null) {

                    Toast.makeText(this, R.string.insertingFailed,
                            Toast.LENGTH_SHORT).show();
                } else {

                    finish();

                    Toast.makeText(this, R.string.insertingSuccessful,
                            Toast.LENGTH_SHORT).show();
                }

            } else {

                int edit = getContentResolver().update(productUri, values, null, null);

                if (quantity == 0) {

                    quantityDecrease.setEnabled(false);

                }

                if (edit == 0) {

                    Toast.makeText(this, R.string.editingFailed,
                            Toast.LENGTH_SHORT).show();
                } else {

                    finish();

                    Toast.makeText(this, R.string.editingSuccessful,
                            Toast.LENGTH_SHORT).show();
                }

            }

        } else {

            Toast.makeText(this, R.string.fillFields,
                    Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_editor, menu);

        order = menu.findItem(R.id.call);

        if (productUri == null) {

            order.setVisible(false);

        } else {

            order.setVisible(true);

        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.call:

                call(mSupplierNumber);

                return true;

            case R.id.save:

                saveProduct();

                return true;

            case R.id.delete:

                delete();

                return true;

            case android.R.id.home:

                if (!changedData) {
                    NavUtils.navigateUpFromSameTask(InventoryEditor.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(InventoryEditor.this);
                            }
                        };

                unsavedChanged(discardButtonClickListener);
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
                PHONE_NUMBER};

        return new CursorLoader(this,
                productUri,
                elements,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {

            return;

        }

        if (cursor.moveToFirst()) {

            int getProductName = cursor.getColumnIndex(Inventory.PRODUCT_NAME);
            int getPrice = cursor.getColumnIndex(Inventory.PRICE);
            int getQuantity = cursor.getColumnIndex(Inventory.QUANTITY);
            int getSupplierName = cursor.getColumnIndex(Inventory.SUPPLIER_NAME);
            int getSupplierPhoneNumber = cursor.getColumnIndex(PHONE_NUMBER);

            String mProductName = cursor.getString(getProductName);
            int mPrice = cursor.getInt(getPrice);
            quantity = cursor.getInt(getQuantity);
            String mSupplierName = cursor.getString(getSupplierName);
            mSupplierNumber = cursor.getString(getSupplierPhoneNumber);

            productNameEditor.setText(mProductName);
            priceEditor.setText("" + mPrice);
            textQuantity.setText("" + quantity);
            supplierNameEditor.setText(mSupplierName);
            supplierNumberEditor.setText("" + mSupplierNumber);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        productNameEditor.setText("");
        priceEditor.setText("");
        textQuantity.setText("");
        supplierNameEditor.setText("");
        supplierNumberEditor.setText("");

    }

    @Override
    public void onBackPressed() {
        if (!changedData) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        unsavedChanged(discardButtonClickListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (productUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void deleteProduct() {

        if (productUri != null) {
            int deleted = getContentResolver().delete(productUri, null, null);

            if (deleted == 0) {
                Toast.makeText(this, R.string.deletingFailed,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.deletingSuccessful,
                        Toast.LENGTH_SHORT).show();
                getContentResolver().notifyChange(productUri, null);
            }

            finish();

        }

    }

    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.wantToDelete);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void call(String number) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 101);

        } else {

            Intent intent = new Intent(Intent.ACTION_CALL);

            intent.setData(Uri.parse(getString(R.string.tel) + number));

            startActivity(intent);

        }

    }
}
