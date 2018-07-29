package com.mirceamoldovan.p8inventory_bookstore.data;

import android.provider.BaseColumns;
import android.content.ContentResolver;
import android.net.Uri;

public final class Contract {
    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.mirceamoldovan.p8inventory_bookstore";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOK = "project9 Inventory_bookstore";


    private Contract(){}

    public static final class BookEntry implements BaseColumns {

        // The content URI to access the book inventory data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOK);

        // The MIME type of the {@link #CONTENT_URI} for a list of books.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOK;

        // The MIME type of the {@link #CONTENT_URI} for a single book.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOK;

        // Name of the DB for books
        public static final String TABLE_NAME = "Book";

        // Unique ID number for book (only for use in the database table). Type: INTEGER
        public final static String COLUMN_ID = "_id";

        // Name of the book. Type: TEXT
        public static final String COLUMN_NAME = "name";

        // Price of the book. Type: REAL
        public static final String COLUMN_PRICE = "price";

        // Quantity of the book. Type: INTEGER
        public static final String COLUMN_QUANTITY = "quantity";

        // Name of the book publisher name. Type: TEXT
        public static final String COLUMN_PUBLISHER_NAME = "publisher_name";

        // Phone of the publisher. Type: TEXT
        public static final String COLUMN_PUBLISHER_PHONE = "publisher_phone";
    }
}
