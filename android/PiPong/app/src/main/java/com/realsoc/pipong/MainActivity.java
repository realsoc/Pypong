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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.realsoc.pipong.utils.DataUtils;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";
    private boolean isFirstLaunched;
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
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

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
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        isFirstLaunched = sharedPreferences.getBoolean(getString(R.string.FIRST_LAUNCHED), true);
        if (isFirstLaunched) {
            // TODO : Tuto or Dialog with choice online or offline
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.FIRST_LAUNCHED), false);

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
        ContentResolver.setIsSyncable(mAccount, "com.realsoc.pipong.provider", 1);
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(mAccount, "com.realsoc.pipong.provider", settingsBundle);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();*/
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */ /*
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        /*
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }*/

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
