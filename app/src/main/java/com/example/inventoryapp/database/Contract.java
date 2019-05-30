package com.example.inventoryapp.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ferhat on 4.6.2018.
 */

public final class Contract {

        private Contract() {}

        public static final String CONTENT_AUTHORITY = "com.example.inventoryapp";

        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        public static final String PATH_INVENTORY = "inventory";

        public static final class Inventory implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        public static final String CONTENT_LIST_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public final static String TABLE_NAME = "inventory";

        public final static String _ID = BaseColumns._ID;

        public final static String PRODUCT_NAME = "productName";

        public final static String PRICE = "price";

        public final static String QUANTITY = "quantity";

        public final static String SUPPLIER_NAME = "supplierName";

        public final static String PHONE_NUMBER = "phoneNumber";

    }

}


