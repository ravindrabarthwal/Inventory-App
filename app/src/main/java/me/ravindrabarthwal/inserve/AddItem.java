package me.ravindrabarthwal.inserve;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import me.ravindrabarthwal.inserve.data.InServeContract;
import me.ravindrabarthwal.inserve.data.InServeContract.ProductEntry;
import me.ravindrabarthwal.inserve.data.InServeDbHelper;

public class AddItem extends AppCompatActivity{

    private ImageView image;
    private int IMAGE_PICK = 1;
    Uri imgUri;
    String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        Button AddProductBtn = (Button) findViewById(R.id.btn_add_product);
        AddProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addProduct();

            }
        });

        image = (ImageView) findViewById(R.id.image_add);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;

                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }

                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICK);

            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode,Intent intent) {
        if(resultCode == RESULT_OK && requestCode == IMAGE_PICK) {
            imgUri = intent.getData();
            image.setImageURI(imgUri);
        }
    }

    private void addProduct(){
        EditText nameEditText = (EditText) findViewById(R.id.edit_txt_name);
        EditText supplierEditText = (EditText) findViewById(R.id.edit_txt_supplier);
        EditText priceEditText = (EditText) findViewById(R.id.edit_txt_price);
        EditText quantityEditText = (EditText) findViewById(R.id.edit_txt_quantity);

        String name = nameEditText.getText().toString().trim();
        String supplier = supplierEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();
        String quantity = quantityEditText.getText().toString().trim();
        String imageUri = imgUri.toString();



        if(TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(supplier) ||
                TextUtils.isEmpty(price) ||
                TextUtils.isEmpty(quantity) ) {
            Toast.makeText(this, "Please fill the form completely.", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                int priceInt = Integer.parseInt(price);
                int quantityInt = Integer.parseInt(quantity);
                ContentValues values = new ContentValues();
                values.put(ProductEntry.NAME,name);
                values.put(ProductEntry.PRICE,priceInt);
                values.put(ProductEntry.QUANTITY,quantityInt);
                values.put(ProductEntry.SUPPLIER,supplier);
                values.put(ProductEntry.IMAGE,imageUri);

                Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI,values);

                // Show a toast message depending on whether or not the insertion was successful.
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, "Error entering the product.", Toast.LENGTH_SHORT).show();

                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, "Product entered successfully.", Toast.LENGTH_SHORT).show();
                    finish();

                }
            }catch (NumberFormatException e) {
                Toast.makeText(this, "Make sure you enter integer on price and quantity.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
