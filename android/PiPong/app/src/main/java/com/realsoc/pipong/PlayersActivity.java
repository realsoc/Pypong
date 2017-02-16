package com.realsoc.pipong;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
    private static final String PLAYER_LIST = "PlayerList";
    private static final String NEW_PLAYER = "NewPlayer";
    private static final String CONFLICT_SOLVER = "ConflictSolver";
    public static final String TYPE = "type";
    public static final String NEW_NAME = "newName";
    public static final String NAME = "name";
    public static final String OLD_NAME = "oldName";
    private DataUtils dataUtils;
    private PlayerListFragment playerListFragment;
    private PlayerDetailFragment playerDetailFragment;

    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(UPDATE_VIEW)){
                if(playerListFragment != null){
                    playerListFragment.adviseDataChanged();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataUtils = DataUtils.getInstance(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);
        if (savedInstanceState == null){
            playerListFragment = PlayerListFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.players_frame_container, playerListFragment,PLAYER_LIST);
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
    }



    public void addPlayer(View v){
        if(playerListFragment != null)
            new NewPlayerDialogFragment().show(getSupportFragmentManager(), NEW_PLAYER);
    }
    public void otherPlayer(View v){
        if(playerDetailFragment!=null){
            ConflictSolverFragment conflictSolverFragment = ConflictSolverFragment.getInstance(playerDetailFragment.getName());
            conflictSolverFragment.show(getSupportFragmentManager(), CONFLICT_SOLVER);
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
        int type;
        if(mBundle.containsKey(TYPE)){
            type = mBundle.getInt(TYPE);
            switch(type){
                case CONFLICT_SOLVER_FRAGMENT:
                    if(mBundle.containsKey(NEW_NAME) && mBundle.containsKey(OLD_NAME)){
                        if(dataUtils.containsPlayer(mBundle.getString(NEW_NAME))){
                            Toast.makeText(this,getString(R.string.PLAYER_ALREADY_EXISTS),Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String oldName = mBundle.getString(OLD_NAME);
                            String newName = mBundle.getString(NEW_NAME);
                            dataUtils.modifyPlayer(oldName,newName);
                            playerListFragment.adviseDataChanged();
                        }
                    }
                    break;
                case NEW_PLAYER_DIALOG_FRAGMENT:
                    if(mBundle.containsKey(NAME)){
                        if(dataUtils.containsPlayer(mBundle.getString(NEW_NAME))){
                            Toast.makeText(this,getString(R.string.PLAYER_ALREADY_EXISTS),Toast.LENGTH_SHORT).show();
                        }else{
                            String name = mBundle.getString(NAME);
                            PlayerModel nPlayer = new PlayerModel(name);
                            dataUtils.addPlayer(nPlayer);
                            playerListFragment.adviseDataChanged();
                            getFragmentManager().popBackStack();
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onNo() {
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(activityReceiver);
        getSupportFragmentManager().beginTransaction().remove(playerListFragment);
    }

    public void launchDetailFragment(String s) {
        Fragment frag = PlayerDetailFragment.newInstance(s);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.players_frame_container, frag);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    /*public static void backgroundThreadShortToast(final Context context,
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
}
