package com.khang.pex;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class BookProvider extends ContentProvider {
    SQLiteDatabase db;

    public static final String AUTHORITY = "com.khang.pex.books.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BookDatabase.DB_TABLE);

    public static final int BOOK = 1;
    public static final int BOOK_ID = 2;

    static UriMatcher myUri = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        myUri.addURI(AUTHORITY, "Books", BOOK);
        myUri.addURI(AUTHORITY, "Books/#", BOOK_ID);
    }

    public BookProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (myUri.match(uri)) {
            case BOOK:
                count = db.delete(BookDatabase.DB_TABLE,
                        selection, selectionArgs);
                break;
            case BOOK_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(BookDatabase.DB_TABLE,
                        BookDatabase.COLUMN_ID + " = " + id
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + " ) " : " ) "),
                        selectionArgs);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (myUri.match(uri)) {
            case BOOK:
                return "vnd.android.cursor.dir/vnd.com.khang.pex.Books";
            case BOOK_ID:
                return "vnd.android.cursor.item/vnd.com.khang.pex.Books";
            default:
                throw new IllegalArgumentException("Unknow URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long row = db.insert(BookDatabase.DB_TABLE, null, values);

        if (row > 0) {
            uri = ContentUris.withAppendedId(CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return uri;
    }

    @Override
    public boolean onCreate() {
        BookDatabase helper = new BookDatabase(getContext());

        db = helper.getWritableDatabase();

        if (db != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder query = new SQLiteQueryBuilder();
        query.setTables(BookDatabase.DB_TABLE);
        if (myUri.match(uri) == BOOK_ID) {
            query.appendWhere(BookDatabase.COLUMN_ID + " = " + uri.getPathSegments().get(1));
        }
        if (sortOrder == null || sortOrder == "") {
            sortOrder = BookDatabase.COLUMN_TITLE;
        }
        Cursor c = query.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        switch (myUri.match(uri)) {
            case BOOK:
                count = db.update(BookDatabase.DB_TABLE, values, selection, selectionArgs);
                break;
            case BOOK_ID:
                String id = uri.getPathSegments().get(1);
                count = db.update(BookDatabase.DB_TABLE, values, BookDatabase.COLUMN_ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + " ) " : " ) "), selectionArgs);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
