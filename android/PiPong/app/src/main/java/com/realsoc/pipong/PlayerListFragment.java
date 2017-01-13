package com.realsoc.pipong;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
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
 * Created by Hugo on 13/01/2017.
 */

public class PlayerListFragment extends ListFragment {

    private ArrayList<String> players;
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client;

    public static PlayerListFragment newInstance(ArrayList<String> players,OkHttpClient client){
        PlayerListFragment plF = new PlayerListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("players", players);
        plF.setArguments(args);
        plF.setClient(client);
        return plF;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public void setArguments(Bundle args){
        if(args.containsKey("players")){
            this.players = args.getStringArrayList("players");
        }else{
            Log.d("BUG", "does not have players array list");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_players,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            players = savedInstanceState.getStringArrayList("players");
        }
        final ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, players);

        setListAdapter(aa);
    }
    public void setClient(OkHttpClient client){
        this.client = client;
    }
    public void addPlayer(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Player");
        alertDialog.setMessage("New Players' Name");

        final EditText input = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setPositiveButton("Send",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String nPlayerName = input.getText().toString();
                        players.add(nPlayerName);
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
                                Toast.makeText(getContext(),"Failed",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onResponse(Call call, final Response response) throws IOException {
                                Log.d(PLAYERS_ACTIVITY_NAME,response.body().string());
                                    /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });*/
                            }
                        });


                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("players",players);
        super.onSaveInstanceState(outState);
    }
}
