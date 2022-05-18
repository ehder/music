package com.der.mymusicplayerapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class FavouritesDBHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public static final String SONG_TABLE           = "favorites";
    public static final String COLUMN_ID             = "songID";
    public static final String COLUMN_TITLE          = "title";
    public static final String COLUMN_SUBTITLE       = "subtitle";
    public static final String COLUMN_PATH           = "songpath";

    private static final String CREATE_TABLE ="CREATE TABLE " + SONG_TABLE + " ("
            + COLUMN_ID + " INTEGER, "
            + COLUMN_TITLE + " TEXT, "
            + COLUMN_SUBTITLE + " TEXT, "
            + COLUMN_PATH + " TEXT PRIMARY KEY " + ")";

    public FavouritesDBHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*  Database - Insertion
            we can create table or insert data into table using execSQL method
            defined in SQLiteDatabase class. Its syntax is given below
    */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + SONG_TABLE);
        db.execSQL(CREATE_TABLE);
    }
}
