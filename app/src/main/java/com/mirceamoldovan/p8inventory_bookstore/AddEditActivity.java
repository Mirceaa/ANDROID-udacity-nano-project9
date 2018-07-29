package com.mirceamoldovan.p8inventory_bookstore;


import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mirceamoldovan.p8inventory_bookstore.data.Contract;

import java.util.Locale;

import static com.mirceamoldovan.p8inventory_bookstore.data.Contract.BookEntry.COLUMN_NAME;
import static com.mirceamoldovan.p8inventory_bookstore.data.Contract.BookEntry.COLUMN_PRICE;
import static com.mirceamoldovan.p8inventory_bookstore.data.Contract.BookEntry.COLUMN_QUANTITY;
import static com.mirceamoldovan.p8inventory_bookstore.data.Contract.BookEntry.COLUMN_PUBLISHER_NAME;
import static com.mirceamoldovan.p8inventory_bookstore.data.Contract.BookEntry.COLUMN_PUBLISHER_PHONE;


public class AddEditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for book data loader
    private static final int EXISTING_BOOK_LOADER = 0;

    //Content URI for the existing book (null if it's a new book)
    private Uri mCurrentBookUri;

    // EditText field to enter the book name
    private EditText mName;

    //EditText field to enter the book price
    private EditText mPrice;

    // EditText field to enter the quantity of books
    private EditText mQuantity;

    // EditText field t oenter the publisher name
    private EditText mPublisherName;

    // EditTExt field to enter publisher phone number
    private EditText mPublisherPhone;

    // Boolean flag that keeps track of whether the book has been edited (true) or not (false)
    private boolean mBookHasChanged = false;

    // OnTouchListener that listens for any user touches on a View, implying that are modifying
    // the view, and we change the mBookHasChanged boolean to true.
    //
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book ore editing an existing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // If the intent DOES NOT contain the book content URI -> create the new book;
        if (mCurrentBookUri == null) {
            // This is the new book, so change the app bar to say "Add a new book"
            setTitle("Add a new book!");

            // Invalidate the option menu, so the "Delete" menu option can be hidden.
            // No sense to delete a book not having been created yet.
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing book, so change app to say "Edit book"
            setTitle("Edit book");

            // Initialize a loader to read the data from DB
            // and display the current value in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        mName = findViewById(R.id.book_name);
        mPrice = findViewById(R.id.book_price);
        mQuantity = findViewById(R.id.book_quantity);
        mPublisherName = findViewById(R.id.publisher_name);
        mPublisherPhone = findViewById(R.id.publisher_phone);

        Button mIncrease = findViewById(R.id.increase_button);
        Button mDecrease = findViewById(R.id.decrease_button);
        Button mOrder = findViewById(R.id.order_button);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mName.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mQuantity.setOnTouchListener(mTouchListener);
        mPublisherName.setOnTouchListener(mTouchListener);
        mPublisherPhone.setOnTouchListener(mTouchListener);
        mIncrease.setOnTouchListener(mTouchListener);
        mDecrease.setOnTouchListener(mTouchListener);

        mIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = mQuantity.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    mQuantity.setText("1");
                } else {
                    int not_null_quantity = Integer.parseInt(mQuantity.getText().toString().trim());
                    not_null_quantity++;
                    mQuantity.setText(String.valueOf(not_null_quantity));
                }
            }
        });

        mDecrease.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String quantity = mQuantity.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    mQuantity.setText("0");
                } else {
                    int new_quantity = Integer.parseInt(mQuantity.getText().toString().trim());
                    if (new_quantity > 0) {
                        new_quantity--;
                        mQuantity.setText(String.valueOf(new_quantity));
                    } else {
                        Toast.makeText(AddEditActivity.this, "The quantity cannot be negative!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mPublisherPhone.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    // Get user input from EditText views and save book details into DB.
    private void saveBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mName.getText().toString().trim();

        String priceString = mPrice.getText().toString().trim();

        String quantityString = mQuantity.getText().toString().trim();

        String publisherNameString = mPublisherName.getText().toString().trim();

        String publisherPhoneString = mPublisherPhone.getText().toString().trim();

        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(publisherNameString) &&
                TextUtils.isEmpty(publisherPhoneString)) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(this, R.string.nothing_was_changed, Toast.LENGTH_SHORT).show();
            // Exit activity
            finish();
            return;
        }

        if (TextUtils.isEmpty(nameString)) {
            mName.requestFocus();
            mName.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.enter_the_book_name), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(priceString)) {
            mPrice.requestFocus();
            mPrice.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.enter_the_price_for_book), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(quantityString)) {
            mQuantity.requestFocus();
            mQuantity.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.enter_the_quantity_for_book), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(publisherNameString)) {
            mPublisherName.requestFocus();
            mPublisherName.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.enter_the_publisherHouse_name), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(publisherPhoneString)) {
            mPublisherPhone.requestFocus();
            mPublisherPhone.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.enter_the_publisherHouse_phone_number), Toast.LENGTH_LONG).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and books attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, nameString);
        float priceFloat = Float.parseFloat(priceString);
        values.put(COLUMN_PRICE, priceFloat);
        values.put(COLUMN_QUANTITY, quantityString);
        values.put(COLUMN_PUBLISHER_NAME, publisherNameString);
        values.put(COLUMN_PUBLISHER_PHONE, publisherPhoneString);

        // Determine if this is a new or existing book by checking if mCurrentBookUri is null or not
        if (mCurrentBookUri == null) {

            // This is a new book, so insert a new book into the provider,
            // returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(Contract.BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.save_error,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.added_msg,
                        Toast.LENGTH_SHORT).show();
                // Exit activity
                finish();
            }
        } else {
            // Otherwise this is an existing book, so update the book with content URI: mCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, R.string.update_error,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, R.string.updated_msg,
                        Toast.LENGTH_SHORT).show();
            }
            // Exit activity
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/add_edit_menu.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.add_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save book to DB
                saveBook();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddEditActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(AddEditActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // This method is called when the back button is pressed.

    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all the book attributes, define a projection that contains
        // all columns from the book
        String[] projection = {
                Contract.BookEntry._ID,
                Contract.BookEntry.COLUMN_NAME,
                Contract.BookEntry.COLUMN_PRICE,
                Contract.BookEntry.COLUMN_QUANTITY,
                Contract.BookEntry.COLUMN_PUBLISHER_NAME,
                Contract.BookEntry.COLUMN_PUBLISHER_NAME};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current book
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of the book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(Contract.BookEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(Contract.BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(Contract.BookEntry.COLUMN_QUANTITY);
            int publisher_nameColumnIndex = cursor.getColumnIndex(Contract.BookEntry.COLUMN_PUBLISHER_NAME);
            int publisher_phoneColumnIndex = cursor.getColumnIndex(Contract.BookEntry.COLUMN_PUBLISHER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String publisher_name = cursor.getString(publisher_nameColumnIndex);
            String phone = cursor.getString(publisher_phoneColumnIndex);

            // Update the views on the screen with the values from DB
            mName.setText(name);
            mPrice.setText(String.format(Float.toString(price), Locale.getDefault()));
            mQuantity.setText(String.format(Integer.toString(quantity), Locale.getDefault()));
            mPublisherName.setText(publisher_name);
            mPublisherPhone.setText(phone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mName.setText(R.string.blank);
        mPrice.setText(R.string.blank);
        mQuantity.setText(R.string.blank);
        mPublisherName.setText(R.string.blank);
        mPublisherPhone.setText(R.string.blank);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_your_changes_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Delete the book from the DB.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, R.string.deleting_error_msg,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, R.string.device_deleted,
                        Toast.LENGTH_SHORT).show();
            }
            // Close the activity
            finish();
        }
    }
}
