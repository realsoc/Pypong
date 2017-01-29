package com.realsoc.pipong.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.test.AndroidTestCase;

import java.util.HashMap;

/**
 * Created by Hugo on 16/01/2017.
 */

public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();
    public void deleteAllRecordsFromProvider(){
        mContext.getContentResolver().delete(
                DataContract.PlayerEntry.CONTENT_URI,
                null,
                null);
        mContext.getContentResolver().delete(
                DataContract.GameEntry.CONTENT_URI,
                null,
                null);
        Cursor cursor = mContext.getContentResolver().query(
                DataContract.GameEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertEquals("Error: Records not deleted from Game table during delete",0,cursor.getCount());
        cursor.close();
        cursor = mContext.getContentResolver().query(
                DataContract.PlayerEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertEquals("Error: Records not deleted from Players table during delete",0,cursor.getCount());
        cursor.close();
    }
    public void deleteAllRecords(){
        deleteAllRecordsFromProvider();
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }
    public void testProviderRegistry(){
        PackageManager pm = mContext.getPackageManager();
        ComponentName componentName = new ComponentName(
                mContext.getPackageName(),
                DataProvider.class.getName());
        try{
            ProviderInfo info = pm.getProviderInfo(componentName,0);
            assertEquals("Error: provider registered with " + info.authority + " instead of "+
                    DataContract.CONTENT_AUTHORITY,info.authority,DataContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error provider not registered at " + mContext.getPackageName(), false);
        }
    }


    public void testGetType(){
        /*String type = mContext.getContentResolver().getType(DataContract.GameEntry.CONTENT_URI);
        assertEquals("Error : the GameEntry Content_uri should return GameEntry.CONTENT_TYPE",
                DataContract.GameEntry.CONTENT_TYPE,type);*/

        String testName = "Najda";
        String testName2 = "Hugo";
        int typ = 6;
        String type = mContext.getContentResolver().getType(
                DataContract.PlayerEntry.buildPlayerWithName(testName));
        assertEquals("Error : the PlayerEntry Content_uri with name should return GameEntry.CONTENT_ITEM_TYPE",
                DataContract.PlayerEntry.CONTENT_ITEM_TYPE,type);

        type = mContext.getContentResolver().getType(
                DataContract.GameEntry.buildGameWithPlayerName(testName));
        assertEquals("Error : the GameEntry Content_uri with plname should return GameEntry.CONTENT_TYPE/ "+type +" . "+ DataContract.GameEntry.CONTENT_URI.toString(),
                DataContract.GameEntry.CONTENT_TYPE,type);

        type = mContext.getContentResolver().getType(
                DataContract.GameEntry.buildGameWithDuel(testName,testName2));
        assertEquals("Error : the GameEntry Content_uri with duel should return GameEntry.CONTENT_TYPE",
                DataContract.GameEntry.CONTENT_TYPE,type);

        type = mContext.getContentResolver().getType(
                DataContract.GameEntry.buildGameWithType(typ));
        assertEquals("Error : the GameEntry Content_uri with type should return GameEntry.CONTENT_TYPE",
                DataContract.GameEntry.CONTENT_TYPE,type);

    }
    public void testBasicPlayerQueries(){
        DataDbHelper dbHelper = new DataDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues najdaValue = TestUtilities.createPlayerNajdaValues();
        long najdaRowId = TestUtilities.insertPlayerValues(mContext,najdaValue);
        ContentValues hugoValue = TestUtilities.createPlayerHugoValues();
        long hugoRowID = TestUtilities.insertPlayerValues(mContext,hugoValue);
        ContentValues gameValue = TestUtilities.createGameValues(najdaRowId,hugoRowID);
        long gameId = TestUtilities.insertGameValues(mContext,gameValue);

        assertTrue("Error inserting game in DB",gameId != -1);
        db.close();

        Cursor gameCursor = mContext.getContentResolver().query(
                DataContract.GameEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        //TestUtilities.validatePlayersCursor("testBasicGameQuery", gameCursor,new ContentValues[]{gameValue});

        Cursor playerCursor = mContext.getContentResolver().query(
                DataContract.PlayerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        HashMap<String,ContentValues> hashMap = new HashMap<>();
        hashMap.put("Najda",najdaValue);
        hashMap.put("Hugo",hugoValue);
        TestUtilities.validatePlayersCursor("testBasicPlayerQuery", playerCursor,hashMap);
    }
    public void testInsertReadProvider(){
        ContentValues testValuesHugo = TestUtilities.createPlayerHugoValues();
        ContentValues testValuesNajda = TestUtilities.createPlayerNajdaValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(DataContract.PlayerEntry.CONTENT_URI, true, tco);
        Uri locationUriHugo = mContext.getContentResolver().insert(DataContract.PlayerEntry.CONTENT_URI, testValuesHugo);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        long locationRowIdHugo = ContentUris.parseId(locationUriHugo);

        // Verify we got a row back.
        assertTrue(locationRowIdHugo != -1);


        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.



        Uri locationUriNaj = mContext.getContentResolver().insert(DataContract.PlayerEntry.CONTENT_URI, testValuesNajda);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowIdNaj = ContentUris.parseId(locationUriNaj);

        // Verify we got a row back.
        assertTrue(locationRowIdNaj != -1);
        Cursor cursor = mContext.getContentResolver().query(
                DataContract.PlayerEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        //cursor.moveToNext();
        HashMap<String,ContentValues> hashMap = new HashMap<>();
        hashMap.put("Najda",testValuesNajda);
        hashMap.put("Hugo",testValuesHugo);
        TestUtilities.validatePlayersCursor("testInsertReadProvider. Error validating NajdaEntry.",
                cursor, hashMap);
        // Fantastic.  Now that we have a location, add some weather!

        ContentValues gameValues = TestUtilities.createGameValues(locationRowIdHugo,locationRowIdNaj);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.TestContentObserver.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(DataContract.GameEntry.CONTENT_URI, true, tco);

        Uri gameInsertUri = mContext.getContentResolver()
                .insert(DataContract.GameEntry.CONTENT_URI, gameValues);
        assertTrue(gameInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor gameCursor = mContext.getContentResolver().query(
                DataContract.GameEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateGamesCursor("testInsertReadProvider. Error validating WeatherEntry insert.",
                gameCursor, gameValues);

        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        gameValues.putAll(testValuesNajda);
        gameValues.putAll(testValuesHugo);

        // Get the joined Weather and Location data
        gameCursor = mContext.getContentResolver().query(
                DataContract.GameEntry.buildGameWithPlayerName("Hugo"),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        String[] proj = new String[]{
                DataContract.GameEntry.COLUMN_ID,
                DataContract.GameEntry.COLUMN_PLAYER1_KEY,
                DataContract.GameEntry.COLUMN_PLAYER2_KEY,
                DataContract.GameEntry.COLUMN_SCORE_PLAYER1,
                DataContract.GameEntry.COLUMN_SCORE_PLAYER2,
                DataContract.GameEntry.PLAYER1_ALIAS+"."+ DataContract.PlayerEntry.COLUMN_PLAYER_NAME + " as " + DataContract.GameEntry.PLAYER1_ALIAS,
                DataContract.GameEntry.PLAYER2_ALIAS+"."+ DataContract.PlayerEntry.COLUMN_PLAYER_NAME + " as " + DataContract.GameEntry.PLAYER2_ALIAS,
        };
        SQLiteQueryBuilder toto = DataProvider.testByPlayerName();
        DataDbHelper dbHelper = new DataDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cur = toto.query(db,
                proj,
                DataProvider.sGameWithPlayer,
                new String[]{"Titi","Toto"},
                null,
                null,
                null
        );
        //assertTrue("Cursor null",cur.moveToFirst());
        TestUtilities.validateGamesCursor("testInsertReadProvider.  Error validating joined Weather and Location Data.",
                gameCursor, gameValues);

        // Get the joined Weather and Location data with a start date
        gameCursor = mContext.getContentResolver().query(
                DataContract.GameEntry.buildGameWithDuel("Hugo","Najda"),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateGamesCursor("testInsertReadProvider.  Error validating joined Weather and Location Data with start date.",
                gameCursor, gameValues);

        // Get the joined Weather data for a specific date
        gameCursor = mContext.getContentResolver().query(
                DataContract.GameEntry.buildGameWithType(6),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateGamesCursor("testInsertReadProvider.  Error validating joined Weather and Location data for a specific date.",
                gameCursor, gameValues);
    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our location delete.
        TestUtilities.TestContentObserver playerObserver = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(DataContract.PlayerEntry.CONTENT_URI, true, playerObserver);

        // Register a content observer for our weather delete.
        TestUtilities.TestContentObserver gameObserver = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(DataContract.GameEntry.CONTENT_URI, true, gameObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        playerObserver.waitForNotificationOrFail();
        gameObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(playerObserver);
        mContext.getContentResolver().unregisterContentObserver(gameObserver);
    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertGameValues(long p1Id, long p2Id ) {
        String currentTestDate = TestUtilities.TEST_DATE;
        long millisecondsInADay = 1000*60*60*24;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, currentTestDate+= millisecondsInADay ) {
            ContentValues gameValues = new ContentValues();
            gameValues.put(DataContract.GameEntry.COLUMN_PLAYER1_KEY, p1Id);
            gameValues.put(DataContract.GameEntry.COLUMN_DATE, currentTestDate);
            gameValues.put(DataContract.GameEntry.COLUMN_PLAYER2_KEY, p2Id);
            gameValues.put(DataContract.GameEntry.COLUMN_SCORE_PLAYER1, i);
            gameValues.put(DataContract.GameEntry.COLUMN_SCORE_PLAYER2, 10-i);
            gameValues.put(DataContract.GameEntry.COLUMN_IS_ONLINE, i %2);
            gameValues.put(DataContract.GameEntry.COLUMN_TYPE, (i%2 == 0) ?6 : 11);
            returnContentValues[i] = gameValues;
        }
        return returnContentValues;
    }
    static ContentValues[] createBulkInsertPlayerValues() {
        String basename = "name";
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++ ) {
            ContentValues gameValues = new ContentValues();
            gameValues.put(DataContract.PlayerEntry.COLUMN_PLAYER_NAME, basename+"_"+i);
            gameValues.put(DataContract.PlayerEntry.COLUMN_IS_ONLINE, i %2);
            returnContentValues[i] = gameValues;
        }
        return returnContentValues;
    }

    // Student: Uncomment this test after you have completed writing the BulkInsert functionality
    // in your provider.  Note that this test will work with the built-in (default) provider
    // implementation, which just inserts records one-at-a-time, so really do implement the
    // BulkInsert ContentProvider function.

    public void testPlayerBulkInsert(){
        ContentValues[] vals = createBulkInsertPlayerValues();
        HashMap<String, ContentValues> map = new HashMap<>();
        for(int i = 0;i<BULK_INSERT_RECORDS_TO_INSERT;i++){
            map.put(vals[i].getAsString(DataContract.PlayerEntry.COLUMN_PLAYER_NAME),vals[i]);
        }
        TestUtilities.TestContentObserver obs = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(DataContract.PlayerEntry.CONTENT_URI,true,obs);
        int insertCount = mContext.getContentResolver().bulkInsert(DataContract.PlayerEntry.CONTENT_URI,vals);
        assertEquals("insert failed ",insertCount,BULK_INSERT_RECORDS_TO_INSERT);
        obs.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(obs);
        Cursor c = mContext.getContentResolver().query(
                DataContract.PlayerEntry.CONTENT_URI,
                null,
                null,
                null,
                DataContract.PlayerEntry.COLUMN_PLAYER_NAME + " DESC"
        );
        TestUtilities.validatePlayersCursor("Error validating bulkinsert for players",c,map);
    }
    public void testBulkInsert() {
        // first, let's create a location value
        ContentValues hugoVal = TestUtilities.createPlayerHugoValues();
        ContentValues najVal = TestUtilities.createPlayerNajdaValues();
        Uri hugoUri = mContext.getContentResolver().insert(DataContract.PlayerEntry.CONTENT_URI, hugoVal);
        Uri najUri = mContext.getContentResolver().insert(DataContract.PlayerEntry.CONTENT_URI, najVal);
        long hugoRowId = ContentUris.parseId(hugoUri);
        long najRowId = ContentUris.parseId(hugoUri);

        // Verify we got a row back.
        assertTrue(hugoRowId != -1);
        assertTrue(najRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                DataContract.PlayerEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        HashMap<String, ContentValues> hashmap = new HashMap<>();
        hashmap.put("Najda",najVal);
        hashmap.put("Hugo",hugoVal);

        TestUtilities.validatePlayersCursor("testBulkInsert. Error validating LocationEntry.",
                cursor, hashmap);

        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for weather
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertGameValues(najRowId,hugoRowId);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver gameObserver = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(DataContract.GameEntry.CONTENT_URI, true, gameObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(DataContract.GameEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        gameObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(gameObserver);
        //assertTrue(""+insertCount,false);
        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor= mContext.getContentResolver().query(
                DataContract.GameEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                DataContract.GameEntry.COLUMN_DATE + " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        //cursor.moveToFirst();
        int k = 0;
        //String id = bulkInsertContentValues[k].get(DataContract.GameEntry.COLUMN_ID).toString();
        //String sP1 = bulkInsertContentValues[k].get(DataContract.GameEntry.COLUMN_SCORE_PLAYER1).toString() ;
        //String sP2 = bulkInsertContentValues[k].get(DataContract.GameEntry.COLUMN_SCORE_PLAYER2).toString();
        //String s = "";
        //for(int i = 0;i<10;i++){

        //}
        //assertTrue("GO id "+id+" sp1 "+sP1+" sp2 "+sP2+"",false);
        HashMap<Integer,ContentValues> hash = new HashMap<>();
        for(int i=0;i<BULK_INSERT_RECORDS_TO_INSERT;i++){
            hash.put(bulkInsertContentValues[i].getAsInteger(DataContract.GameEntry.COLUMN_ID),bulkInsertContentValues[i]);
        }

        TestUtilities.validateSeveralsGamesCursor("testBulkInsert.  Error validating WeatherEntry ",
                    cursor, hash);
        cursor.close();
    }
}
