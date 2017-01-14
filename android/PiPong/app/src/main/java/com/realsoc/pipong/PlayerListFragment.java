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

    private ArrayList<PlayerModel> players;
    private ArrayList<String> playersStringList;
    private ArrayList<GameModel> games;
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client;

    public static PlayerListFragment newInstance(ArrayList<PlayerModel> players,
                                                 ArrayList<GameModel> games,
                                                 OkHttpClient client){
        PlayerListFragment plF = new PlayerListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("players", players);
        args.putParcelableArrayList("games", games);
        plF.setArguments(args);
        plF.setClient(client);
        return plF;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("PLF","OC");

        super.onCreate(savedInstanceState);

    }
    public void setArguments(Bundle args){
        Log.d("PLF","SET ARGUMENTS");
        if(args.containsKey("players")) {
            this.players = args.getParcelableArrayList("players");
            this.playersStringList = createStrListFromObjList(this.players);
            Log.d("PLF","SIZE PSL false : "+this.playersStringList.size());
        }else{
            Log.d("BUG", "does not have players array list");
        }
        if(args.containsKey("games")){
            this.games = args.getParcelableArrayList("games");
        }else{
            Log.d("BUG", "does not have games array list");
        }
    }

    private ArrayList<String> createStrListFromObjList(ArrayList<PlayerModel> players) {
        ArrayList<String> ret = new ArrayList<>();
        for(PlayerModel player : players){
            ret.add(player.getName());
        }
        return ret;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("PLF","OCV");

        return inflater.inflate(R.layout.fragment_players,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("PLF","OAC");

        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            Log.d("PLF","savenotnull in OAC");

            this.playersStringList = savedInstanceState.getStringArrayList("playersStringList");

            Log.d("PLF","SIZE PSL true : "+this.playersStringList.size());
        }
        final ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, this.playersStringList);

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
                        final String nPlayerName = input.getText().toString();
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
                                if(response.code() == 201){
                                    playersStringList.add(nPlayerName);
                                    players.add(new PlayerModel(nPlayerName));
                                }else if(response.code() == 202){
                                    Toast.makeText(getContext(),"Player already exists", Toast.LENGTH_SHORT).show();
                                }
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
        Log.d("PLF","OSIS");
        outState.putStringArrayList("playersStringList",this.playersStringList);
        outState.putParcelableArrayList("players",this.players);
        outState.putParcelableArrayList("games",this.games);
        Log.d("PLF","saving PSL size : "+this.playersStringList.size());

        super.onSaveInstanceState(outState);
    }
}
