package com.realsoc.pipong.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.realsoc.pipong.data.DataContract.GameEntry;
import com.realsoc.pipong.data.DataContract.PlayerEntry;
import com.realsoc.pipong.data.DataContract.CountEntry;

/**
 * Created by Hugo on 16/01/2017.
 */

public class DataDbHelper extends SQLiteOpenHelper {
    public final static String SQL_CREATE_PLAYER_TABLE =  "CREATE TABLE "+PlayerEntry.TABLE_NAME+" ("+
            PlayerEntry.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            PlayerEntry.COLUMN_IS_ONLINE + " INTEGER, "+
            PlayerEntry.COLUMN_PLAYER_NAME + " TEXT NOT NULL, "+
            " UNIQUE  ("+PlayerEntry.COLUMN_PLAYER_NAME+") ON CONFLICT REPLACE);";
    public final static String SQL_CREATE_GAME_TABLE = "CREATE TABLE "+ GameEntry.TABLE_NAME+" ("+
            GameEntry.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            GameEntry.COLUMN_DATE +" TEXT, "+
            GameEntry.COLUMN_IS_ONLINE +" INTEGER, " +
            GameEntry.COLUMN_PLAYER1_NAME + " TEXT NOT NULL, "+
            GameEntry.COLUMN_PLAYER2_NAME + " TEXT NOT NULL, "+
            GameEntry.COLUMN_PLAYER1_SCORE + " INTEGER NOT NULL, "+
            GameEntry.COLUMN_PLAYER2_SCORE + " INTEGER NOT NULL, "+
            GameEntry.COLUMN_TYPE+ " INTEGER NOT NULL);";
    public final static String SQL_CREATE_COUNT_TABLE = "CREATE TABLE "+ CountEntry.TABLE_NAME+" ("+
            CountEntry.COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            CountEntry.COLUMN_PLAYER_NAME +" TEXT NOT NULL, "+
            CountEntry.COLUMN_6GL +" INTEGER NOT NULL, " +
            CountEntry.COLUMN_6GP +" INTEGER NOT NULL, " +
            CountEntry.COLUMN_6GW +" INTEGER NOT NULL, " +
            CountEntry.COLUMN_6PS +" INTEGER NOT NULL, " +
            CountEntry.COLUMN_6PL +" INTEGER NOT NULL, " +
            CountEntry.COLUMN_11GL +" INTEGER NOT NULL, " +
            CountEntry.COLUMN_11GP +" INTEGER NOT NULL, " +
            CountEntry.COLUMN_11GW +" INTEGER NOT NULL, " +
            CountEntry.COLUMN_11PS +" INTEGER NOT NULL, " +
            CountEntry.COLUMN_11PL +" INTEGER NOT NULL, " +
            CountEntry.COLUMN_21GL +" INTEGER NOT NULL, " +
            CountEntry.COLUMN_21GP +" INTEGER NOT NULL, " +
            CountEntry.COLUMN_21GW +" INTEGER NOT NULL, " +
            CountEntry.COLUMN_21PS +" INTEGER NOT NULL, " +
            CountEntry.COLUMN_21PL +" INTEGER NOT NULL," +
            " UNIQUE  ("+CountEntry.COLUMN_PLAYER_NAME+") ON CONFLICT REPLACE);";
            /*
            //");";
            +", FOREIGN KEY ("+GameEntry.COLUMN_PLAYER1_KEY+") REFERENCES "+
            PlayerEntry.TABLE_NAME+" ("+PlayerEntry.COLUMN_ID+"),"+
            " FOREIGN KEY ("+GameEntry.COLUMN_PLAYER2_KEY+") REFERENCES "+
            PlayerEntry.TABLE_NAME+" ("+PlayerEntry.COLUMN_ID+"));";*/
    public static final int DATABASE_VERSION = 15;
    public static final String DATABASE_NAME = "pipong.db";

    public DataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_GAME_TABLE);
        db.execSQL(SQL_CREATE_PLAYER_TABLE);
        db.execSQL(SQL_CREATE_COUNT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+PlayerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+GameEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+CountEntry.TABLE_NAME);
        onCreate(db);
    }
}
