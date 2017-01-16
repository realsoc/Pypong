package com.realsoc.pipong;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.realsoc.pipong.data.DataContract;
import com.realsoc.pipong.data.DataDbHelper;

import java.util.HashSet;

/**
 * Created by Hugo on 16/01/2017.
 */

public class TestDb  extends AndroidTestCase{
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteDb(){
        mContext.deleteDatabase(DataDbHelper.DATABASE_NAME);
    }
    public void setUp(){
        deleteDb();
    }
    public void testCreateDb() throws Throwable{
        final HashSet<String> tableNameHashset = new HashSet<>();
        tableNameHashset.add(DataContract.GameEntry.TABLE_NAME);
        tableNameHashset.add(DataContract.PlayerEntry.TABLE_NAME);
        mContext.deleteDatabase(DataDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new DataDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true,db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'",null);
        assertTrue("Error: this means that the database has not been created correctly",c.moveToFirst());
        do{
            tableNameHashset.remove(c.getString(0));
        }while (c.moveToNext());
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashset.isEmpty());

        c = db.rawQuery("PRAGMA table_info("+DataContract.GameEntry.TABLE_NAME+")",null);
         assertTrue("Error : this means that we were unable to query the database for game table information.",
                 c.moveToFirst());

        final HashSet<String> gameColumnHashSet = new HashSet<>();
        gameColumnHashSet.add(DataContract.GameEntry._ID);
        gameColumnHashSet.add(DataContract.GameEntry.COLUMN_DATE);
        gameColumnHashSet.add(DataContract.GameEntry.COLUMN_IS_ONLINE);
        gameColumnHashSet.add(DataContract.GameEntry.COLUMN_PLAYER1_KEY);
        gameColumnHashSet.add(DataContract.GameEntry.COLUMN_PLAYER2_KEY);
        gameColumnHashSet.add(DataContract.GameEntry.COLUMN_SCORE_PLAYER1);
        gameColumnHashSet.add(DataContract.GameEntry.COLUMN_SCORE_PLAYER2);
        gameColumnHashSet.add(DataContract.GameEntry.COLUMN_TYPE);

        final HashSet<String> playerColumnHashSet = new HashSet<>();
        playerColumnHashSet.add(DataContract.PlayerEntry._ID);
        playerColumnHashSet.add(DataContract.PlayerEntry.COLUMN_PLAYER_NAME);
        playerColumnHashSet.add(DataContract.PlayerEntry.COLUMN_IS_ONLINE);

        int columnNameIndex = c.getColumnIndex("name");
        do{
            String columnName = c.getString(columnNameIndex);
            gameColumnHashSet.remove(columnName);
        }while (c.moveToNext());

        assertTrue("Error : The database doesn't contain all of the required game entry columns",
                gameColumnHashSet.isEmpty());



        c = db.rawQuery("PRAGMA table_info("+ DataContract.PlayerEntry.TABLE_NAME+")",null);
        assertTrue("Error : this means that we were unable to query the database for player table information.",
                c.moveToFirst());
        columnNameIndex = c.getColumnIndex("name");
        do{
            String columnName = c.getString(columnNameIndex);
            playerColumnHashSet.remove(columnName);
        }while (c.moveToNext());

        assertTrue("Error : The database doesn't contain all of the required player entry columns",
                playerColumnHashSet.isEmpty());
        db.close();

    }
    public void testGameTable(){
//TODO : LETS DO THIS WELL
    }
    public void testPlayerTable(){
//TODO : ISSO TAMBEM
    }
    public Long insertPlayer(){
        return -1L;
    }
}
