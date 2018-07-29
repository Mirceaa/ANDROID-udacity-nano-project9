package com.mirceamoldovan.p8inventory_bookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    // Name of DB file
    public static final String DATABASE_NAME = "books.db";

    // The version of the database. If we change the database schema, we must increment the database version
    public static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // This is called when DB is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the book table:
        // CREATE TABLE book (_id INTEGER PRIMARY KEY AUTOINCREMENT, product_name TEXT, price INTEGER quantity INTEGER, publisher_name TEXT, publisher_phone_number TEXT );

        String SQL_CREATE_BOOK_TABLE = "CREATE TABLE " + Contract.BookEntry.TABLE_NAME + " (" +
                Contract.BookEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.BookEntry.COLUMN_NAME + " TEXT NOT NULL," +
                Contract.BookEntry.COLUMN_PRICE + " REAL NOT NULL," +
                Contract.BookEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                Contract.BookEntry.COLUMN_PUBLISHER_NAME + " TEXT NOT NULL," +
                Contract.BookEntry.COLUMN_PUBLISHER_PHONE + " TEXT NOT NULL );";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOK_TABLE);
    }

    //This is called when the database needs to be upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

}
