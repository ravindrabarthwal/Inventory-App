package me.ravindrabarthwal.inserve;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import me.ravindrabarthwal.inserve.data.InServeContract.ProductEntry;

public class ProductDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private TextView nameTextView;
    private TextView supplierTextView;
    private TextView priceTextView;
    private TextView quantityTextView;

    private String name;
    private String supplier;
    private String quantity;
    private String price;
    private String stringImageUri;
    ImageView imageView;
    private int id;

    private Uri currentProductUri;

    int EXISTING_PRODUCT_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        currentProductUri = intent.getData();
        getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch(id){
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                break;
            case R.id.action_modify_quantity:
                modifyQuantity();
                break;
            case R.id.action_order:
                orderProduct();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct(){
        Uri contentUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI,id);
        long isDeleted = getContentResolver().delete(contentUri,null,null);
        if(isDeleted != -1) {
            Toast.makeText(getBaseContext(),"Product Deleted Successfully",Toast.LENGTH_LONG).show();
        }
    }

    private void modifyQuantity(){
        Uri contentUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI,id);
        Intent intent = new Intent(ProductDetail.this, ModifyQuantity.class);
        intent.setData(contentUri);
        startActivity(intent);
    }

    private void orderProduct() {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");

        String subject = "Need More " + name.toUpperCase();
        String message = "Hello " + supplier.toUpperCase() + ",\n\nWe are in need of " + name.toUpperCase() + ".\nPlease send asap.\n\nThankyou";
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getBaseContext(),currentProductUri,null,null,null,null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if(cursor.moveToNext()){
            nameTextView = (TextView) findViewById(R.id.name);
            supplierTextView = (TextView) findViewById(R.id.supplier);
            priceTextView = (TextView) findViewById(R.id.price);
            quantityTextView = (TextView) findViewById(R.id.quantity);
            imageView = (ImageView) findViewById(R.id.image_product_detail);

            name = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.NAME));
            supplier = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.SUPPLIER));
            price = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.PRICE));
            quantity = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.QUANTITY));
            stringImageUri = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.IMAGE));
            id = cursor.getInt(cursor.getColumnIndex(ProductEntry.ID));


            nameTextView.setText(name);
            supplierTextView.setText(supplier);
            priceTextView.setText("$"+price);
            quantityTextView.setText(quantity);

            try{
             imageView.setImageBitmap(getBitmapFromUri(Uri.parse(stringImageUri)));
            }catch(Exception e) {
                throw new IllegalArgumentException("Unable to parse image uri." + e);
            }

            Button btn = (Button) findViewById(R.id.btn_sale);
            final int quantityInt = Integer.parseInt(quantity);
            if(quantityInt < 10){
                quantityTextView.setTextColor(Color.parseColor("red"));
            }
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(quantityInt > 0) {
                        int newQuantity = quantityInt - 1;
                        ContentValues values = new ContentValues();
                        values.put(ProductEntry.QUANTITY, newQuantity);
                        Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                        int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);
                        if (rowsAffected == 0) {
                            Toast.makeText(getBaseContext(), "Error updating the quantity.", Toast.LENGTH_LONG).show();
                        }


                    }else {
                        Toast.makeText(getBaseContext(), "The stock is already zero.", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameTextView.setText("");
        supplierTextView.setText("");
        priceTextView.setText("");
        quantityTextView.setText("");
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e("Bitmap", "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e("Bitmap", "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }
}
