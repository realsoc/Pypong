package com.realsoc.pipong.data;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Hugo on 23/01/2017.
 */

public class SynchroData extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = "SynchroData";
    private final ContentResolver mContentResolver;
    public SynchroData(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();

    }

    public SynchroData(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        //TODO : is online mode
        //TODO : verify server (handshake)
        //TODO : DOWNLOAD DATA
        //TODO : CONFLICTS
        //TODO : UPLOAD DATA
        //TODO : CLEAN UP
        Log.d(LOG_TAG,"PERFORM SYNC");
    }
}
