package me.ravindrabarthwal.inserve;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import me.ravindrabarthwal.inserve.data.InServeContract;

public class ModifyQuantity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri currentProductUri;

    int EXISTING_PRODUCT_LOADER = 1;

    private EditText quantityEditText;
    private TextView nameTxtView;
    private int currentProductId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_quantity);

        Intent intent = getIntent();
        currentProductUri = intent.getData();

        Button modifyBtn = (Button) findViewById(R.id.modify_btn);

        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    int quantity = Integer.parseInt(quantityEditText.getText().toString().trim());

                    ContentValues values = new ContentValues();
                    values.put(InServeContract.ProductEntry.QUANTITY,quantity);

                    Uri contentUri = ContentUris.withAppendedId(InServeContract.ProductEntry.CONTENT_URI,currentProductId);
                    int rowAffected = getContentResolver().update(contentUri,values,null,null);
                    if(rowAffected > 0) {
                        Toast.makeText(getBaseContext(),"Quantity Modified Successfully.",Toast.LENGTH_LONG).show();
                        finish();;
                    }else {
                        Toast.makeText(getBaseContext(),"Error  Modifying Quantity.",Toast.LENGTH_LONG).show();
                    }
                }catch(NumberFormatException e) {
                    Toast.makeText(getBaseContext(),"Error in parsing quantity.",Toast.LENGTH_LONG).show();
                }
            }
        });

        getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getBaseContext(),currentProductUri,null,null,null,null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if(cursor.moveToNext()){
            quantityEditText = (EditText) findViewById(R.id.edit_quantity);
            nameTxtView = (TextView) findViewById(R.id.text_name);


            quantityEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(InServeContract.ProductEntry.QUANTITY)));
            nameTxtView.setText(cursor.getString(cursor.getColumnIndexOrThrow(InServeContract.ProductEntry.NAME)));
            currentProductId = cursor.getInt(cursor.getColumnIndexOrThrow(InServeContract.ProductEntry.ID));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        quantityEditText.setText("");
        nameTxtView.setText("");
    }
}
