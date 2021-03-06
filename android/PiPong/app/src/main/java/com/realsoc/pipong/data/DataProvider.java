package com.realsoc.pipong.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;

import com.realsoc.pipong.data.DataContract.GameEntry;
import com.realsoc.pipong.data.DataContract.PlayerEntry;

/**
 * Created by Hugo on 16/01/2017.
 */

public class DataProvider extends ContentProvider {
    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private static final String LOG_TAG = "DataProvider";
    private DataDbHelper mDataDbHelper;

    public static final int PLAYER = 100;
    public static final int PLAYER_WITH_NAME = 101;
    public static final int PLAYER_OFFLINE = 102;
    public static final int GAME = 200;
    public static final int GAME_WITH_PLAYER = 201;
    public static final int GAME_WITH_DUEL = 202;
    public static final int GAME_WITH_TYPE = 203;
    public static final int GAME_WITH_DATE = 204;
    public static final int GAME_OFFLINE = 205;
    public static final int COUNT = 300;
    public static final int COUNT_WITH_NAME = 301;


    private static final SQLiteQueryBuilder sGameWithNameQueryBuilder;

    static{
        sGameWithNameQueryBuilder = new SQLiteQueryBuilder();
        sGameWithNameQueryBuilder.setTables(
                GameEntry.TABLE_NAME +" AS "+GameEntry.ALIAS+
                        " INNER JOIN " + PlayerEntry.TABLE_NAME + " AS " + PlayerEntry.TABLE_NAME_ALIAS_1+
                        " ON " + GameEntry.ALIAS +
                        "." + GameEntry.COLUMN_PLAYER1_ID +
                        " = " + PlayerEntry.TABLE_NAME_ALIAS_1 +
                        "." + PlayerEntry.COLUMN_ID +
                        " INNER JOIN " + PlayerEntry.TABLE_NAME + " AS " + PlayerEntry.TABLE_NAME_ALIAS_2+
                        " ON " + GameEntry.ALIAS +
                        "." + GameEntry.COLUMN_PLAYER2_ID +
                        " = " + PlayerEntry.TABLE_NAME_ALIAS_2 +
                        "." + PlayerEntry.COLUMN_ID
        );
    }
    private static final SQLiteQueryBuilder sCountWithNameQueryBuilder;

    static{
        sCountWithNameQueryBuilder = new SQLiteQueryBuilder();
        sCountWithNameQueryBuilder.setTables(
                DataContract.CountEntry.TABLE_NAME +" AS "+ DataContract.CountEntry.ALIAS+
                        " INNER JOIN " + PlayerEntry.TABLE_NAME +
                        " ON " + DataContract.CountEntry.ALIAS +
                        "." + DataContract.CountEntry.COLUMN_PLAYER_ID +
                        " = " + PlayerEntry.TABLE_NAME +
                        "." + PlayerEntry.COLUMN_ID
        );
    }
    public static final String sGameWithPlayer =
            DataContract.GameEntry.COLUMN_PLAYER1_NAME + " = ? OR " +
                    DataContract.GameEntry.COLUMN_PLAYER2_NAME + " = ?";
    private static final String sGameWithDuel =
            "( "+ DataContract.GameEntry.COLUMN_PLAYER1_NAME + " = ? AND " +
                    DataContract.GameEntry.COLUMN_PLAYER2_NAME + " = ? ) OR ( " +
                    DataContract.GameEntry.COLUMN_PLAYER1_NAME +" = ? AND " +
                    DataContract.GameEntry.COLUMN_PLAYER2_NAME + " = ? )";
    private static final String sGameWithDate =
            DataContract.GameEntry.TABLE_NAME +
                    "." + DataContract.GameEntry.COLUMN_DATE + " = ?";
    private static final String sGameWithType =
            DataContract.GameEntry.TABLE_NAME +
                    "." + DataContract.GameEntry.COLUMN_TYPE + " = ?";
    private static final String sGameOffline =
            DataContract.GameEntry.TABLE_NAME +
                    "." + DataContract.GameEntry.COLUMN_IS_ONLINE + " = 0";
    private static final String sCountWithPlayer =
            DataContract.CountEntry.COLUMN_PLAYER_NAME + " = ?";

    private static final String sPlayerOffline =
            DataContract.PlayerEntry.TABLE_NAME +
                    "." + DataContract.PlayerEntry.COLUMN_IS_ONLINE + " = 0";
    /*public static SQLiteQueryBuilder testByPlayerName(){
        return sGameWithNameQueryBuilder;
    }*/
    private Cursor getGameByPlayerName(Uri uri, String[] projection,String sortOrder){
        String playerName = DataContract.GameEntry.getFirstPlayerNameFromUri(uri);
        return sGameWithNameQueryBuilder.query(mDataDbHelper.getReadableDatabase(),
                projection,
                sGameWithPlayer,
                new String[]{playerName,playerName},
                null,
                null,
                sortOrder
                );
    }
    private Cursor getGameByDuel(Uri uri, String[] projection, String sortOrder){
        String player1Name = DataContract.GameEntry.getFirstPlayerNameFromUri(uri);
        String player2Name = DataContract.GameEntry.getSecondPlayerNameFromUri(uri);
        return  sGameWithNameQueryBuilder.query(mDataDbHelper.getReadableDatabase(),
                projection,
                sGameWithDuel,
                new String[]{player1Name,player2Name,player2Name,player1Name},
                null,
                null,
                sortOrder
        );

    }
    private Cursor getGameByType(Uri uri,String[] projection, String sortOrder){
        String type = DataContract.GameEntry.getGameTypeFromUri(uri);
        return sGameWithNameQueryBuilder.query(mDataDbHelper.getReadableDatabase(),
                projection,
                sGameWithType,
                new String[]{type},
                null,
                null,
                sortOrder);
    }

    private Cursor getGameByDate(Uri uri, String[] projection, String sortOrder) {
        String date = DataContract.GameEntry.getGameDateFromUri(uri);
        return sGameWithNameQueryBuilder.query(mDataDbHelper.getReadableDatabase(),
                projection,
                sGameWithDate,
                new String[]{date},
                null,
                null,
                sortOrder
        );
    }
    private Cursor getCountByPlayerName(Uri uri, String[] projection, String sortOrder) {
        String playerName = DataContract.CountEntry.getPlayerNameFromUri(uri);
        return sCountWithNameQueryBuilder.query(mDataDbHelper.getReadableDatabase(),
                projection,
                sCountWithPlayer,
                new String[]{playerName},
                null,
                null,
                sortOrder
        );
    }
    private Cursor getGameOffline(String[] projection,String sortOrder){
        return sGameWithNameQueryBuilder.query(mDataDbHelper.getReadableDatabase(),
                projection,
                sGameOffline,
                null,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getPlayerOffline(String[] projection,String sortOrder){
        return mDataDbHelper.getReadableDatabase().query(
                DataContract.PlayerEntry.TABLE_NAME,
                projection,
                sPlayerOffline,
                null,null,null,sortOrder
        );
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
        Cursor retCursor;
        switch (mUriMatcher.match(uri)){
            case GAME_OFFLINE:
                retCursor = getGameOffline(projection,sortOrder);
                break;
            case PLAYER_OFFLINE:
                retCursor = getPlayerOffline(projection,sortOrder);
                break;
            case GAME_WITH_PLAYER:
                retCursor = getGameByPlayerName(uri,projection,sortOrder);
                break;
            case GAME_WITH_DUEL:
                retCursor = getGameByDuel(uri,projection,sortOrder);
                break;
            case GAME_WITH_DATE:
                retCursor = getGameByDate(uri,projection,sortOrder);
                break;
            case GAME_WITH_TYPE:
                retCursor = getGameByType(uri,projection,sortOrder);
                break;
            case GAME:
                retCursor = sGameWithNameQueryBuilder.query(mDataDbHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PLAYER:
                retCursor = mDataDbHelper.getReadableDatabase().query(
                        DataContract.PlayerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case COUNT:
                retCursor = sCountWithNameQueryBuilder.query(mDataDbHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case COUNT_WITH_NAME:
                retCursor = getCountByPlayerName(uri,projection,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher nUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;

        nUriMatcher.addURI(authority, DataContract.PATH_GAME, GAME);
        nUriMatcher.addURI(authority, DataContract.PATH_GAME + "/type/#", GAME_WITH_TYPE);
        nUriMatcher.addURI(authority, DataContract.PATH_GAME + "/name/*", GAME_WITH_PLAYER);
        nUriMatcher.addURI(authority, DataContract.PATH_GAME + "/duel/*/*", GAME_WITH_DUEL);
        nUriMatcher.addURI(authority, DataContract.PATH_GAME + "/offline", GAME_OFFLINE);

        nUriMatcher.addURI(authority, DataContract.PATH_PLAYER,PLAYER);
        nUriMatcher.addURI(authority, DataContract.PATH_PLAYER + "/name/*",PLAYER_WITH_NAME);
        nUriMatcher.addURI(authority, DataContract.PATH_PLAYER + "/offline",PLAYER_OFFLINE);

        nUriMatcher.addURI(authority, DataContract.PATH_COUNT,COUNT);
        nUriMatcher.addURI(authority, DataContract.PATH_COUNT +"/name/*",COUNT_WITH_NAME);
        return nUriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDataDbHelper = new DataDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);
        switch (match){
            case PLAYER:
                return DataContract.PlayerEntry.CONTENT_TYPE;
            case PLAYER_WITH_NAME:
                return DataContract.PlayerEntry.CONTENT_ITEM_TYPE;
            case PLAYER_OFFLINE:
                return DataContract.PlayerEntry.CONTENT_TYPE;
            case GAME:
                return DataContract.GameEntry.CONTENT_TYPE;
            case GAME_WITH_DUEL:
                return DataContract.GameEntry.CONTENT_TYPE;
            case GAME_WITH_TYPE:
                return DataContract.GameEntry.CONTENT_TYPE;
            case GAME_WITH_PLAYER:
                return DataContract.GameEntry.CONTENT_TYPE;
            case GAME_OFFLINE:
                return DataContract.GameEntry.CONTENT_TYPE;
            case COUNT:
                return DataContract.CountEntry.CONTENT_TYPE;
            case COUNT_WITH_NAME:
                return DataContract.CountEntry.CONTENT_ITEM_TYPE;
            default:
                throw  new UnsupportedOperationException("Unknown uri: " + uri +" match "+match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDataDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case PLAYER: {
                long _id = db.insertOrThrow(DataContract.PlayerEntry.TABLE_NAME, null, values);

                if (_id > 0)
                    returnUri = DataContract.PlayerEntry.buildPlayerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case GAME: {
                long _id = -1;
                try{
                    _id = db.insertOrThrow(DataContract.GameEntry.TABLE_NAME, null, values);
                }catch (SQLException e){
                    e.printStackTrace();
                }
                if (_id > 0)
                    returnUri = DataContract.GameEntry.buildGameUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case COUNT: {
                long _id = db.insertOrThrow(DataContract.CountEntry.TABLE_NAME,null,values);
                if(_id > 0)
                    returnUri = DataContract.CountEntry.buildCountUri(_id);
                else
                    throw  new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDataDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsDeleted;
        if(null == selection) selection = "1";
        switch (match){
            case PLAYER:
                rowsDeleted = db.delete(DataContract.PlayerEntry.TABLE_NAME, selection,selectionArgs);
                break;
            case GAME:
                rowsDeleted = db.delete(DataContract.GameEntry.TABLE_NAME, selection,selectionArgs);
                break;
            case COUNT:
                rowsDeleted = db.delete(DataContract.CountEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri);
        }
        if(rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDataDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rowsUpdated;
        switch (match){
            case PLAYER:
                rowsUpdated = db.update(DataContract.PlayerEntry.TABLE_NAME, values,selection,selectionArgs);
                break;
            case GAME:
                rowsUpdated = db.update(DataContract.GameEntry.TABLE_NAME, values, selection,selectionArgs);
                break;
            case COUNT:
                rowsUpdated = db.update(DataContract.CountEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri);
        }
        if(rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;
    }
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDataDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int returnCount = 0;
        switch (match){
            case GAME: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertOrThrow(DataContract.GameEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                            value.put(DataContract.GameEntry.COLUMN_ID,_id);
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            }
            case PLAYER: {
                db.beginTransaction();
                try{
                    for(ContentValues value : values){
                        long _id = db.insertOrThrow(DataContract.PlayerEntry.TABLE_NAME,null,value);
                        if(_id != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            }
            case COUNT: {
                db.beginTransaction();
                try{
                    for(ContentValues value: values){
                        long _id = db.insertOrThrow(DataContract.CountEntry.TABLE_NAME,null,value);
                        if(_id != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                break;
            }
            default:
                return super.bulkInsert(uri,values);
        }
        return returnCount;
    }

    @Override
    public void shutdown() {
        mDataDbHelper.close();
        super.shutdown();
    }


}
