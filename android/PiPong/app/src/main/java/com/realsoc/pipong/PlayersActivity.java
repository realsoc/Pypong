package com.realsoc.pipong;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

public class PlayersActivity extends AppCompatActivity implements NewPlayerDialogFragment.YesNoListener{
    private static final String LOG_TAG = "PLAYERS_ACTIVITY";
    private DataUtils dataUtils;
    private PlayerListFragment playerListFragment;

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

    }


    @Override
    public void onAttachFragment(android.support.v4.app.Fragment fragment) {
        super.onAttachFragment(fragment);
        if(fragment instanceof PlayerListFragment)
            playerListFragment = (PlayerListFragment) fragment;
        Log.d("PA","OnAttachFragmentTwo");
    }



    public void addPlayer(View v){
        if(playerListFragment != null)
            playerListFragment.addPlayer(getSupportFragmentManager());
        else
            Log.d("PA","AddPlayer playerListFragment null");
    }


    @Override
    public void onYes(final String name) {
        PlayerModel nPlayer = new PlayerModel(name);
        dataUtils.addPlayer(nPlayer);
        playerListFragment.adviseNewPlayer(name);
        dataUtils.addPlayer(nPlayer);
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
        getSupportFragmentManager().beginTransaction().remove(playerListFragment);
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
