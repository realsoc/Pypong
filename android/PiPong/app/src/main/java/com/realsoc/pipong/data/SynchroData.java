package com.realsoc.pipong.data;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.realsoc.pipong.PlayerListFragment;
import com.realsoc.pipong.R;
import com.realsoc.pipong.utils.DataUtils;
import com.realsoc.pipong.utils.NetworkUtils;

/**
 * Created by Hugo on 23/01/2017.
 */

public class SynchroData extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = "SynchroData";
    private final ContentResolver mContentResolver;
    private DataUtils dataUtils;
    private SharedPreferences sharedPreferences;
    private boolean isOnline;
    private boolean initialized;
    private String hash;
    private String serverIp;
    private RequestQueue queue ;
    private NetworkUtils networkUtils;
    public SynchroData(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        dataUtils = DataUtils.getInstance(context);
        networkUtils = NetworkUtils.getInstance(context);
        mContentResolver = context.getContentResolver();
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key),Context.MODE_PRIVATE);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG,"PERFORM SYNC");
        isOnline = sharedPreferences.getBoolean(getContext().getString(R.string.IS_ONLINE), false);
        initialized = sharedPreferences.getBoolean(getContext().getString(R.string.HAS_SUBSCRIBED), false);
        hash = sharedPreferences.getString(getContext().getString(R.string.HASH),"");
        String ip = sharedPreferences.getString(getContext().getString(R.string.SERVER_IP),"tarace");
        Log.d(LOG_TAG,"ip "+ip);
        if(isOnline){
            if(hash.equals("")){
                Log.d(LOG_TAG,"hash does not exist");

                networkUtils.getHash();
                initialized = false;
            }else{
                Log.d(LOG_TAG,"hash exist");
            }
            if(!initialized){
                Log.d(LOG_TAG,"Not initialized");
                networkUtils.subscribe();
            }
            initialized = sharedPreferences.getBoolean(getContext().getString(R.string.HAS_SUBSCRIBED), false);
            if(initialized){
                Log.d(LOG_TAG,"initialized");
                networkUtils.test();
                networkUtils.postOfflinePlayers();
            }
        }else{
            Log.d(LOG_TAG,"Not online");
        }
        Intent intent = new Intent(PlayerListFragment.ACTION_FINISHED_SYNC);
        getContext().sendBroadcast(new Intent(PlayerListFragment.ACTION_FINISHED_SYNC));
        //TODO : getall timestamp last and future
        //TODO : conflict resolution
        //TODO : listener on change preference all offline drop my things ...


        //TODO : is online mode
        //TODO : verify server (handshake)
        //TODO : DOWNLOAD DATA
        //TODO : CONFLICTS
        //TODO : UPLOAD DATA
        //TODO : CLEAN UP

    }
    public static void backgroundThreadShortToast(final Context context,
                                                  final String msg) {
        if (context != null && msg != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
