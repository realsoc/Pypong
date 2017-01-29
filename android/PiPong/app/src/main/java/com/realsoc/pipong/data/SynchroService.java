package com.realsoc.pipong.data;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Hugo on 23/01/2017.
 */

public class SynchroService extends Service {
    private static SynchroData sSyncAdapter = null;
    private static final Object sSyncAdapterLock = new Object();
    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (sSyncAdapterLock){
            if(sSyncAdapter == null){
                sSyncAdapter = new SynchroData(getApplicationContext(),true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
