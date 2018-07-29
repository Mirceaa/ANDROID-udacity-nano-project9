package com.mirceamoldovan.p8inventory_bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mirceamoldovan.p8inventory_bookstore.data.Contract;

import static com.mirceamoldovan.p8inventory_bookstore.data.Contract.BookEntry.COLUMN_NAME;
import static com.mirceamoldovan.p8inventory_bookstore.data.Contract.BookEntry.COLUMN_PRICE;

/**
 * * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of it inventory data as its data source. This adapter knows
 * how to create list items for each row of it inventory data in the {@link Cursor}.
 */

public class BookCursorAdapter extends CursorAdapter {
    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book inventory data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.book_name);
        TextView priceTextView = view.findViewById(R.id.book_price);
        TextView quantityTextView = view.findViewById(R.id.book_quantity);

        Button sellButton = view.findViewById(R.id.sell);

        // Find the columns of book attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(Contract.BookEntry.COLUMN_QUANTITY);

        // Read the book attributes from the Cursor for the current book
        final String BookName = cursor.getString(nameColumnIndex);
        final String BookPrice = cursor.getString(priceColumnIndex);
        String BookQuantity = cursor.getString(quantityColumnIndex);

        // Update the TextViews with the attributes for the current book
        nameTextView.setText(BookName);
        priceTextView.setText(BookPrice);
        quantityTextView.setText(BookQuantity);

        final int idColumnIndex = cursor.getInt(cursor.getColumnIndex(Contract.BookEntry._ID));
        final int currentQuantityColumnIndex = cursor.getColumnIndex(Contract.BookEntry.COLUMN_QUANTITY);
        final int currentQuantity = Integer.valueOf(cursor.getString(currentQuantityColumnIndex));

        //Sell button which decrease quantity in storage
        sellButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (currentQuantity > 0) {
                    int newCurrentQuantity = currentQuantity - 1;
                    Uri quantityUri = ContentUris.withAppendedId(Contract.BookEntry.CONTENT_URI, idColumnIndex);

                    ContentValues values = new ContentValues();
                    values.put(Contract.BookEntry.COLUMN_QUANTITY, newCurrentQuantity);
                    context.getContentResolver().update(quantityUri, values, null, null);

                    Toast.makeText(context, "The sale was successfully! \nThe new quantity for "+ BookName + " is: " + newCurrentQuantity, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "You can't sale, because " + BookName + " is out of stock!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
