package com.realsoc.pipong.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Hugo on 16/01/2017.
 */

public class DataContract {
    public static final String CONTENT_AUTHORITY = "com.realsoc.pipong";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);


    public static final String PATH_PLAYER = "player";
    public static final String PATH_GAME = "game";

    public static final class GameEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GAME).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GAME;
        public static final String TABLE_NAME = "Game";

        public static final String COLUMN_PLAYER1_KEY = "player1_id";
        public static final String COLUMN_PLAYER2_KEY = "player2_id";
        public static final String COLUMN_SCORE_PLAYER1 = "score_player1";
        public static final String COLUMN_SCORE_PLAYER2 = "score_player2";
        public static final String COLUMN_IS_ONLINE = "is_game_online";
        public static final String COLUMN_DATE ="date";
        public static final String COLUMN_TYPE = "type";


    }
    public static final class PlayerEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAYER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLAYER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLAYER;
        public static final String TABLE_NAME = "player";

        public static final String COLUMN_PLAYER_NAME = "name";
        public static final String COLUMN_IS_ONLINE = "is_player_online";
    }
}
