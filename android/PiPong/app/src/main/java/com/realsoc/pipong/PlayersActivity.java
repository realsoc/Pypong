package com.realsoc.pipong;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.realsoc.pipong.Constants.PLAYERS_ACTIVITY_NAME;
import static com.realsoc.pipong.Constants.REMOTE_SERVER_ADDRESS;

/**
 * Created by Hugo on 03/01/2017.
 */

public class PlayersActivity extends AppCompatActivity implements NewPlayerDialogFragment.YesNoListener{
    private ArrayList<PlayerModel> players;
    private ArrayList<GameModel> games;
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private PlayerListFragment playerListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("PA","OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);
        Bundle extras = getIntent().getExtras();

        if (savedInstanceState != null) {

        } else {
            this.players = extras.getParcelableArrayList("players");
            this.games = extras.getParcelableArrayList("games");

            playerListFragment = PlayerListFragment.newInstance(players, games, client);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.players_frame_container, playerListFragment,"TAG");
            //transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }

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
    public void onSaveInstanceState(Bundle outState) {
        Log.d("PA","OnSaveInstanceState");
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onYes(final String name) {
        final String nPlayerName = name;
        if(name.equalsIgnoreCase("")){
            backgroundThreadShortToast(this,"Player's name can't be empty");
            return;
        }
        String req = "{\"name\":\""+nPlayerName+"\"}";
        Log.d(PLAYERS_ACTIVITY_NAME,req);
        RequestBody body = RequestBody.create(JSON,req);
        Request request = new Request.Builder()
                .url(REMOTE_SERVER_ADDRESS+"/api/players")
                .post(body)
                .build();
        Log.d(PLAYERS_ACTIVITY_NAME,body.toString());
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                backgroundThreadShortToast(getApplicationContext(),"Failed");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Log.d(PLAYERS_ACTIVITY_NAME,response.body().string());
                if(response.code() == 201||response.code() == 200){
                    playerListFragment.addPlayerToList(nPlayerName);
                }else if(response.code() == 202){
                    backgroundThreadShortToast(getApplicationContext(),"Player Already Exists");
                }
            }
        });
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

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d("PA","OnSaveInstanceStateTwoArgs");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("PA","OnRestoreInstanceState");
        this.players = savedInstanceState.getParcelableArrayList("players");
        this.games = savedInstanceState.getParcelableArrayList("games");
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
    }

    @Override
    public void onNo() {
    }
}
