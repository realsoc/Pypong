package com.realsoc.pipong.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Hugo on 16/01/2017.
 */

public class DataContract {
    public static final String CONTENT_AUTHORITY = "com.realsoc.pipong.data.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);


    public static final String PATH_PLAYER = "player";
    public static final String PATH_GAME = "game";
    public static final String PATH_COUNT = "count";
    public static final int FALSE = 0;
    public static final int TRUE = 1;

    public static final class CountEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COUNT).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COUNT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COUNT;

        public static final String TABLE_NAME = "count";

        public static final String COLUMN_ID = "count_id";
        public static final String COLUMN_PLAYER_NAME = "name";
        public static final String COLUMN_6PL = "point_lost_6";
        public static final String COLUMN_11PL = "point_lost_11";
        public static final String COLUMN_21PL = "point_lost_21";
        //public static final String COLUMN_0PL = "point_lost_0";
        public static final String COLUMN_6PS = "point_scored_6";
        public static final String COLUMN_11PS = "point_scored_11";
        public static final String COLUMN_21PS = "point_scored_21";
        //public static final String COLUMN_0PS = "point_scored_0";
        public static final String COLUMN_6GW = "game_won_6";
        public static final String COLUMN_11GW = "game_won_11";
        public static final String COLUMN_21GW = "game_won_21";
        //public static final String COLUMN_0GW = "game_won_0";
        public static final String COLUMN_6GL = "game_lost_6";
        public static final String COLUMN_11GL = "game_lost_11";
        public static final String COLUMN_21GL = "game_lost_21";
        //public static final String COLUMN_0GL = "game_lost_0";
        public static final String COLUMN_6GP = "game_played_6";
        public static final String COLUMN_11GP = "game_played_11";
        public static final String COLUMN_21GP = "game_played_21";
        //public static final String COLUMN_0GP = "game_played_0";


        public static Uri buildCountUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static String getPlayerNameFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
        public static Uri buildCountWithName(String name){
            return CONTENT_URI.buildUpon().appendPath(COLUMN_PLAYER_NAME).appendPath(name).build();
        }
    }
    public static final class GameEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GAME).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GAME;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GAME;
        public static final String TABLE_NAME = "game";

        public static final String COLUMN_ID = "game_id";
        public static final String COLUMN_PLAYER1_NAME = "player1_name";
        public static final String COLUMN_PLAYER2_NAME = "player2_name";
        public static final String COLUMN_PLAYER1_SCORE = "player1_score";
        public static final String COLUMN_PLAYER2_SCORE = "player2_score";
        public static final String COLUMN_IS_ONLINE = "is_online";
        public static final String COLUMN_DATE ="date";
        public static final String COLUMN_TYPE = "type";

        public static final String ALIAS_NAME = "name";
        public static final String ALIAS_DUEL = "duel";
        public static final String ALIAS_TYPE = "type";
        public static final String ALIAS_DATE = "date";


        public static Uri buildGameWithPlayerName(String name){
            return CONTENT_URI.buildUpon().appendPath(ALIAS_NAME).appendPath(name).build();
        }
        public static Uri buildGameWithDuel(String player1,String player2){
            return CONTENT_URI.buildUpon().appendPath(ALIAS_DUEL).appendPath(player1).appendPath(player2).build();
        }
        public static Uri buildGameWithType(int type){
            return CONTENT_URI.buildUpon().appendPath(ALIAS_TYPE).appendPath(Integer.toString(type)).build();
        }
        public static Uri buildGameUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildGameWithDate(String date){
            return CONTENT_URI.buildUpon().appendPath(ALIAS_DATE).appendPath(date).build();
        }
        public static String getFirstPlayerNameFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
        public static String getSecondPlayerNameFromUri(Uri uri){
            return uri.getPathSegments().get(3);
        }
        public static String getGameTypeFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getGameDateFromUri(Uri uri) { return uri.getPathSegments().get(2);
        }
    }
    public static final class PlayerEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAYER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLAYER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLAYER;
        public static final String TABLE_NAME = "player";

        public static final String COLUMN_PLAYER_NAME = "name";
        public static final String COLUMN_IS_ONLINE = "is_online";
        public static final String COLUMN_ID = "player_id";
        public static Uri buildPlayerUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildPlayerWithName(String name){
            return CONTENT_URI.buildUpon().appendPath(COLUMN_PLAYER_NAME).appendPath(name).build();
        }
        
        
    }
}
