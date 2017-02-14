package com.realsoc.pipong;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.realsoc.pipong.model.PlayerModel;
import com.realsoc.pipong.utils.DataUtils;
import com.realsoc.pipong.utils.NetworkUtils;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";
    private boolean isFirstLaunched;
    private boolean hasSuscribed;
    private boolean activityCreated = false;
    public static final String AUTHORITY = "com.realsoc.pipong.data.provider";
    public static final String ACCOUNT_TYPE = "pipong.com";
    public static final String ACCOUNT = "dummyaccount";
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
            if(savedInstanceState.containsKey("activityCreated")){
                activityCreated = savedInstanceState.getBoolean("activityCreated");
            }
            if(savedInstanceState.containsKey("dataHolderInit")){
                dataHolderInit = savedInstanceState.getBoolean("dataHolderInit");
            }
        }
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isFirstLaunched = sharedPreferences.getBoolean(getString(R.string.FIRST_LAUNCHED), true);
        //TODO STRING INTERNATIONALISATION
        hasSuscribed = sharedPreferences.getBoolean("has_subscribed",false);
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
        /*
        mAccount = CreateSyncAccount(this);
        ContentResolver.setIsSyncable(mAccount, "com.realsoc.pipong.data.provider", 1);
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(mAccount, "com.realsoc.pipong.data.provider", settingsBundle);*/

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
    public void test(View v){
        DataUtils dataUtils = DataUtils.getInstance(this);

        NetworkUtils networkUtils = NetworkUtils.getInstance(this);
        HashMap<String,PlayerModel> players = dataUtils.getOfflinePlayersFromDB();
        Log.d(LOG_TAG,players.toString());
        networkUtils.postOfflinePlayers();
        players = dataUtils.getOfflinePlayersFromDB();
        Log.d(LOG_TAG,players.toString());
    }

    public static Account CreateSyncAccount(Context context) {
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);

        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            Log.d(LOG_TAG, "Account created");
        } else {
            Log.d(LOG_TAG, "Account already exists");
        }
        return newAccount;
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
        outState.putBoolean("activityCreated",activityCreated);
        outState.putBoolean("dataHolderInit",dataHolderInit);
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


    private class CreateDataHolder extends AsyncTask<Void, Integer, Boolean> {
        private Context context;
        private DataUtils mDataHolder;
        private Intent toLaunch = null;
        public CreateDataHolder(Context context) {
            this.context = context;
        }
        public void setToLaunch(Intent nIntent){
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
