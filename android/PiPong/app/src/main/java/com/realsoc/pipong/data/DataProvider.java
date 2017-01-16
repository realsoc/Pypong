package com.realsoc.pipong.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Hugo on 16/01/2017.
 */

public class DataProvider extends ContentProvider {
    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private DataDbHelper mDataDbHelper;

    public static final int PLAYER = 100;
    public static final int PLAYER_WITH_NAME = 101;
    public static final int GAME = 200;
    public static final int GAME_WITH_PLAYER_NAME = 201;
    public static final int GAME_WITH_DUEL = 202;
    public static final int GAME_WITH_DATE = 203;
    public static final int GAME_WITH_TYPE = 204;


    private static UriMatcher buildUriMatcher() {
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
