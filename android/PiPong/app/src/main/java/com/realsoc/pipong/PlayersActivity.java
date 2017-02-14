package com.realsoc.pipong;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.realsoc.pipong.model.PlayerModel;
import com.realsoc.pipong.utils.DataUtils;

/**
 * Created by Hugo on 03/01/2017.
 */

public class PlayersActivity extends AppCompatActivity implements YesNoListener{
    public static final int NEW_PLAYER_DIALOG_FRAGMENT = 0;
    public static final int CONFLICT_SOLVER_FRAGMENT = 1;
    private static final String LOG_TAG = "PLAYERS_ACTIVITY";
    public static final String UPDATE_VIEW = "UPDATE_VIEW";
    private DataUtils dataUtils;
    private PlayerListFragment playerListFragment;
    private PlayerDetailFragment playerDetailFragment;

    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG,"ON RECEIVE");
            if(intent.getAction().equals(UPDATE_VIEW)){
                Log.d(LOG_TAG,"ON UPDATE VIEW");

                if(playerListFragment != null){
                    Log.d(LOG_TAG,"ADVISE DATA CHANGED");

                    playerListFragment.adviseDataChanged();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("PA","OnCreate");
        dataUtils = DataUtils.getInstance(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        if (savedInstanceState != null) {

        } else {
            playerListFragment = PlayerListFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.players_frame_container, playerListFragment,"PlayerList");
            transaction.commit();
        }
        if (activityReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(UPDATE_VIEW);
            registerReceiver(activityReceiver, intentFilter);
        }

    }


    @Override
    public void onAttachFragment(android.support.v4.app.Fragment fragment) {
        super.onAttachFragment(fragment);
        if(fragment instanceof PlayerListFragment)
            playerListFragment = (PlayerListFragment) fragment;
        else if(fragment instanceof PlayerDetailFragment)
            playerDetailFragment = (PlayerDetailFragment)fragment;
        Log.d("PA","OnAttachFragmentTwo");
    }



    public void addPlayer(View v){
        if(playerListFragment != null)
            //playerListFragment.addPlayer(getSupportFragmentManager());
            new NewPlayerDialogFragment().show(getSupportFragmentManager(), "NewPlayer");
        else
            Log.d("PA","AddPlayer playerListFragment null");
    }
    public void otherPlayer(View v){
        Log.d(LOG_TAG,"HEY");
        if(playerDetailFragment!=null){
            Log.d(LOG_TAG,"HO");
            ConflictSolverFragment conflictSolverFragment = ConflictSolverFragment.getInstance(playerDetailFragment.getName());
            conflictSolverFragment.show(getSupportFragmentManager(), "ConflictSolver");
        }
    }
    public void samePlayer(View v){
        if(playerDetailFragment != null){
            String name = playerDetailFragment.getName();
            playerDetailFragment.killYaSelf();
            dataUtils.removePlayer(name);
            dataUtils.notInConflict(name);
            if(playerListFragment!=null){
                playerListFragment.adviseDataChanged();
            }
        }
    }


    @Override
    public void onYes(Bundle mBundle) {
        int type = -1;
        if(mBundle.containsKey("type")){
            type = mBundle.getInt("type");
            switch(type){
                case CONFLICT_SOLVER_FRAGMENT:
                    if(mBundle.containsKey("newName") && mBundle.containsKey("oldName")){
                        if(dataUtils.containsPlayer(mBundle.getString("newName"))){
                            Toast.makeText(this,"This player already exists",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String oldName = mBundle.getString("oldName");
                            String newName = mBundle.getString("newName");
                            dataUtils.modifyPlayer(oldName,newName);
                            playerListFragment.adviseDataChanged();
                        }
                    }
                    break;
                case NEW_PLAYER_DIALOG_FRAGMENT:
                    if(mBundle.containsKey("name")){
                        String name = mBundle.getString("name");
                        PlayerModel nPlayer = new PlayerModel(name);
                        dataUtils.addPlayer(nPlayer);
                        playerListFragment.adviseDataChanged();
                        getFragmentManager().popBackStack();
                    }
                    else
                        Log.d(LOG_TAG,"onYes new player name does not exist");
                    break;
            }
        }
        else{
            Log.d(LOG_TAG,"onYes type does not exist");
        }
    }

    @Override
    public void onNo() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("PA","OnDestroy");
        unregisterReceiver(activityReceiver);
        getSupportFragmentManager().beginTransaction().remove(playerListFragment);
    }

    public void launchDetailFragment(String s) {

        //ContentResolver.setIsSyncable(mAccount, "com.realsoc.pipong.data.provider", 1);
/*
        Account newAccount = new Account(ACCOUNT, MainActivity.ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) this.getSystemService(ACCOUNT_SERVICE);

        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            Log.d(LOG_TAG, "Account created");
        } else {
            Log.d(LOG_TAG, "Account already exists");
        }
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(newAccount,"com.realsoc.pipong.data.provider",settingsBundle);*/
        Fragment frag = PlayerDetailFragment.newInstance(s);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back*/
        transaction.replace(R.id.players_frame_container, frag);
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();
    }

/*
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("PA","OnRestoreInstanceState");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("PA","OnSaveInstanceState");
        super.onSaveInstanceState(outState);

    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d("PA","OnSaveInstanceStateTwoArgs");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("PA","OnRestart");
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Log.d("PA","OnCreateTwoArgs");
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.d("PA","OnAttachFragment");
    }

    @Override
    public void onStateNotSaved() {
        super.onStateNotSaved();
        Log.d("PA","OnStateNotSaved");
    }
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Log.d("PA","OnResultFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("PA","OnPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("PA","OnResume");
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d("PA","OnStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("PA","OnStart");
    }*/

}
