package com.realsoc.pipong.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.realsoc.pipong.data.DataContract;
import com.realsoc.pipong.model.CountModel;
import com.realsoc.pipong.model.GameModel;
import com.realsoc.pipong.model.PlayerModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Hugo on 23/01/2017.
 */

public class DataUtils {
    private static final String LOG_TAG = "DataUtils";
    //private static long lastUpdate = -1;
    private static Context mContext = null;
    private static DataUtils mDataUtils = null;
    private final static Object singletonLock = new Object();
    private HashMap<String,PlayerModel> players = null;
    private ArrayList<String> playerAsStringList = null;
    private HashMap<String,CountModel> counts = null;
    private ArrayList<GameModel> games = null;

    private DataUtils(Context context){
        mContext = context;
        initPlayers();
        initGames();
        initCounts();
        if(counts.size() == 0){
            Log.d(LOG_TAG,"Count empty");
        }else if(counts.size() != players.size())
            Log.d(LOG_TAG,"BAD : Players & Counts have different size");
    }
    public static DataUtils getInstance(Context context){
        synchronized (singletonLock){
            if(mDataUtils == null){
                mDataUtils = new DataUtils(context);
            }
        }
        return mDataUtils;
    }

    public void addPlayer(PlayerModel player){
        if(players.containsKey(player.getName())){
            Log.d(LOG_TAG,"Player already existing");
        }else{
            Uri uri = addPlayerInDB(player);
            player.setId(ContentUris.parseId(uri));
            players.put(player.getName(),player);
            playerAsStringList.add(player.getName());
            CountModel newCount = new CountModel(player.getName());
            addCount(newCount);
        }
    }
    public void addGame(GameModel game){
        Uri uri = addGameInDB(game);
        game.setId(ContentUris.parseId(uri));
        games.add(game);
        updateCounts(game);
    }
    public HashMap<String,PlayerModel> getPlayers(){
        return players;
    }
    public ArrayList<GameModel> getGames(){
        return games;
    }
    public ArrayList<String> getPlayerAsStringList(){
        return playerAsStringList;
    }
    public HashMap<String,CountModel> getCounts(){
        return counts;
    }

    public boolean isInit() {
        return (players != null) && (games != null) && (counts != null);
    }

    public int dropOfflinePlayers(){
        final String offlineString = DataContract.PlayerEntry.COLUMN_IS_ONLINE + " = 0";
        return mContext.getContentResolver().delete(DataContract.PlayerEntry.CONTENT_URI,offlineString,null);
    }
    public int dropOfflineGames(){
        final String offlineString = DataContract.GameEntry.COLUMN_IS_ONLINE + " = 0";
        return mContext.getContentResolver().delete(DataContract.GameEntry.CONTENT_URI,offlineString,null);
    }
    public int dropCounts(){
        return mContext.getContentResolver().delete(DataContract.CountEntry.CONTENT_URI,null,null);
    }

    public PlayerModel getPlayerById(long id, ArrayList<PlayerModel> players){
        for(PlayerModel player : players){
            if(player.getId() == id){
                return player;
            }
        }
        return null;
    }
    public CountModel getCountByNameFromDB(String name){
        Uri uri = DataContract.CountEntry.buildCountWithName(name);
        Cursor countCursor = mContext.getContentResolver().query(uri,null,null,null,null);
        if(countCursor.getCount()<1){
            Log.d(LOG_TAG,"This player does not exists");
            return null;
        }
        countCursor.moveToFirst();
        return ((CountModel) getModelFromCursor(countCursor, CountModel.class));
    }
    public PlayerModel getPlayerByName(String name, ArrayList<PlayerModel> players){
        for(PlayerModel player : players){
            if(player.getName().equals(name)){
                return player;
            }
        }
        return null;
    }
    private void initPlayers(){
        players = getPlayersFromDB(null,null,null,null);
        if(players == null)
            Log.d(LOG_TAG,"Error getting players");
        else if(players.size() == 0)
            Log.d(LOG_TAG,"Players empty");
        if(counts != null && counts.size() != players.size()){
            Log.d(LOG_TAG,"Players & Counts have different size");
            createCountDB();
            counts = getCountsFromDB(null,null,null,null);
        }
        playerAsStringList = new ArrayList<>(players.keySet());
    }
    private void initGames(){
        games = getGamesFromDB(null,null,null,null);
        if(games == null)
            Log.d(LOG_TAG,"Error getting games");
        else if(players.size() == 0)
            Log.d(LOG_TAG,"Games empty");
    }
    private void initCounts(){
        counts = getCountsFromDB(null,null,null,null);
        if(counts == null)
            Log.d(LOG_TAG,"Error getting counts");
        else if(players.size() == 0)
            Log.d(LOG_TAG,"Counts empty");
        if(players != null && counts.size() != players.size())
            Log.d(LOG_TAG,"Players & Counts have different size");
    }
    private void addCount(CountModel countModel){
        if(counts.containsKey(countModel.getName())){
            Log.d(LOG_TAG,"Count already exists");
        }else{
            Uri mUri = addCountInDB(countModel);
            countModel.setId(ContentUris.parseId(mUri));
            counts.put(countModel.getName(),countModel);
        }
    }
    private Uri addCountInDB(CountModel count){
        Log.d(LOG_TAG,"Adding count in db "+count.getName());
        ContentValues countValues = createCountValuesFromModel(count);
        return mContext.getContentResolver().insert(DataContract.CountEntry.CONTENT_URI,countValues);
    }
    private Uri addPlayerInDB(PlayerModel player){
        ContentValues playerValues = createPlayerValuesFromModel(player);
        return mContext.getContentResolver().insert(DataContract.PlayerEntry.CONTENT_URI,playerValues);
    }
    private Uri addGameInDB(GameModel game){
        ContentValues gameValues = createGameValuesFromModel(game);
        return mContext.getContentResolver().insert(DataContract.GameEntry.CONTENT_URI,gameValues);
    }
    private void updateCounts(GameModel game) {
        String player1 = game.getPlayer1();
        String player2 = game.getPlayer2();
        HashSet<String> playerSet = new HashSet<>();
        playerSet.add(player1);
        playerSet.add(player2);
        int type = game.getType();
        int scorePlayer1 = game.getScorePlayer1();
        int scorePlayer2 = game.getScorePlayer2();
        counts.get(player1).addGame(type,scorePlayer1,scorePlayer2);
        counts.get(player2).addGame(type,scorePlayer2,scorePlayer1);
        updateCountsInDB(playerSet);
    }
    private void updateCountsInDB(HashSet<String> playerSet){
        for(String player : playerSet){
            CountModel count = counts.get(player);
            ContentValues value = createCountValuesFromModel(count);
            int ret = mContext.getContentResolver().update(
                    DataContract.CountEntry.CONTENT_URI,
                    value,
                    DataContract.CountEntry.COLUMN_ID+" = "+count.getId(),
                    null);
            if(ret != 1){
                Log.d(LOG_TAG,"Problem updating count : rowupdated = "+ret);
            }
        }
    }
    private HashMap<String,PlayerModel> getPlayersFromDB(String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor playersCursor = mContext.getContentResolver().query(
                DataContract.PlayerEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
        return createPlayerHashMapFromCursor(playersCursor);
    }
    private ArrayList<GameModel> getGamesFromDB(String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor gamesCursor = mContext.getContentResolver().query(
                DataContract.GameEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
        return createGameArrayListFromCursor(gamesCursor);
    }
    private HashMap<String,CountModel> getCountsFromDB(String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor countCursor = mContext.getContentResolver().query(
                DataContract.CountEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
        return createCountHashMapFromCursor(countCursor);
    }
    private HashMap<String, CountModel> createCountHashMapFromCursor(Cursor countCursor) {
        HashMap<String, CountModel> ret = new HashMap<>();
        if(countCursor.getCount()<1){
            Log.d(LOG_TAG,"Error count cursor empty");
            return ret;
        }
        countCursor.moveToFirst();
        do{
            CountModel newCount = (CountModel)getModelFromCursor(countCursor,CountModel.class);
            ret.put(newCount.getName(),newCount);
        }while (countCursor.moveToNext());
        return ret;
    }
    private ArrayList<GameModel> createGameArrayListFromCursor(Cursor gamesCursor) {
        ArrayList<GameModel> ret = new ArrayList<>();
        if(gamesCursor.getCount()<1)
            return ret;
        gamesCursor.moveToFirst();
        do{
            ret.add((GameModel)getModelFromCursor(gamesCursor,GameModel.class));
        }while (gamesCursor.moveToNext());
        return ret;
    }

    private HashMap<String,PlayerModel> createPlayerHashMapFromCursor(Cursor playersCursor) {
        HashMap<String,PlayerModel> ret = new HashMap<>();
        if(playersCursor.getCount()<1){
            Log.d(LOG_TAG,"PLAYER CURSOR EMPTY");
            return ret;
        }
        playersCursor.moveToFirst();
        do{
            PlayerModel tempPlayer = (PlayerModel) getModelFromCursor(playersCursor,PlayerModel.class);
            ret.put(tempPlayer.getName(),tempPlayer);
        }while (playersCursor.moveToNext());
        return ret;
    }


    private Object getModelFromCursor(Cursor cursor, Class modelClass) {
        Object ret = null;
        if (modelClass.getSimpleName().equals(PlayerModel.class.getSimpleName())){
            Log.d(LOG_TAG,"Detecting player");
            long id = cursor.getLong(
                    cursor.getColumnIndex(DataContract.PlayerEntry.COLUMN_ID));
            String name = cursor.getString(
                    cursor.getColumnIndex(DataContract.PlayerEntry.COLUMN_PLAYER_NAME));
            boolean online = cursor.getInt(
                    cursor.getColumnIndex(DataContract.PlayerEntry.COLUMN_IS_ONLINE)) == 1;
            ret = new PlayerModel(id,name,online);
        }else if(modelClass.getSimpleName().equals(GameModel.class.getSimpleName())){
            Log.d(LOG_TAG,"Detecting game");

            long id = cursor.getLong(
                    cursor.getColumnIndex(DataContract.GameEntry.COLUMN_ID));
            int type = cursor.getInt(
                    cursor.getColumnIndex(DataContract.GameEntry.COLUMN_TYPE));
            boolean online = cursor.getInt(
                    cursor.getColumnIndex(DataContract.GameEntry.COLUMN_IS_ONLINE)) == 1;
            String player1 = cursor.getString(
                    cursor.getColumnIndex(DataContract.GameEntry.COLUMN_PLAYER1_NAME));
            String player2 = cursor.getString(
                    cursor.getColumnIndex(DataContract.GameEntry.COLUMN_PLAYER2_NAME));
            int sPlayer1 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.GameEntry.COLUMN_PLAYER1_SCORE));
            int sPlayer2 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.GameEntry.COLUMN_PLAYER2_SCORE));
            String date = cursor.getString(
                    cursor.getColumnIndex(DataContract.GameEntry.COLUMN_DATE));
           ret = new GameModel(id,type,online,player1,player2,sPlayer1,sPlayer2,date);
        }else if(modelClass.getSimpleName().equals(CountModel.class.getSimpleName())){
            long id = cursor.getLong(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_ID));
            String name = cursor.getString(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_PLAYER_NAME));
            int pointLost6 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_6PL));
            int pointLost11 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_11PL));
            int pointLost21 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_21PL));
            int pointScored6 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_6PS));
            int pointScored11 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_11PS));
            int pointScored21 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_21PS));
            int gameWon6 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_6GW));
            int gameWon11 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_11GW));
            int gameWon21 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_21GW));
            int gameLost6 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_6GL));
            int gameLost11 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_11GL));
            int gameLost21 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_11GL));
            int gamePlayed6 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_6GP));
            int gamePlayed11 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_11GP));
            int gamePlayed21 = cursor.getInt(
                    cursor.getColumnIndex(DataContract.CountEntry.COLUMN_21GP));
            ret = new CountModel(id, name,pointLost6,pointLost11,pointLost21,pointScored6,pointScored11,pointScored21,gameWon6,gameWon11,gameWon21,gameLost6,gameLost11,gameLost21,gamePlayed6,gamePlayed11,gamePlayed21);
        }
        return ret;
    }

    private ContentValues createPlayerValuesFromModel(PlayerModel player) {
        ContentValues playerValues = new ContentValues();
        playerValues.put(DataContract.PlayerEntry.COLUMN_PLAYER_NAME,player.getName());
        playerValues.put(DataContract.PlayerEntry.COLUMN_IS_ONLINE,player.isOnline());
        if(player.getId()>=0)
            playerValues.put(DataContract.PlayerEntry.COLUMN_ID,player.getId());
        return playerValues;
    }
    private ContentValues createGameValuesFromModel(GameModel game) {
        ContentValues gameValues = new ContentValues();
        if(game.getId()>=0)
            gameValues.put(DataContract.GameEntry.COLUMN_ID,game.getId());
        gameValues.put(DataContract.GameEntry.COLUMN_IS_ONLINE,game.isOnline());
        gameValues.put(DataContract.GameEntry.COLUMN_TYPE,game.getType());
        gameValues.put(DataContract.GameEntry.COLUMN_DATE,game.getDate());
        gameValues.put(DataContract.GameEntry.COLUMN_PLAYER1_NAME,game.getPlayer1());
        gameValues.put(DataContract.GameEntry.COLUMN_PLAYER2_NAME,game.getPlayer2());
        gameValues.put(DataContract.GameEntry.COLUMN_PLAYER1_SCORE,game.getScorePlayer1());
        gameValues.put(DataContract.GameEntry.COLUMN_PLAYER2_SCORE,game.getScorePlayer2());
        return gameValues;
    }
    private ContentValues createCountValuesFromModel(CountModel count){
        ContentValues countValues = new ContentValues();
        if(count.getId()>=0)
            countValues.put(DataContract.CountEntry.COLUMN_ID,count.getId());
        countValues.put(DataContract.CountEntry.COLUMN_PLAYER_NAME,count.getName());
        countValues.put(DataContract.CountEntry.COLUMN_6GL,count.getGameLost6());
        countValues.put(DataContract.CountEntry.COLUMN_6GP,count.getGamePlayed6());
        countValues.put(DataContract.CountEntry.COLUMN_6GW,count.getGameWon6());
        countValues.put(DataContract.CountEntry.COLUMN_6PS,count.getPointScored6());
        countValues.put(DataContract.CountEntry.COLUMN_6PL,count.getPointLost6());
        countValues.put(DataContract.CountEntry.COLUMN_11GL,count.getGameLost11());
        countValues.put(DataContract.CountEntry.COLUMN_11GP,count.getGamePlayed11());
        countValues.put(DataContract.CountEntry.COLUMN_11GW,count.getGameWon11());
        countValues.put(DataContract.CountEntry.COLUMN_11PS,count.getPointScored11());
        countValues.put(DataContract.CountEntry.COLUMN_11PL,count.getPointLost11());
        countValues.put(DataContract.CountEntry.COLUMN_21GL,count.getGameLost21());
        countValues.put(DataContract.CountEntry.COLUMN_21GP,count.getGamePlayed21());
        countValues.put(DataContract.CountEntry.COLUMN_21GW,count.getGameWon21());
        countValues.put(DataContract.CountEntry.COLUMN_21PS,count.getPointScored21());
        countValues.put(DataContract.CountEntry.COLUMN_21PL,count.getPointLost21());
        return countValues;
    }

    private ContentValues[] createSeveralPlayersValuesFromModel(ArrayList<PlayerModel> players, boolean isOnline) {
        ContentValues[] playersValues = new ContentValues[players.size()];
        for(int i = 0;i<players.size();i++){
            playersValues[i] = createPlayerValuesFromModel(players.get(i));
        }
        return playersValues;
    }
    private ContentValues[] createSeveralGamesValuesFromModel(ArrayList<GameModel> games, boolean isOnline){
        ContentValues[] gamesValues = new ContentValues[games.size()];
        for(int i = 0; i < games.size(); i++){
            gamesValues[i] = createGameValuesFromModel(games.get(i));
        }
        return gamesValues;
    }


    private void createCountDB(){
        HashSet<String> playerWithoutCount = new HashSet<String>();
        for(String player : players.keySet()){
            if(!counts.containsKey(player)){
                playerWithoutCount.add(player);
                initCountInArrayFor(player);
            }
        }
        for(GameModel game : games){
            String player1 = game.getPlayer1();
            String player2 = game.getPlayer2();
            if(playerWithoutCount.contains(player1)||playerWithoutCount.contains(player2)){
                int scorePlayer1 = game.getScorePlayer1();
                int scorePlayer2 = game.getScorePlayer2();
                int type = game.getType();
                if(playerWithoutCount.contains(player1)){
                    counts.get(player1).addGame(type,scorePlayer1,scorePlayer2);
                }
                if(playerWithoutCount.contains(player2)){
                    counts.get(player2).addGame(type,scorePlayer2,scorePlayer1);
                }
            }
        }
        addArrayCountInDB(playerWithoutCount);
    }
    private void initCountInArrayFor(String name){
        CountModel newCount = new CountModel(name);
        counts.put(name,newCount);
    }
    private void addArrayCountInDB(HashSet<String> players){
        for(String player : players){
            addCountInDB(counts.get(player));
        }
    }
}
