package com.der.mymusicplayerapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;
import android.util.Log;

import com.der.mymusicplayerapp.model.Song;

import java.util.ArrayList;
import java.util.List;

public class FavoritesOperations {

    SQLiteOpenHelper dbHandler;
    SQLiteDatabase database;

    private static final String[] allColumns = {
            FavouritesDBHandler.COLUMN_ID,
            FavouritesDBHandler.COLUMN_TITLE,
            FavouritesDBHandler.COLUMN_SUBTITLE,
            FavouritesDBHandler.COLUMN_PATH
    };

    public FavoritesOperations(Context context){
        dbHandler = new FavouritesDBHandler(context);
    }

    public void addSongToFav(Song song){
        //open
        open();

        ContentValues values =new ContentValues();
        values.put(FavouritesDBHandler.COLUMN_TITLE, song.getTitle());
        values.put(FavouritesDBHandler.COLUMN_SUBTITLE, song.getTitle());
        values.put(FavouritesDBHandler.COLUMN_PATH, song.getPath());

        database.insertWithOnConflict(
                FavouritesDBHandler.SONG_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        //close
        close();
    }

    //TODO: find all favourite method
    public ArrayList<Song> getAllFavSong(){
        open();

        Cursor cursor = database.query(FavouritesDBHandler.SONG_TABLE, allColumns, null, null, null, null, null);
        ArrayList<Song> favSongs = new ArrayList<>();
        int title = cursor.getColumnIndex(FavouritesDBHandler.COLUMN_TITLE);
        int subTitle = cursor.getColumnIndex(FavouritesDBHandler.COLUMN_SUBTITLE);
        int path = cursor.getColumnIndex(FavouritesDBHandler.COLUMN_PATH);

        while (cursor.moveToNext()){
            Song song = new Song();
            song.setTitle(cursor.getString(title));
            song.setSubTitle(cursor.getString(subTitle));
            song.setPath(cursor.getString(path));
            favSongs.add(song);
        }

        close();
        return favSongs;
    }


    //TODO: remove favourite song by path method
    public void removeSong(String songPath) {
        open();
        String whereClause = FavouritesDBHandler.COLUMN_PATH + "=?";
        String[] whereArgs = new String[]{songPath};
        database.delete(FavouritesDBHandler.SONG_TABLE, whereClause, whereArgs);
        close();
    }


    public void open() {
        database = dbHandler.getWritableDatabase();
    }

    public void close() {
        dbHandler.close();
    }

}
