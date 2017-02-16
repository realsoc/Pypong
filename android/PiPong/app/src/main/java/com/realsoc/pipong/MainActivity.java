package com.realsoc.pipong;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.realsoc.pipong.utils.DataUtils;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";
    private boolean isFirstLaunched;
    private boolean activityCreated = false;
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 60L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;
    public static final String AUTHORITY = "com.realsoc.pipong.data.provider";
    public static final String ACCOUNT_TYPE = "pipong.com";
    public static final String ACCOUNT = "dummyaccount";
    private static final String ACTIVITY_CREATED = "activityCreated";
    private static final String DATA_HOLDER_INIT = "dataHolderInit";
    private boolean dataHolderInit = false;
    private final Object dataHolderInitLock = new Object();
    private CreateDataHolder createDataHolder;
    private Button playerButton;
    private Button newGameButton;


    Account mAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(ACTIVITY_CREATED)){
                activityCreated = savedInstanceState.getBoolean(ACTIVITY_CREATED);
            }
            if(savedInstanceState.containsKey(DATA_HOLDER_INIT)){
                dataHolderInit = savedInstanceState.getBoolean(DATA_HOLDER_INIT);
            }
        }
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isFirstLaunched = sharedPreferences.getBoolean(getString(R.string.FIRST_LAUNCHED), true);
        //TODO STRING INTERNATIONALISATION
        if (isFirstLaunched) {
            // TODO : Tuto or Dialog with choice online or offline
            sharedPreferences.edit().putBoolean(getString(R.string.FIRST_LAUNCHED),false)
                    .putString(getString(R.string.SERVER_IP),"").apply();
            defaultSharedPreferences.edit().putBoolean(getString(R.string.IS_ONLINE),false)
                    .putString(getString(R.string.SERVER_IP),"0.0.0.0").apply();
            //backgroundThreadShortToast(this,"FIRST LAUNCHED");
        }
        if(!activityCreated){
            createDataHolder = new CreateDataHolder(this);
            createDataHolder.execute();
            activityCreated = true;
        }
        setContentView(R.layout.activity_main);

        playerButton = (Button) findViewById(R.id.player_activity_button);
        newGameButton = (Button) findViewById(R.id.new_game_button);
        mAccount = new Account(ACCOUNT, MainActivity.ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(mAccount, null, null)) {
            Log.d(LOG_TAG, "Account created");
        } else {
            Log.d(LOG_TAG, "Account already exists");
        }
        ContentResolver.setIsSyncable(mAccount, AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
        ContentResolver.addPeriodicSync(
                mAccount,
                AUTHORITY,
                Bundle.EMPTY,
                SYNC_INTERVAL);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.preferences:
            {
                Intent intent = new Intent();
                intent.setClassName(this, "com.realsoc.pipong.SettingsActivity");
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
    public void addPlayer(View v) {
        Button b = (Button) v;
        b.setEnabled(false);
        Intent intent = new Intent(this,PlayersActivity.class);
        synchronized (dataHolderInitLock){
            if(dataHolderInit){
                b.setEnabled(true);
                startActivity(intent);
            }else{
                createDataHolder.setToLaunch(intent);
            }
        }
    }

    public void newGame(View v) {
        Button b = (Button) v;
        b.setEnabled(false);
        Intent intent = new Intent(this,ConfigurationActivity.class);
        synchronized (dataHolderInitLock){
            if(dataHolderInit){
                b.setEnabled(true);
                startActivity(intent);
            }else{
                createDataHolder.setToLaunch(intent);
            }
        }
    }
    public void ranking(View view) {
        Button b = (Button) view;
        b.setEnabled(false);
        Intent intent = new Intent(this,RankingActivity.class);
        synchronized (dataHolderInitLock){
            if(dataHolderInit){
                b.setEnabled(true);
                startActivity(intent);
            }else{
                createDataHolder.setToLaunch(intent);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerButton = (Button) findViewById(R.id.player_activity_button);
        newGameButton = (Button) findViewById(R.id.new_game_button);
        playerButton.setEnabled(true);
        newGameButton.setEnabled(true);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ACTIVITY_CREATED,activityCreated);
        outState.putBoolean(DATA_HOLDER_INIT,dataHolderInit);
    }/*
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
    }*/




    private class CreateDataHolder extends AsyncTask<Void, Integer, Boolean> {
        private Context context;
        private DataUtils mDataHolder;
        private Intent toLaunch = null;
        private CreateDataHolder(Context context) {
            this.context = context;
        }
        private void setToLaunch(Intent nIntent){
            toLaunch = nIntent;
        }

        protected Boolean doInBackground(Void... result) {
            mDataHolder = DataUtils.getInstance(context);
            return mDataHolder.isInit();
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result){
                synchronized (dataHolderInitLock){
                    if(toLaunch != null){
                        playerButton.setEnabled(true);
                        newGameButton.setEnabled(true);
                        startActivity(toLaunch);
                    }
                    dataHolderInit = true;
                }
            }
        }
    }
}
