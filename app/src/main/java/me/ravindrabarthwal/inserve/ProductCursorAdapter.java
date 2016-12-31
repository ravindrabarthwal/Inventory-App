package me.ravindrabarthwal.inserve;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.ravindrabarthwal.inserve.data.InServeContract;
import me.ravindrabarthwal.inserve.data.InServeContract.ProductEntry;

/**
 * Created by ravologi on 12/29/2016.
 */
public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c){
        super(context,c,0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_layout_main,viewGroup,false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.txt_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.txt_price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.txt_quantity);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.NAME));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.QUANTITY));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.PRICE));

        nameTextView.setText(name);
        priceTextView.setText(context.getString(R.string.price_tag) + " $"+ String.valueOf(price));
        quantityTextView.setText(context.getString(R.string.quantity_tag) + " " + String.valueOf(quantity));

        if(quantity < 10){
            quantityTextView.setTextColor(Color.parseColor("red"));
        }

        final long id = cursor.getInt(cursor.getColumnIndex(ProductEntry.ID));
        Button btn = (Button) view.findViewById(R.id.btn_sale);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(quantity > 0) {
                    int newQuantity = quantity - 1;
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.QUANTITY, newQuantity);
                    Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                    int rowsAffected = context.getContentResolver().update(currentProductUri, values, null, null);
                    if (rowsAffected == 0) {
                        Toast.makeText(context, "Error updating the quantity.", Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(context, "The stock is already zero.", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}
