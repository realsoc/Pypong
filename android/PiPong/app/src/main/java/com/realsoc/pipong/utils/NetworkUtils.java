package com.realsoc.pipong.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.realsoc.pipong.JsonArrayRequestPlus;
import com.realsoc.pipong.PlayersActivity;
import com.realsoc.pipong.R;
import com.realsoc.pipong.model.GameModel;
import com.realsoc.pipong.model.PlayerModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hugo on 23/01/2017.
 */

public class NetworkUtils extends ContextWrapper{

    private static final String LOG_TAG = "NetworkUtils";
    private RequestQueue queue;
    private SharedPreferences sharedPreferences;
    boolean isOnline;
    boolean initialized;
    public static final String ADDRESS_REGEX = "([\\da-z\\.-]+)\\.([a-z\\.]{2,6})";
    boolean hasConflict= false;
    private String hash;
    private DataUtils dataUtils;
    private static NetworkUtils instance = null;


    public static NetworkUtils getInstance(Context mContext){
        if(instance == null){
            instance = new NetworkUtils(mContext);
        }
        return instance;
    }
    private NetworkUtils(Context context){
        super(context);
        queue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        dataUtils = DataUtils.getInstance(this);
        String baseServerIp = sharedPreferences.getString(getString(R.string.SERVER_IP),"0.0.0.0");
    }
    public void autokill(){
        instance = null;
    }
    public void getAll(){
        Log.d(LOG_TAG,"GETALL BORDER");
        Log.d(LOG_TAG,"conflict ?"+dataUtils.hasConflict());
        String baseServerIp = sharedPreferences.getString(getString(R.string.SERVER_IP),"0.0.0.0");
        if(Patterns.IP_ADDRESS.matcher(baseServerIp).matches()) {
            String serverIp = "http://" + baseServerIp + ":3000";
            isOnline = sharedPreferences.getBoolean(getString(R.string.IS_ONLINE), false);
            initialized = sharedPreferences.getBoolean(getString(R.string.HAS_SUBSCRIBED), false);
            hash = sharedPreferences.getString(getString(R.string.HASH), "");
            Log.d(LOG_TAG, "GETALL");
            if (isOnline && !dataUtils.hasConflict()) {
                if (hash.equals("")) {
                    sharedPreferences.edit().putBoolean(getString(R.string.preference_file_key), false).apply();
                    Log.d(LOG_TAG, "getAll hash null");

                } else if (!initialized) {
                    Log.d(LOG_TAG, "getAll not initialized");
                } else {
                    JSONObject too = new JSONObject();
                    hash = sharedPreferences.getString(getString(R.string.HASH), "");
                    if (hash.equals(""))
                        return;
                    try {
                        too.put(getString(R.string.HASH), hash);
                        too.put(getString(R.string.TIME_START), sharedPreferences.getLong(getString(R.string.LAST_UPDATE), 0));

                        JsonObjectRequest arrayRequest = new JsonObjectRequest
                                (Request.Method.POST, serverIp + "/getAllFrom", too, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //TODO GET ALL
                                        try {
                                            Log.d(LOG_TAG, "GetAll response");
                                            Log.d(LOG_TAG, "GETALL");
                                            JSONArray gamesArray = response.getJSONArray("games");
                                            JSONArray playersArray = response.getJSONArray("players");
                                            long timestamp = response.getLong("timestamp");
                                            sharedPreferences.edit().putLong(getString(R.string.LAST_UPDATE), timestamp).apply();
                                            ArrayList<GameModel> gamesToPersist = dataUtils.getPersistGamesFromJSON(gamesArray);
                                            HashMap<String, PlayerModel> playersToPersist = dataUtils.getPersistPlayersFromJSON(playersArray);
                                            dataUtils.addOnlinePlayers(playersToPersist);
                                            dataUtils.addOnlineGames(gamesToPersist);
                                            Intent newIntent = new Intent();
                                            newIntent.setAction(PlayersActivity.UPDATE_VIEW);
                                            sendBroadcast(newIntent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        NetworkResponse networkResponse = error.networkResponse;
                                        if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                                            Log.d(LOG_TAG, "Error 404 in getAll");
                                        } else if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                                            Log.d(LOG_TAG, "Error 400 in getAll");
                                        } else if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                                            Log.d(LOG_TAG, "Error 500 in getAll");
                                        }
                                        error.printStackTrace();
                                    }
                                });
                        queue.add(arrayRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return;
    }

    public void subscribe()  {
        String baseServerIp = sharedPreferences.getString(getString(R.string.SERVER_IP),"0.0.0.0");
        if(Patterns.IP_ADDRESS.matcher(baseServerIp).matches()) {
            String serverIp = "http://" + baseServerIp + ":3000";
            isOnline = sharedPreferences.getBoolean(getString(R.string.IS_ONLINE), false);
            initialized = sharedPreferences.getBoolean(getString(R.string.HAS_SUBSCRIBED), false);
            hash = sharedPreferences.getString(getString(R.string.HASH), "");
            if (isOnline) {
                if (hash.equals("")) {
                    sharedPreferences.edit().putBoolean(getString(R.string.HAS_SUBSCRIBED), false).apply();
                } else if (!initialized) {
                    JSONObject req = new JSONObject();
                    try {
                        req.put(getString(R.string.HASH), sharedPreferences.getString(getString(R.string.HASH), ""));
                        JsonObjectRequest objectRequest = new JsonObjectRequest
                                (Request.Method.POST, serverIp + "/subscribe", req, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        sharedPreferences.edit().putBoolean(getString(R.string.HAS_SUBSCRIBED), true).apply();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        NetworkResponse networkResponse = error.networkResponse;
                                        if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                                            Log.d(LOG_TAG, "Error 500 in subscribe");
                                        }
                                        error.printStackTrace();
                                    }
                                });
                        queue.add(objectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void backgroundThreadShortToast(final Context mContext,
                                                  final String msg) {
        if (mContext != null && msg != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void postOfflinePlayers(){
        String baseServerIp = sharedPreferences.getString(getString(R.string.SERVER_IP),"0.0.0.0");
        if(Patterns.IP_ADDRESS.matcher(baseServerIp).matches()) {
            String serverIp = "http://" + baseServerIp + ":3000";
            isOnline = sharedPreferences.getBoolean(getString(R.string.IS_ONLINE), false);
            initialized = sharedPreferences.getBoolean(getString(R.string.HAS_SUBSCRIBED), false);
            hash = sharedPreferences.getString(getString(R.string.HASH), "");
            if (isOnline) {
                if (hash.equals("")) {
                    sharedPreferences.edit().putBoolean(getString(R.string.HAS_SUBSCRIBED), false).apply();
                    Log.d(LOG_TAG, "postOfflinePlayers hash null");
                } else if (!initialized) {
                    Log.d(LOG_TAG, "postOfflinePlayers not suscribed");
                } else {
                    hash = sharedPreferences.getString(getString(R.string.HASH), "");
                    try {
                        final HashMap<String, PlayerModel> offlinePlayers = dataUtils.getOfflinePlayersFromDB();
                        JSONObject playersToSend = dataUtils.createPostPlayersJson(offlinePlayers);
                        JsonArrayRequestPlus arrayRequest = new JsonArrayRequestPlus
                                (Request.Method.POST, serverIp + "/players", playersToSend, new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        try {
                                            Log.d(LOG_TAG, response.toString());
                                            ArrayList<String> playersNotPersists = DataUtils.JsonToPlayersNameArrayList(response);
                                            HashMap<String, PlayerModel> allOfflinePlayers = new HashMap<>(offlinePlayers);
                                            dataUtils.compareAndPutOnline(allOfflinePlayers, playersNotPersists);
                                            if (playersNotPersists.size() > 0) {
                                                dataUtils.playersInConflict(playersNotPersists);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        postOfflineGames();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        NetworkResponse networkResponse = error.networkResponse;
                                        if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                                            Log.d(LOG_TAG, "Error 404 in postPlayers");
                                        } else if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                                            Log.d(LOG_TAG, "Error 400 in postPlayers");
                                        } else if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                                            Log.d(LOG_TAG, "Error 500 in postPlayers");
                                        }
                                        error.printStackTrace();
                                    }
                                });
                        queue.add(arrayRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void postOfflineGames(){
        String baseServerIp = sharedPreferences.getString(getString(R.string.SERVER_IP),"0.0.0.0");
        if(Patterns.IP_ADDRESS.matcher(baseServerIp).matches()) {
            String serverIp = "http://" + baseServerIp + ":3000";
            isOnline = sharedPreferences.getBoolean(getString(R.string.IS_ONLINE), false);
            initialized = sharedPreferences.getBoolean(getString(R.string.HAS_SUBSCRIBED), false);
            hash = sharedPreferences.getString(getString(R.string.HASH), "");
            if (isOnline) {
                if (hash.equals("")) {
                    sharedPreferences.edit().putBoolean(getString(R.string.HAS_SUBSCRIBED), false).apply();
                    Log.d(LOG_TAG, "postOfflineGames hash null");
                } else if (!initialized) {
                    Log.d(LOG_TAG, "postOfflineGames not initialized");
                } else {
                    try {
                        final ArrayList<GameModel> offlineGames = dataUtils.getOfflineGamesFromDB();

                        JSONObject gamesToSend = dataUtils.createPostGamesJson(offlineGames);
                        JsonArrayRequestPlus arrayRequest = new JsonArrayRequestPlus
                                (Request.Method.POST, serverIp + "/games", gamesToSend, new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        try {
                                            ArrayList<GameModel> gamesNotPersists = DataUtils.JsonToGamesArrayList(response);
                                            ArrayList<GameModel> allOfflineGames = new ArrayList<>(offlineGames);
                                            dataUtils.compareAndPutOnline(allOfflineGames, gamesNotPersists);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        getAll();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        NetworkResponse networkResponse = error.networkResponse;
                                        if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                                            Log.d(LOG_TAG, "Error 404 in postGames");
                                        } else if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                                            Log.d(LOG_TAG, "Error 400 in postGames");
                                        } else if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                                            Log.d(LOG_TAG, "Error 500 in postGames");
                                        }
                                        error.printStackTrace();
                                    }
                                });
                        queue.add(arrayRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public  void getHash(){
        String baseServerIp = sharedPreferences.getString(getString(R.string.SERVER_IP),"0.0.0.0");
        if(Patterns.IP_ADDRESS.matcher(baseServerIp).matches()) {
            String serverIp = "http://" + baseServerIp + ":3000";
            hash = sharedPreferences.getString(getString(R.string.HASH), "");
            Log.d(LOG_TAG, "GETHASH " + hash);
            initialized = sharedPreferences.getBoolean(getString(R.string.HAS_SUBSCRIBED), true);
            if (hash.equals("")) {
                if (initialized) {
                    sharedPreferences.edit().putBoolean(getString(R.string.HAS_SUBSCRIBED), false).apply();
                }
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, serverIp + "/subscribe", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, "getHash response");
                        try {
                            hash = response.getString(getString(R.string.HASH));
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(getString(R.string.HASH), hash).apply();
                            subscribe();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                            Log.d(LOG_TAG, "Error 500 getHash");
                        }
                        error.printStackTrace();

                    }
                });
                queue.add(jsObjRequest);
            } else {
                return;
            }
        }
    }


    public void test() {
        String baseServerIp = sharedPreferences.getString(getString(R.string.SERVER_IP), "0.0.0.0");
        if (Patterns.IP_ADDRESS.matcher(baseServerIp).matches()) {
            String serverIp = "http://" + baseServerIp + ":3000";
            isOnline = sharedPreferences.getBoolean(getString(R.string.IS_ONLINE), false);
            initialized = sharedPreferences.getBoolean(getString(R.string.HAS_SUBSCRIBED), false);
            hash = sharedPreferences.getString(getString(R.string.HASH), "");
            Log.d(LOG_TAG, "TEST");
            if (isOnline) {
                if (hash.equals("")) {
                    sharedPreferences.edit().putBoolean(getString(R.string.HAS_SUBSCRIBED), false).apply();
                } else if (!initialized) {

                } else {
                    JSONObject req = new JSONObject();
                    try {
                        req.put(getString(R.string.HASH), sharedPreferences.getString(getString(R.string.HASH), ""));
                        JsonObjectRequest objectRequest = new JsonObjectRequest
                                (Request.Method.POST, serverIp + "/users", req, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        NetworkResponse networkResponse = error.networkResponse;
                                        if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
                                            sharedPreferences.edit().putBoolean(getString(R.string.HAS_SUBSCRIBED), false).apply();
                                        }
                                        error.printStackTrace();
                                    }
                                });
                        queue.add(objectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
