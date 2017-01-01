package me.ravindrabarthwal.inserve.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import me.ravindrabarthwal.inserve.data.InServeContract.ProductEntry;

/**
 * Created by ravologi on 12/29/2016.
 */
public class InServeDbHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "inserve.db";

    private final static int DATABASE_VERSION = 1;

    public InServeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String schema = "CREATE TABLE " + ProductEntry.TABLE_NAME + "("
                        + ProductEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + ProductEntry.NAME + " TEXT NOT NULL, "
                        + ProductEntry.SUPPLIER + " TEXT NOT NULL, "
                        + ProductEntry.IMAGE + " TEXT , "
                        + ProductEntry.PRICE + " INTEGER NOT NULL, "
                        + ProductEntry.QUANTITY + " INTEGER NOT NULL );";

        db.execSQL(schema);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
