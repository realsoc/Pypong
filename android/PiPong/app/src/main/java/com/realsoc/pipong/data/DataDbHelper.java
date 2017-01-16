package com.realsoc.pipong.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.realsoc.pipong.data.DataContract.GameEntry;
import com.realsoc.pipong.data.DataContract.PlayerEntry;

/**
 * Created by Hugo on 16/01/2017.
 */

public class DataDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "pipong.db";

    public DataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_PLAYER_TABLE =  "CREATE TABLE "+PlayerEntry.TABLE_NAME+" ("+
                PlayerEntry._ID +" INTEGER PRIMARY KEY, "+
                PlayerEntry.COLUMN_IS_ONLINE + " BOOLEAN, "+
                PlayerEntry.COLUMN_PLAYER_NAME + " TEXT UNIQUE NOT NULL "+
                ");";
        final String SQL_CREATE_GAME_TABLE = "CREATE TABLE "+ GameEntry.TABLE_NAME+" ("+
                GameEntry._ID +" INTEGER PRIMARY KEY, "+
                GameEntry.COLUMN_DATE +" TEXT, "+
                GameEntry.COLUMN_IS_ONLINE +" BOOLEAN, " +
                GameEntry.COLUMN_PLAYER1_KEY + " INTEGER NOT NULL, "+
                GameEntry.COLUMN_PLAYER2_KEY + " INTEGER NOT NULL, "+
                GameEntry.COLUMN_SCORE_PLAYER1 + " INTEGER NOT NULL, "+
                GameEntry.COLUMN_SCORE_PLAYER2 + " INTEGER NOT NULL, "+
                GameEntry.COLUMN_TYPE+ " INTEGER NOT NULL, "+
                " FOREIGN KEY ("+GameEntry.COLUMN_PLAYER1_KEY+") REFERENCES "+
                PlayerEntry.TABLE_NAME+" ("+PlayerEntry._ID+"),"+
                " FOREIGN KEY ("+GameEntry.COLUMN_PLAYER2_KEY+") REFERENCES "+
                PlayerEntry.TABLE_NAME+" ("+PlayerEntry._ID+"));";
        db.execSQL(SQL_CREATE_GAME_TABLE);
        db.execSQL(SQL_CREATE_PLAYER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+PlayerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+GameEntry.TABLE_NAME);
        onCreate(db);
    }
}
