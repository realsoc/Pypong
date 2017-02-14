package com.realsoc.pipong.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.realsoc.pipong.PlayerListFragment;
import com.realsoc.pipong.R;
import com.realsoc.pipong.data.DataContract.CountEntry;
import com.realsoc.pipong.data.DataContract.GameEntry;
import com.realsoc.pipong.data.DataContract.PlayerEntry;
import com.realsoc.pipong.model.CountModel;
import com.realsoc.pipong.model.GameModel;
import com.realsoc.pipong.model.PlayerModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.realsoc.pipong.data.DataContract.GameEntry.COLUMN_PLAYER1_NAME;
import static com.realsoc.pipong.data.DataContract.GameEntry.COLUMN_USER;

/**
 * Created by Hugo on 23/01/2017.
 */

public class DataUtils extends ContextWrapper {

    private SharedPreferences sharedPreferences;
    private static final String LOG_TAG = "DataUtils";
    private static DataUtils mDataUtils = null;
    private final static Object singletonLock = new Object();
    private final static Object playersLock = new Object();
    private final static Object playersInConflictLock = new Object();
    private final static Object playerSALLock = new Object();
    private final static Object countLock = new Object();
    private final static Object gamesLock = new Object();

    private volatile HashMap<String,PlayerModel> players = null;
    private volatile HashMap<String,PlayerModel> playersInConflict = null;
    private volatile ArrayList<String> playerAsStringList = null;
    private volatile HashMap<String,CountModel> counts = null;
    private volatile ArrayList<GameModel> games = null;
    private PlayerListFragment playerFragment;
    private String hash;

    private DataUtils(Context context){
        super(context);
        Log.d(LOG_TAG,"CONSTRUCTOR");
        sharedPreferences= getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        initPlayers();
        initPlayersInConflict();
        initGames();
        initCounts();
        if(counts.size() == 0){
            Log.d(LOG_TAG,"Count empty");
        }else if(counts.size() != players.size())
            Log.d(LOG_TAG,"BAD : Players & Counts have different size");
    }
    public static DataUtils reset(Context context){
        if(mDataUtils != null) {
            synchronized (singletonLock) {
                if (mDataUtils != null) {
                    mDataUtils = null;
                    mDataUtils = new DataUtils(context);
                }
            }
        }
        return mDataUtils;
    }


    private void addPlayerToPlayers(PlayerModel player){
        synchronized (playersLock){
            players.put(player.getName(),player);
        }
    }
    private PlayerModel removePlayerFromPlayers(String name){
        synchronized (playersLock){
            return players.remove(name);
        }
    }
    private void addCountToCounts(CountModel count){
        synchronized (countLock){
            counts.put(count.getName(),count);
        }
    }
    private CountModel removeCountFromCounts(String name){
        synchronized (countLock){
            return counts.remove(name);
        }
    }
    private boolean removeGameFromGames(GameModel game){
        synchronized (gamesLock){
            return games.remove(game);
        }
    }
    private void addGameToGames(GameModel game){
        synchronized (gamesLock){
            games.add(game);
        }
    }
    private boolean gamesContains(GameModel game){
        synchronized (gamesLock){
            return games.contains(game);
        }
    }
   private boolean removePlayerFromPlayerSAL(String name){
        synchronized (playerSALLock){
            return playerAsStringList.remove(name);
        }
    }
    private void addPlayerToPLayerSAL(String name){
        synchronized (playerSALLock){
            playerAsStringList.add(name);
        }
    }
    private boolean playerSALContains(String name){
        synchronized (playerSALLock){
            return playerAsStringList.contains(name);
        }
    }
    private boolean countsContains(String name){
        synchronized (countLock){
            return counts.containsKey(name);
        }
    }
    private boolean playersContains(String name){
        synchronized (playersLock){
            return players.containsKey(name);
        }
    }
    public static DataUtils getInstance(Context context){
        if(mDataUtils == null) {
            synchronized (singletonLock) {
                if (mDataUtils == null) {
                    mDataUtils = new DataUtils(context);
                }
            }
        }
        return mDataUtils;
    }

    public void addPlayer(PlayerModel player){
        if(playersContains(player.getName())){
            Log.d(LOG_TAG,"Player already existing : "+player.getName());
        }else{
            Log.d(LOG_TAG,"adding player "+player.getName());
            Uri uri = addPlayerInDB(player);
            player.setId(ContentUris.parseId(uri));
            addPlayerToPlayers(player);
            addPlayerToPLayerSAL(player.getName());
            CountModel newCount = new CountModel(player.getName());
            addCount(newCount);
        }
    }
    public void addOnlinePlayers(HashMap<String,PlayerModel> players){
        Log.d(LOG_TAG,"addingonlineplayers");
        Iterator<Map.Entry<String,PlayerModel>> it = players.entrySet().iterator();
        while (it.hasNext()){
            /*Map.Entry<String,PlayerModel> pair = it.next();
            Log.d(LOG_TAG,pair.getValue().getName());*/

            PlayerModel tempPlayer = it.next().getValue();
            tempPlayer.setOnline(true);
            addPlayer(tempPlayer);
        }
    }
    public void addOnlineGames(ArrayList<GameModel> games){
        for(GameModel game : games){
            game.setOnline(true);
            Log.d(LOG_TAG,"player2name :"+game.getPlayer2());
            Log.d(LOG_TAG,"player2 :"+players.get(game.getPlayer2()));
            Log.d(LOG_TAG,"player1name :"+game.getPlayer1());
            Log.d(LOG_TAG,"player1 :"+players.get(game.getPlayer1()));
            game.setPlayer1_id(players.get(game.getPlayer1()).getId());
            game.setPlayer2_id(players.get(game.getPlayer2()).getId());
            addGame(game);
        }
    }
    private void updatePlayer(PlayerModel player){
        if(players.containsKey(player.getName())){
            Log.d(LOG_TAG,"Player already existing");
        }else{
            String defaultUser = sharedPreferences.getString(getString(R.string.HASH),"1");
            if(player.getUser().equals("0")){
                player.setUser(defaultUser);
            }
            ContentValues playerValues = createPlayerValuesFromModel(player);
            updatePlayer(player.getId(),playerValues);
            synchronized (playersLock){
                if(!playersContains(player.getName()))
                    addPlayerToPlayers(player);
                if(!playerSALContains(player.getName()))
                    addPlayerToPLayerSAL(player.getName());
            }
        }
    }

    public void addGame(GameModel game){
        Uri uri = addGameInDB(game);
        game.setId(ContentUris.parseId(uri));
        addGameToGames(game);
        updateCounts(game);
    }
    public HashMap<String,PlayerModel> getPlayers(){
        synchronized (playersLock){
            return players;
        }
    }
    public ArrayList<GameModel> getGames(){
        synchronized (gamesLock){
            return games;
        }
    }

    public ArrayList<String> getPlayerAsStringList(){
        if(playerAsStringList != null)
            return playerAsStringList;
        else
            return getPlayersSALFromDB(null, null, null, PlayerEntry.COLUMN_PLAYER_NAME+" ASC");
    }
    public HashMap<String,CountModel> getCounts(){
        synchronized (countLock){
            return counts;
        }
    }

    public boolean isInit() {
        return (players != null) && (games != null) && (counts != null);
    }

    public int dropOfflinePlayers(){
        final String offlineString = PlayerEntry.COLUMN_IS_ONLINE + " = 0";
        return getContentResolver().delete(PlayerEntry.CONTENT_URI,offlineString,null);
    }
    public int dropOfflineGames(){
        final String offlineString = GameEntry.COLUMN_IS_ONLINE + " = 0";
        return getContentResolver().delete(GameEntry.CONTENT_URI,offlineString,null);
    }
    public int dropCounts(){
        return getContentResolver().delete(CountEntry.CONTENT_URI,null,null);
    }
    public int dropGames(){
        return getContentResolver().delete(GameEntry.CONTENT_URI,null,null);
    }
    public int dropPlayers(){
        return getContentResolver().delete(PlayerEntry.CONTENT_URI,null,null);
    }

    public PlayerModel getPlayerById(long id, ArrayList<PlayerModel> players){
        for(PlayerModel player : players){
            if(player.getId() == id){
                return player;
            }
        }
        return null;
    }
    public void modifyPlayer(String oldName,String newName){
        PlayerModel player = removePlayerFromPlayers(oldName);
        removePlayerFromPlayerSAL(oldName);
        addPlayerToPLayerSAL(newName);
        player.setName(newName);
        player.setConflict(false);
        mDataUtils.notInConflict(player.getName());
        player.setOnline(false);
        updatePlayer(player);
        addPlayerToPlayers(player);
        CountModel count = removeCountFromCounts(oldName);
        count.setName(newName);
        addCountToCounts(count);
        Log.d(LOG_TAG,"Updating player "+player.getName());
        int o =0;
    }

    public void notInConflict(String player) {
        synchronized (playersInConflictLock){
            playersInConflict.remove(player);
        }
    }
    private void inConflict(PlayerModel player){
        synchronized (playersInConflictLock){
            playersInConflict.put(player.getName(),player);
        }
    }

    private void updateCount(CountModel count) {
        addCountToCounts(count);
        ContentValues countValue = createCountValuesFromModel(count);
        updateCount(count.getId(),countValue);
    }

    private void updateCount(long id, ContentValues countValue) {
        getContentResolver().update(CountEntry.CONTENT_URI,countValue, CountEntry.COLUMN_ID+" = ?",new String[]{""+id});
    }

    public HashMap<String,PlayerModel> getOfflinePlayersFromDB(){
        //Uri uri = PlayerEntry.buildPlayerOffline();
        Cursor playerCursor = getContentResolver().query(PlayerEntry.CONTENT_URI,null, PlayerEntry.COLUMN_IS_ONLINE+" = ?",new String[]{"0"},null);
        return createPlayerHashMapFromCursor(playerCursor);
    }
    public ArrayList<GameModel> getOfflineGamesFromDB(){
        Cursor playerCursor = getContentResolver().query(PlayerEntry.CONTENT_URI,null, PlayerEntry.COLUMN_IS_ONLINE+" = ?",new String[]{"0"},null);
        HashMap<String,PlayerModel> playersHM =  createPlayerHashMapFromCursor(playerCursor);
        String[] projection = new String[]{GameEntry.ALIAS+".*",
                PlayerEntry.TABLE_NAME_ALIAS_1+"."+ PlayerEntry.COLUMN_PLAYER_NAME+" AS "+ COLUMN_PLAYER1_NAME,
                PlayerEntry.TABLE_NAME_ALIAS_2+"."+ PlayerEntry.COLUMN_PLAYER_NAME+" AS "+ GameEntry.COLUMN_PLAYER2_NAME};
        String selection = GameEntry.ALIAS+"."+GameEntry.COLUMN_IS_ONLINE+" = ?";
        String[] selectionArgs = new String[]{"0"};
        Cursor gameCursor = getContentResolver().query(GameEntry.CONTENT_URI,projection, selection,selectionArgs,null);
        String[] columnNames = gameCursor.getColumnNames();
        Log.d(LOG_TAG,"COUNT "+gameCursor.getCount());
        for(String name : columnNames){
            Log.d(LOG_TAG,"et "+name);
        }
        ArrayList<GameModel> gamesAL =  createGameArrayListFromCursor(gameCursor);
        return removeOfflinePlayersGames(gamesAL,playersHM.keySet());
    }

    private ArrayList<GameModel> removeOfflinePlayersGames(ArrayList<GameModel> gamesAL, Set<String> playersSet) {
        ArrayList<GameModel> gamesToDel = new ArrayList<>();
        for(GameModel game : gamesAL){
            if(playersSet.contains(game.getPlayer1()) || playersSet.contains(game.getPlayer2())){
                gamesToDel.add(game);
            }
        }
        for(GameModel gameToDel :gamesToDel){
            gamesAL.remove(gameToDel);
        }
        return gamesAL;
    }

    public CountModel getCountByNameFromDB(String name){
        Uri uri = CountEntry.buildCountWithName(name);
        Cursor countCursor = getContentResolver().query(uri,null,null,null,null);
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
    public JSONObject createPostUserJson() throws JSONException {

        String hash = sharedPreferences.getString(getString(R.string.HASH),"");
        JSONObject ret = null;
        if(!hash.equals("")){
            ret = new JSONObject();
            ret.put(getString(R.string.HASH),hash);
        }
        return ret;
    }
    public JSONObject createPostPlayersJson(HashMap<String, PlayerModel> players) throws JSONException {

        JSONArray playerRequest = new JSONArray();
        JSONObject globalRequest = new JSONObject();
        String hash = sharedPreferences.getString(getString(R.string.HASH), "");
        Iterator<Map.Entry<String,PlayerModel>> it = players.entrySet().iterator();

        while(it.hasNext()){
            Map.Entry<String,PlayerModel> pair = it.next();
            playerRequest.put(pair.getValue().toJSON());
        }
        globalRequest.put("hash", hash);
        globalRequest.put("data",playerRequest);
        return globalRequest;
    }
    public JSONObject createPostGamesJson(ArrayList<GameModel> games) throws JSONException {

        JSONObject globalRequest = new JSONObject();
        JSONArray gameRequest = new JSONArray();
        for(GameModel game:games){
            gameRequest.put(game.toJSON());
        }
        globalRequest.put("hash", sharedPreferences.getString(getString(R.string.HASH),""));
        globalRequest.put("data",gameRequest);
        return globalRequest;
    }
    private void updatePlayer(Long id, ContentValues contentValues){
        Log.d(LOG_TAG,"UPDATING PLAYER ");
        getContentResolver().update(PlayerEntry.CONTENT_URI,contentValues, PlayerEntry.COLUMN_ID+" = ?",new String[]{""+id});
    }
    private void updateGame(Long id, ContentValues contentValues){
        getContentResolver().update(GameEntry.CONTENT_URI,contentValues, GameEntry.COLUMN_ID+" = ?",new String[]{""+id});
    }
    public void compareAndPutOnline(HashMap<String,PlayerModel> allPlayers,ArrayList<String> errorPlayers){
        for(String player : errorPlayers){
            allPlayers.remove(player);
        }
        HashMap<Long,ContentValues> playersValues = createSeveralPlayersValuesFromModel(allPlayers,true);
        Iterator<Map.Entry<Long,ContentValues>> it = playersValues.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Long,ContentValues> pair = it.next();
            updatePlayer(pair.getKey(),pair.getValue());
        }
    }
    public void compareAndPutOnline(ArrayList<GameModel> allGames,ArrayList<GameModel> errorGames){
        for(GameModel game : errorGames){
            allGames.remove(game);
        }
        HashMap<Long,ContentValues> gamesValues = createSeveralGamesValuesFromModel(allGames,true);
        Iterator<Map.Entry<Long,ContentValues>> it = gamesValues.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Long,ContentValues> pair = it.next();
            updateGame(pair.getKey(),pair.getValue());
        }
    }
    private void initPlayers(){
        synchronized (playersLock) {
            players = getPlayersFromDB(null, null, null, PlayerEntry.COLUMN_PLAYER_NAME+" ASC");
        }
        if(players == null)
            Log.d(LOG_TAG,"Error getting players");
        else if(players.size() == 0)
            Log.d(LOG_TAG,"Players empty");
        synchronized (playerSALLock){
            playerAsStringList = getPlayersSALFromDB(null, null, null, PlayerEntry.COLUMN_PLAYER_NAME+" ASC");
        }
        if(counts != null && counts.size() != players.size()){
            Log.d(LOG_TAG,"Players & Counts have different size");
            createCountDB();
            synchronized (countLock){
                counts = getCountsFromDB(null,null,null,null);
            }
        }

    }

    private void initPlayersInConflict() {
        synchronized (playersInConflictLock) {
            playersInConflict = getPlayersFromDB(null, PlayerEntry.COLUMN_CONFLICT+" = ?", new String[]{"1"}, PlayerEntry.COLUMN_PLAYER_NAME+" ASC");
        }
    }

    private void initGames(){
        synchronized (gamesLock){
            games = getGamesFromDB(new String[]{
                    GameEntry.ALIAS+".*",
                    PlayerEntry.TABLE_NAME_ALIAS_1+"."+PlayerEntry.COLUMN_PLAYER_NAME+" AS "+GameEntry.COLUMN_PLAYER1_NAME,
                    PlayerEntry.TABLE_NAME_ALIAS_2+"."+PlayerEntry.COLUMN_PLAYER_NAME+" AS "+GameEntry.COLUMN_PLAYER2_NAME
            },null,null,null);
            if(games == null)
                Log.d(LOG_TAG,"Error getting games");
            else if(players.size() == 0)
                Log.d(LOG_TAG,"Games empty");
        }

    }
    private void initCounts(){
        int a = 0;
        synchronized (countLock) {
            counts = getCountsFromDB(
                    new String[]{
                            CountEntry.ALIAS+".*",
                            PlayerEntry.TABLE_NAME+"."+PlayerEntry.COLUMN_PLAYER_NAME
                    }, null, null, null);
            if (counts == null)
                Log.d(LOG_TAG, "Error getting counts");
            else if (players.size() == 0)
                Log.d(LOG_TAG, "Counts empty");
            if (players != null && counts.size() != players.size())
                Log.d(LOG_TAG, "Players & Counts have different size");
        }
    }

    private void addCount(CountModel countModel){
        synchronized (countLock){
        if(counts.containsKey(countModel.getName())){
            Log.d(LOG_TAG,"Count already exists");
        }else{
            Uri mUri = addCountInDB(countModel);
            countModel.setId(ContentUris.parseId(mUri));
                counts.put(countModel.getName(),countModel);
            }
        }
    }
    private Uri addCountInDB(CountModel count){
        Log.d(LOG_TAG,"Adding count in db "+count.getName());
        ContentValues countValues = createCountValuesFromModel(count);
        return getContentResolver().insert(CountEntry.CONTENT_URI,countValues);
    }
    private Uri addPlayerInDB(PlayerModel player){
        ContentValues playerValues = createPlayerValuesFromModel(player);
        return getContentResolver().insert(PlayerEntry.CONTENT_URI,playerValues);
    }
    private Uri addGameInDB(GameModel game){
        ContentValues gameValues = createGameValuesFromModel(game);
        return getContentResolver().insert(GameEntry.CONTENT_URI,gameValues);
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
        synchronized (countLock){
            if(counts.containsKey(player1) && counts.containsKey(player1)){
                counts.get(player1).addGame(type,scorePlayer1,scorePlayer2);
                counts.get(player2).addGame(type,scorePlayer2,scorePlayer1);
            }
            else{
                if(players.containsKey(player1) && players.containsKey(player2)){
                    createCountDB();
                    counts.get(player1).addGame(type,scorePlayer1,scorePlayer2);
                    counts.get(player2).addGame(type,scorePlayer2,scorePlayer1);
                }else{
                    Log.d(LOG_TAG,"One player does not exists 1:"+player1+" 2:"+player2);
                }
            }
        }
        updateCountsInDB(playerSet);
    }
    private void updateCountsInDB(HashSet<String> playerSet){
        for(String player : playerSet){
            CountModel count ;
            synchronized (countLock){
                count = counts.get(player);
            }
            ContentValues value = createCountValuesFromModel(count);
            int ret = getContentResolver().update(
                    CountEntry.CONTENT_URI,
                    value,
                    CountEntry.COLUMN_ID+" = "+count.getId(),
                    null);
            if(ret != 1){
                Log.d(LOG_TAG,"Problem updating count : rowupdated = "+ret);
            }
        }
    }
    private HashMap<String,PlayerModel> getPlayersFromDB(String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor playersCursor = getContentResolver().query(
                PlayerEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
        String[] names = playersCursor.getColumnNames();
        for(int i=0;i<names.length;i++){
            Log.d(LOG_TAG,"Key "+names[i]);
            //Log.d(LOG_TAG,"Val "+playersCursor.getnames[i]);
        }
        return createPlayerHashMapFromCursor(playersCursor);
    }
    private ArrayList<String> getPlayersSALFromDB(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor playersCursor = getContentResolver().query(
                PlayerEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
        return createPlayerSALFromCursor(playersCursor);
    }


    private ArrayList<GameModel> getGamesFromDB(String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor gamesCursor = getContentResolver().query(
                GameEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
        String[] columns = gamesCursor.getColumnNames();
        for(String col:columns){
            Log.d(LOG_TAG,col);
        }
        return createGameArrayListFromCursor(gamesCursor);
    }
    private HashMap<String,CountModel> getCountsFromDB(String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor countCursor = getContentResolver().query(
                CountEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
        Log.d(LOG_TAG,"Countcursor size "+countCursor.getCount());
        String[] columns = countCursor.getColumnNames();
        for(String name:columns){
            Log.d(LOG_TAG,"cou "+name);
        }
        Log.d(LOG_TAG," "+countCursor.getCount());
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
        String defaultUser = sharedPreferences.getString(getString(R.string.HASH),"1");
        if(gamesCursor.getCount()<1)
            return ret;
        gamesCursor.moveToFirst();
        do{
            GameModel tempGame = (GameModel)getModelFromCursor(gamesCursor,GameModel.class);
            if(tempGame.getUser().equals("0")){
                tempGame.setUser(defaultUser);
            }
            ret.add(tempGame);
        }while (gamesCursor.moveToNext());
        return ret;
    }
    private ArrayList<String> createPlayerSALFromCursor(Cursor playersCursor) {
        ArrayList<String> ret = new ArrayList<String>(){
            public boolean add(String name) {
                int index = Collections.binarySearch(this, name,new Comparator<String>() {
                    public int compare(String a, String b) {
                        return a.compareToIgnoreCase(b);
                    }
                });
                if (index < 0) index = ~index;
                super.add(index, name);
                return true;
            }
        };
        if(playersCursor.getCount()<1)
            return ret;
        playersCursor.moveToFirst();
        do{
            ret.add(playersCursor.getString(playersCursor.getColumnIndex(PlayerEntry.COLUMN_PLAYER_NAME)));
        }while (playersCursor.moveToNext());
        return ret;
    }

    private HashMap<String,PlayerModel> createPlayerHashMapFromCursor(Cursor playersCursor) {
        HashMap<String,PlayerModel> ret = new HashMap<>();
        String defaultUser = sharedPreferences.getString(getString(R.string.HASH),"1");
        if(playersCursor.getCount()<1){
            Log.d(LOG_TAG,"PLAYER CURSOR EMPTY");
            return ret;
        }
        playersCursor.moveToFirst();
        do{
            PlayerModel tempPlayer = (PlayerModel) getModelFromCursor(playersCursor,PlayerModel.class);
            if(tempPlayer.getUser().equals("0")){
                tempPlayer.setUser(defaultUser);
            }
            ret.put(tempPlayer.getName(),tempPlayer);
        }while (playersCursor.moveToNext());
        return ret;
    }


    private Object getModelFromCursor(Cursor cursor, Class modelClass) {
        Object ret = null;
        if (modelClass.getSimpleName().equals(PlayerModel.class.getSimpleName())){
            Log.d(LOG_TAG,"Detecting player");
            long id = cursor.getLong(
                    cursor.getColumnIndex(PlayerEntry.COLUMN_ID));
            String name = cursor.getString(
                    cursor.getColumnIndex(PlayerEntry.COLUMN_PLAYER_NAME));
            String user = cursor.getString(
                    cursor.getColumnIndex(PlayerEntry.COLUMN_USER));
            boolean online = cursor.getInt(
                    cursor.getColumnIndex(PlayerEntry.COLUMN_IS_ONLINE)) == 1;
            ret = new PlayerModel(id,name,user,online);
        }else if(modelClass.getSimpleName().equals(GameModel.class.getSimpleName())){
            Log.d(LOG_TAG,"Detecting game");

            long id = cursor.getLong(
                    cursor.getColumnIndex(GameEntry.COLUMN_ID));
            int type = cursor.getInt(
                    cursor.getColumnIndex(GameEntry.COLUMN_TYPE));
            boolean online = cursor.getInt(
                    cursor.getColumnIndex(GameEntry.COLUMN_IS_ONLINE)) == 1;
            long player1Id = cursor.getLong(
                    cursor.getColumnIndex(GameEntry.COLUMN_PLAYER1_ID));
            long player2Id = cursor.getLong(
                    cursor.getColumnIndex(GameEntry.COLUMN_PLAYER2_ID));
            String player1 = cursor.getString(
                    cursor.getColumnIndex(COLUMN_PLAYER1_NAME));
            String player2 = cursor.getString(
                    cursor.getColumnIndex(GameEntry.COLUMN_PLAYER2_NAME));
            String user = cursor .getString(
                    cursor.getColumnIndex(COLUMN_USER));
            int sPlayer1 = cursor.getInt(
                    cursor.getColumnIndex(GameEntry.COLUMN_PLAYER1_SCORE));
            int sPlayer2 = cursor.getInt(
                    cursor.getColumnIndex(GameEntry.COLUMN_PLAYER2_SCORE));
            long date = cursor.getLong(
                    cursor.getColumnIndex(GameEntry.COLUMN_DATE));
           ret = new GameModel(id,type,online,player1Id,player2Id,player1,player2,user,sPlayer1,sPlayer2,date);
        }else if(modelClass.getSimpleName().equals(CountModel.class.getSimpleName())){
            long id = cursor.getLong(
                    cursor.getColumnIndex(CountEntry.COLUMN_ID));
            long playerId = cursor.getLong(
                    cursor.getColumnIndex(CountEntry.COLUMN_PLAYER_ID));
            String name = cursor.getString(
                    cursor.getColumnIndex(CountEntry.COLUMN_PLAYER_NAME));
            int pointLost6 = cursor.getInt(
                    cursor.getColumnIndex(CountEntry.COLUMN_6PL));
            int pointLost11 = cursor.getInt(
                    cursor.getColumnIndex(CountEntry.COLUMN_11PL));
            int pointLost21 = cursor.getInt(
                    cursor.getColumnIndex(CountEntry.COLUMN_21PL));
            int pointScored6 = cursor.getInt(
                    cursor.getColumnIndex(CountEntry.COLUMN_6PS));
            int pointScored11 = cursor.getInt(
                    cursor.getColumnIndex(CountEntry.COLUMN_11PS));
            int pointScored21 = cursor.getInt(
                    cursor.getColumnIndex(CountEntry.COLUMN_21PS));
            int gameWon6 = cursor.getInt(
                    cursor.getColumnIndex(CountEntry.COLUMN_6GW));
            int gameWon11 = cursor.getInt(
                    cursor.getColumnIndex(CountEntry.COLUMN_11GW));
            int gameWon21 = cursor.getInt(
                    cursor.getColumnIndex(CountEntry.COLUMN_21GW));
            int gameLost6 = cursor.getInt(
                    cursor.getColumnIndex(CountEntry.COLUMN_6GL));
            int gameLost11 = cursor.getInt(
                    cursor.getColumnIndex(CountEntry.COLUMN_11GL));
            int gameLost21 = cursor.getInt(
                    cursor.getColumnIndex(CountEntry.COLUMN_11GL));
            int gamePlayed6 = cursor.getInt(
                    cursor.getColumnIndex(CountEntry.COLUMN_6GP));
            int gamePlayed11 = cursor.getInt(
                    cursor.getColumnIndex(CountEntry.COLUMN_11GP));
            int gamePlayed21 = cursor.getInt(
                    cursor.getColumnIndex(CountEntry.COLUMN_21GP));
            ret = new CountModel(id, playerId,name,pointLost6,pointLost11,pointLost21,pointScored6,pointScored11,pointScored21,gameWon6,gameWon11,gameWon21,gameLost6,gameLost11,gameLost21,gamePlayed6,gamePlayed11,gamePlayed21);
        }
        return ret;
    }

    private ContentValues createPlayerValuesFromModel(PlayerModel player) {
        ContentValues playerValues = new ContentValues();
        String user = player.getUser();
        if(user.equals("0")){
            user = sharedPreferences.getString(getString(R.string.HASH),"1");
        }
        Log.d(LOG_TAG,"UPDATING "+player.getName());
        playerValues.put(PlayerEntry.COLUMN_PLAYER_NAME,player.getName());
        playerValues.put(PlayerEntry.COLUMN_IS_ONLINE,player.isOnline());
        playerValues.put(PlayerEntry.COLUMN_USER,user);
        if(player.getId()>=0)
            playerValues.put(PlayerEntry.COLUMN_ID,player.getId());
        return playerValues;
    }
    private ContentValues createGameValuesFromModel(GameModel game) {
        ContentValues gameValues = new ContentValues();
        long player1_id = game.getPlayer1_id();
        long player2_id = game.getPlayer2_id();
        String user = game.getUser();
        if(user.equals("0")){
            user = sharedPreferences.getString(getString(R.string.HASH),"1");
        }
        if(player1_id <0){
            player1_id = players.get(game.getPlayer1()).getId();
        }
        if(player2_id<0){
            player2_id = players.get(game.getPlayer2()).getId();
        }
        if(game.getId()>=0)
            gameValues.put(GameEntry.COLUMN_ID,game.getId());
        gameValues.put(GameEntry.COLUMN_IS_ONLINE,game.isOnline()?1:0);
        gameValues.put(GameEntry.COLUMN_TYPE,game.getType());
        gameValues.put(GameEntry.COLUMN_DATE,game.getDate());
        gameValues.put(GameEntry.COLUMN_PLAYER1_ID,player1_id);
        gameValues.put(GameEntry.COLUMN_PLAYER2_ID,player2_id);
        gameValues.put(GameEntry.COLUMN_USER,user);
        //gameValues.put(GameEntry.COLUMN_PLAYER1_NAME,game.getPlayer1());
        //gameValues.put(GameEntry.COLUMN_PLAYER2_NAME,game.getPlayer2());
        gameValues.put(GameEntry.COLUMN_PLAYER1_SCORE,game.getScorePlayer1());
        gameValues.put(GameEntry.COLUMN_PLAYER2_SCORE,game.getScorePlayer2());
        return gameValues;
    }
    private ContentValues createCountValuesFromModel(CountModel count){
        ContentValues countValues = new ContentValues();
        long player_id = count.getPlayer_id();
        if(player_id <0){
            player_id = players.get(count.getName()).getId();
        }
        if(count.getId()>=0)
            countValues.put(CountEntry.COLUMN_ID,count.getId());
        countValues.put(CountEntry.COLUMN_PLAYER_ID,player_id);
        countValues.put(CountEntry.COLUMN_6GL,count.getGameLost6());
        countValues.put(CountEntry.COLUMN_6GP,count.getGamePlayed6());
        countValues.put(CountEntry.COLUMN_6GW,count.getGameWon6());
        countValues.put(CountEntry.COLUMN_6PS,count.getPointScored6());
        countValues.put(CountEntry.COLUMN_6PL,count.getPointLost6());
        countValues.put(CountEntry.COLUMN_11GL,count.getGameLost11());
        countValues.put(CountEntry.COLUMN_11GP,count.getGamePlayed11());
        countValues.put(CountEntry.COLUMN_11GW,count.getGameWon11());
        countValues.put(CountEntry.COLUMN_11PS,count.getPointScored11());
        countValues.put(CountEntry.COLUMN_11PL,count.getPointLost11());
        countValues.put(CountEntry.COLUMN_21GL,count.getGameLost21());
        countValues.put(CountEntry.COLUMN_21GP,count.getGamePlayed21());
        countValues.put(CountEntry.COLUMN_21GW,count.getGameWon21());
        countValues.put(CountEntry.COLUMN_21PS,count.getPointScored21());
        countValues.put(CountEntry.COLUMN_21PL,count.getPointLost21());
        return countValues;
    }
    private ContentValues createPlayerValuesFromModel(PlayerModel player,boolean online) {
        ContentValues playerValues = new ContentValues();
        String hash = player.getUser();
        if(hash.equals("0")){
            hash = sharedPreferences.getString(getString(R.string.HASH),"1");
        }
        playerValues.put(PlayerEntry.COLUMN_PLAYER_NAME,player.getName());
        playerValues.put(PlayerEntry.COLUMN_USER,hash);
        playerValues.put(PlayerEntry.COLUMN_IS_ONLINE,online);
        if(player.getId()>=0)
            playerValues.put(PlayerEntry.COLUMN_ID,player.getId());
        return playerValues;
    }
    private ContentValues createGameValuesFromModel(GameModel game,boolean online) {
        ContentValues gameValues = new ContentValues();
        String user = game.getUser();
        if(user.equals("0")){
            user = sharedPreferences.getString(getString(R.string.HASH),"1");
        }
        long player1_id = game.getPlayer1_id();
        long player2_id = game.getPlayer2_id();
        if(player1_id <0){
            player1_id = players.get(game.getPlayer1()).getId();
        }
        if(player2_id<0){
            player2_id = players.get(game.getPlayer2()).getId();
        }
        if(game.getId()>=0)
            gameValues.put(GameEntry.COLUMN_ID,game.getId());
        gameValues.put(GameEntry.COLUMN_IS_ONLINE,online);
        gameValues.put(GameEntry.COLUMN_TYPE,game.getType());
        gameValues.put(GameEntry.COLUMN_DATE,game.getDate());
        gameValues.put(GameEntry.COLUMN_PLAYER1_ID,player1_id);
        gameValues.put(GameEntry.COLUMN_PLAYER2_ID,player2_id);
        gameValues.put(GameEntry.COLUMN_USER,user);
        //gameValues.put(GameEntry.COLUMN_PLAYER1_NAME,game.getPlayer1());
        //gameValues.put(GameEntry.COLUMN_PLAYER2_NAME,game.getPlayer2());
        gameValues.put(GameEntry.COLUMN_PLAYER1_SCORE,game.getScorePlayer1());
        gameValues.put(GameEntry.COLUMN_PLAYER2_SCORE,game.getScorePlayer2());
        return gameValues;
    }
    private HashMap<Long,ContentValues> createSeveralPlayersValuesFromModel(HashMap<String,PlayerModel> players,boolean online) {
        HashMap<Long,ContentValues> playersValues = new HashMap<>();
        Iterator<Map.Entry<String,PlayerModel>> it = players.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String,PlayerModel> pair = it.next();
            playersValues.put(pair.getValue().getId(),createPlayerValuesFromModel(pair.getValue(),online));
        }
        return playersValues;
    }
    private HashMap<Long,ContentValues> createSeveralGamesValuesFromModel(ArrayList<GameModel> games,boolean online){
        HashMap<Long,ContentValues> gamesValues = new HashMap<>();
        for(int i = 0; i < games.size(); i++){
            gamesValues.put(games.get(i).getId(),createGameValuesFromModel(games.get(i),online));
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

    public static ArrayList<String> JsonToPlayersNameArrayList(JSONArray response) throws JSONException {
        ArrayList<String> playerNotPersist = new ArrayList<>();
        for(int i =0;i<response.length();i++){
            PlayerModel play = new PlayerModel(response.getJSONObject(i));
            playerNotPersist.add(play.getName());
        }
        return playerNotPersist;
    }
    public static ArrayList<GameModel> JsonToGamesArrayList(JSONArray response) throws JSONException {
        ArrayList<GameModel> gamesNotPersist = new ArrayList<>();
        for(int i =0;i<response.length();i++){
            GameModel game = new GameModel(response.getJSONObject(i));
            gamesNotPersist.add(game);
        }
        return gamesNotPersist;
    }

    public void playersInConflict(ArrayList<String> playersNotPersists) {
        synchronized (playersLock){
            for(String play : playersNotPersists){
                PlayerModel player = players.get(play);
                if(player !=null){
                    player.setConflict(true);
                    inConflict(player);
                    updatePlayer(player);
                }
            }
            if(playerFragment != null){
                playerFragment.adviseDataChanged();
            }
        }
    }
    public boolean hasConflict(){
        return playersInConflict.size()!=0;
    }
    public HashMap<String,PlayerModel> getPersistPlayersFromJSON(JSONArray playersArray) throws JSONException {
        HashMap<String,PlayerModel> ret = new HashMap<>();
        PlayerModel tempPlayer;
        for(int i =0;i<playersArray.length();i++){
            tempPlayer = new PlayerModel(playersArray.getJSONObject(i));
            ret.put(tempPlayer.getName(),tempPlayer);
        }
        return ret;
    }
    public ArrayList<GameModel> getPersistGamesFromJSON(JSONArray gamesArray) throws JSONException {
        ArrayList<GameModel> ret = new ArrayList<>();
        for(int i =0;i<gamesArray.length();i++){
            ret.add(new GameModel(gamesArray.getJSONObject(i)));
        }
        return ret;
    }

    public void unregisterPlayerFragment() {
        playerFragment = null;
    }

    public void registerPlayerFragment(PlayerListFragment playerListFragment) {
        playerFragment = playerListFragment;
    }
    public void test(String who){
       // Log.d(LOG_TAG,who+" SAL SIZE "+playerAsStringList.size()+" id : "+System.identityHashCode(playerAsStringList));
        Log.d(LOG_TAG,who+" PHM SIZE "+players.size()+" id : "+System.identityHashCode(players));
        Log.d(LOG_TAG,who+" DataUtils id : "+System.identityHashCode(this));
    }

    public boolean containsPlayer(String newName) {
        return players.containsKey(newName);
    }

    public void removePlayer(String name) {
       removePlayerFromPlayerSAL(name);
        PlayerModel player = removePlayerFromPlayers(name);
        CountModel count = removeCountFromCounts(name);
        removePlayerInDB(player);
        removeCountInDB(count);
    }

    private void removeCountInDB(CountModel count) {
        if(count.getId()>=0){
            long id = count.getId();
            getContentResolver().delete(CountEntry.CONTENT_URI,CountEntry.COLUMN_ID+" = ?",new String[]{""+id});
        }else{
            getContentResolver().delete(CountEntry.CONTENT_URI,CountEntry.COLUMN_PLAYER_NAME+" = ?",new String[]{count.getName()});
        }
    }

    private void removePlayerInDB(PlayerModel player) {
        if(player.getId()>=0){
            long id = player.getId();
            getContentResolver().delete(PlayerEntry.CONTENT_URI,PlayerEntry.COLUMN_ID+" = ?",new String[]{""+id});
        }else{
            getContentResolver().delete(PlayerEntry.CONTENT_URI,PlayerEntry.COLUMN_PLAYER_NAME+" = ?", new String[]{player.getName()});
        }
    }
    private void turnPlayersOffline(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlayerEntry.COLUMN_IS_ONLINE,false);
        getContentResolver().update(PlayerEntry.CONTENT_URI,contentValues,null,null);
    }
    private void turnGamesffline(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(GameEntry.COLUMN_IS_ONLINE,false);
        getContentResolver().update(GameEntry.CONTENT_URI,contentValues,null,null);
    }

    public void turnEverythingOffline() {
        turnGamesffline();
        turnPlayersOffline();
    }
}
