package me.ravindrabarthwal.inserve.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ravologi on 12/29/2016.
 */
public final class InServeContract {

    /** Make sure no one instantiate the class*/

    private InServeContract(){}

    /**
     * The content authority for the entire contact provider
     * It should be unique on entire device.
     */
    public final static String CONTENT_AUTHORITY = "me.ravindrabarthwal.inserve";

    /**
     * The Base content Uri which will be use to contact the
     * content provider
     */
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path of the products
     */
    public final static String PATH_PRODUCT = "product";

    public static final class ProductEntry implements BaseColumns {

        /**
         * Content Uri to access the me.ravindrabarthwal.inserve.data in the provider.
         */
        public final static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PRODUCT);

        /**
         * The MIME type for the list of items from the provider.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        /**
         * The MIME type for the single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        /**
         * Table Name
         */
        public final static String TABLE_NAME = "product";

        /*
         * Id of the Table
         */
        public final static String ID = BaseColumns._ID;

        /**
         * Product Name
         */
        public final static String NAME = "name";

        /**
         * Price of the Product
         */
        public final static String PRICE = "price";

        /**
         * Quantity of the Product
         */
        public final static String QUANTITY = "quantity";

        /**
         * Name of the Supplier
         */
        public final static String SUPPLIER = "supplier";



    }

}
