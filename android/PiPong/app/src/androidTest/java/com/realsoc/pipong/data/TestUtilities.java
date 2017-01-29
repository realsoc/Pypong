package com.realsoc.pipong.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;
import android.text.format.DateFormat;

import com.realsoc.pipong.utils.PollingCheck;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by Hugo on 17/01/2017.
 */
public class TestUtilities extends AndroidTestCase{
    static final String NAME = "Najda";
    static final String NAME2 = "Hugo";
    static final String TEST_DATE = new DateFormat().format("yyyy-mm-dd",new Date()).toString();
    public static ContentValues createPlayerNajdaValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(DataContract.PlayerEntry.COLUMN_PLAYER_NAME, NAME);
        testValues.put(DataContract.PlayerEntry.COLUMN_0GL, 0);
        testValues.put(DataContract.PlayerEntry.COLUMN_11GL, 1);
        testValues.put(DataContract.PlayerEntry.COLUMN_21PS, 7);
        testValues.put(DataContract.PlayerEntry.COLUMN_IS_ONLINE, DataContract.FALSE);
        return testValues;
    }
    public static ContentValues createPlayerHugoValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(DataContract.PlayerEntry.COLUMN_PLAYER_NAME, NAME2);
        testValues.put(DataContract.PlayerEntry.COLUMN_0GL, 0);
        testValues.put(DataContract.PlayerEntry.COLUMN_11GL, 1);
        testValues.put(DataContract.PlayerEntry.COLUMN_21PS, 7);
        testValues.put(DataContract.PlayerEntry.COLUMN_IS_ONLINE, DataContract.TRUE);
        testValues.put(DataContract.PlayerEntry.COLUMN_ID, 5);
        return testValues;
    }

    public static long insertPlayerValues(Context mContext,ContentValues values) {
        DataDbHelper dbHelper = new DataDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long playerRowId = db.insert(DataContract.PlayerEntry.TABLE_NAME,null,values);

        assertTrue("Error : failure to insert player values",playerRowId != -1);
        return playerRowId;

    }

    public static ContentValues createGameValues(long player1Id, long player2Id) {
        ContentValues retVal = new ContentValues();
        retVal.put(DataContract.GameEntry.COLUMN_TYPE, 6);
        retVal.put(DataContract.GameEntry.COLUMN_PLAYER1_KEY,player1Id);
        retVal.put(DataContract.GameEntry.COLUMN_PLAYER2_KEY,player2Id);
        retVal.put(DataContract.GameEntry.COLUMN_IS_ONLINE,DataContract.FALSE);
        retVal.put(DataContract.GameEntry.COLUMN_SCORE_PLAYER1, 6);
        retVal.put(DataContract.GameEntry.COLUMN_SCORE_PLAYER2, 0);
        return retVal;
    }

    public static long insertGameValues(Context mContext, ContentValues gameValue) {
        DataDbHelper dbHelper = new DataDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long gameRowId = db.insertOrThrow(DataContract.GameEntry.TABLE_NAME,null,gameValue);
        assertTrue("Error : failure to insert game values",gameRowId != -1);
        return gameRowId;
    }
    public static void validateGamesCursor(String error, Cursor gameCursor, ContentValues gamesValues) {
        assertTrue("Empty cursor returned."+error,gameCursor.moveToFirst());
        validateCurrentRecord(error,gameCursor,gamesValues);
        //gameCursor.close();
    }



    public static void validatePlayersCursor(String error, Cursor gameCursor, HashMap<String, ContentValues> gamesValues) {
        assertTrue("Empty cursor returned."+error,gameCursor != null);
        gameCursor.move(-1);
        assertTrue("Cursor not set before first."+error,gameCursor.isBeforeFirst());
        int i =0;
        assertTrue("Cursor size "+ gameCursor.getCount()+" different than gamesVal size "+gamesValues.size(),gamesValues.size() == gameCursor.getCount());
        while(gameCursor.moveToNext()){
            validateCurrentRecord(error,gameCursor,gamesValues.get(gameCursor.getString(gameCursor.getColumnIndex(DataContract.PlayerEntry.COLUMN_PLAYER_NAME))));
        }
        gameCursor.close();
    }

    private static void validateCurrentRecord(String error, Cursor gameCursor, ContentValues gameValue) {
        Set<Map.Entry<String,Object>> valueSet = gameValue.valueSet();
        for(Map.Entry<String,Object> entry : valueSet){
            String columName = entry.getKey();
            int idx = gameCursor.getColumnIndex(columName);
            if(idx == -1)
                continue;
            assertFalse("Column '"+columName+"' not found." + error,idx ==-1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value "+entry.getKey()+"'"+expectedValue+"' did not match expected value '"+
            gameCursor.getString(idx).toString()+ "'. "+ error,expectedValue,gameCursor.getString(idx));
        }
    }

    public static void validateSeveralsGamesCursor(String s, Cursor cursor, HashMap<Integer,ContentValues> hashMap) {
        assertTrue("Empty cursor returned."+s,cursor != null);
        cursor.move(-1);
        assertTrue("Cursor not set before first."+s,cursor.isBeforeFirst());
        int i =0;
        assertTrue("Cursor size "+ cursor.getCount()+" different than games size "+hashMap.size(),hashMap.size() == cursor.getCount());
        while(cursor.moveToNext()){
            validateCurrentRecord(s,cursor,hashMap.get(cursor.getInt(cursor.getColumnIndex(DataContract.GameEntry.COLUMN_ID))));
        }
        cursor.close();
    }


    public static class TestContentObserver extends ContentObserver{
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver(){
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht){
            super(new Handler(ht.getLooper()));
            this.mHT = ht;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange,null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail(){
            new PollingCheck(5000){
                @Override
                protected boolean check(){
                    return  mContentChanged;
                }
            }.run();
                    mHT.quit();
        }
    }


}
