package me.ravindrabarthwal.inserve.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import me.ravindrabarthwal.inserve.data.InServeContract.ProductEntry;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by ravologi on 12/29/2016.
 */
public class InServeProvider extends ContentProvider {

    //Integer code for the Products
    private final static int PRODUCT = 100;

    //Integer code for the single Product Item
    private final static int PRODUCT_ITEM = 101;

    //UriMatcher object which will hold matching Uri
    private final static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        /**
         * The Matching Uri for
         * content://me.ravindrabarthwal.inserve/product
         */
        sUriMatcher.addURI(InServeContract.CONTENT_AUTHORITY,InServeContract.PATH_PRODUCT,PRODUCT);

        /**
         * The Matching Uri for
         * content://me.ravindrabarthwal.inserve/product/#
         * # represent an integer can be accepted and it will be id.
         */
        sUriMatcher.addURI(InServeContract.CONTENT_AUTHORITY,InServeContract.PATH_PRODUCT + "/#", PRODUCT_ITEM);
    }

    private InServeDbHelper mDBHelper;

    @Override
    public boolean onCreate() {
        mDBHelper = new InServeDbHelper(getContext());
        return true;

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projections, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch(match){
            case PRODUCT:
                cursor = db.query(ProductEntry.TABLE_NAME,projections,selection,selectionArgs,null,null,sortOrder);
                break;

            case PRODUCT_ITEM:

                selection = ProductEntry.ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };


                cursor = db.query(ProductEntry.TABLE_NAME,projections,selection,selectionArgs,null,null,sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Can not query Unknown URI "  + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ITEM:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    private Uri insertProduct(Uri uri, ContentValues values) {

        String name = values.getAsString(ProductEntry.NAME);
        if(name == null) {
            throw new IllegalArgumentException("Product Name is required.");
        }

        Integer price = values.getAsInteger(ProductEntry.PRICE);
        if(price == null || price <0){
            throw new IllegalArgumentException("Price is required and should be greater than 0.");
        }

        Integer quantity = values.getAsInteger(ProductEntry.QUANTITY);
        if(quantity == null || quantity <0){
            throw new IllegalArgumentException("Quantity is required and should be greater than 0.");
        }

        String supplier = values.getAsString(ProductEntry.NAME);
        if(supplier == null) {
            throw new IllegalArgumentException("Supplier Name is required.");
        }

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        long id = db.insert(ProductEntry.TABLE_NAME,null,values);

        if(id == -1){
            Log.v("INSERT ", "Unable to insert the product");
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                // Delete all rows that match the selection and selection args
                rowsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ITEM:
                // Delete a single row given by the ID in the URI
                selection = ProductEntry.ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ITEM:
                selection = ProductEntry.ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ProductEntry.NAME)) {
            String name = values.getAsString(ProductEntry.NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(ProductEntry.SUPPLIER)) {
            String supplier = values.getAsString(ProductEntry.SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException("Supplier name required.");
            }
        }

        if (values.containsKey(ProductEntry.PRICE)) {
            Integer price = values.getAsInteger(ProductEntry.PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Price is required and should be greater than 0.");
            }
        }

        if (values.containsKey(ProductEntry.QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductEntry.QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Quantity is required and should be greater than 0.");
            }
        }


        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = db.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
}
