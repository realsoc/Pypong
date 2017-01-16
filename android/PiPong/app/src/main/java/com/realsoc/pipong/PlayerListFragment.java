package com.realsoc.pipong;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

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
    private ArrayAdapter<String> aa;
    private Handler mHandler;
    private Bundle savedState = null;
    public static PlayerListFragment newInstance(final ArrayList<PlayerModel> players,
                                                 ArrayList<GameModel> games,
                                                 OkHttpClient client){


            PlayerListFragment eu = new PlayerListFragment();
            Bundle args = new Bundle();
            args.putParcelableArrayList("players", players);
            args.putParcelableArrayList("games", games);
            eu.setArguments(args);
            eu.setClient(client);
        return eu;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("PLF","OnCreate");
        if(savedInstanceState == null && savedState == null){
            if(players != null){
                if(playersStringList == null){
                    playersStringList = createStrListFromObjList(players);
                }
            }
        }else{
            if(savedState == null){
                if(savedInstanceState.containsKey("SaveBundle"));
                    savedState = savedInstanceState.getBundle("SaveBundle");
            }
            if(savedState == null)
                throw new AssertionError("savedState can't be null");
            if(players == null){
                if(savedState.containsKey("players")){
                    players = savedState.getParcelableArrayList("players");
                    if(playersStringList == null){
                        if(savedState.containsKey("playersStringList")){
                            playersStringList = savedState.getStringArrayList("playersStringList");
                        }else{
                            playersStringList = createStrListFromObjList(players);
                        }
                    }
                }
            }
            if(games == null){
                if(savedState.containsKey("games")){
                    games = savedState.getParcelableArrayList("games");
                }
            }
        }
        if(games == null)
            Log.d("PLF","OC : games null");
        if(players == null)
            Log.d("PLF","OC : players null");
        if(playersStringList == null)
            Log.d("PLF","OC : playersStringList null");
        this.mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case 0:
                        aa.notifyDataSetChanged();
                        break;
                    case 1:
                        createArrayAdapter();
                        break;
                }
            }
        };
        super.onCreate(savedInstanceState);

    }
    public void addPlayerToList(String name){
        this.playersStringList.add(name);
        players.add(new PlayerModel(name));
        Message msg = Message.obtain();
        msg.what = 0;
        mHandler.sendMessage(msg);
    }
    public void setArguments(Bundle args){
        Log.d("PLF","SetArguments");
        if(args.containsKey("players")) {
            this.players = args.getParcelableArrayList("players");
            playersStringList = createStrListFromObjList(players);
        }else{
            Log.d("PLF", "BUG does not have players array list");
        }
        if(args.containsKey("games")){
            this.games = args.getParcelableArrayList("games");
        }else{
            Log.d("PLF", "BUG does not have games array list");
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
        View v = inflater.inflate(R.layout.fragment_players,container,false);
        Log.d("PLF","OnCreateView ");
        if(playersStringList != null){
            createArrayAdapter();
        }
        return v;
    }

    public void createArrayAdapter(){
        Log.d("PLF","CreateArrayAdapter");
        if(getListAdapter() == null){
            this.aa = new ArrayAdapter(getActivity(),
                    android.R.layout.simple_list_item_1, this.playersStringList);
            setListAdapter(aa);
        }
        else
            Log.d("PLF","CAA listadapter already exists");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d("PLF","OnListItemClick");
        PlayerModel currentPlayerModel = this.players.get(position);
        PlayerDetailFragment newFragment = PlayerDetailFragment.newInstance(currentPlayerModel);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.players_frame_container, newFragment);
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("PLF","OnActivityCreated");
    }
    public void setClient(OkHttpClient client){
        this.client = client;
    }
    public void addPlayer(FragmentManager supportFragmentManager){
        new NewPlayerDialogFragment().show(supportFragmentManager, "tag"); // or getFragmentManager() in API 11+
    }
    @Override
    public void onDestroyView() {
        Log.d("PLF","OnDestroyView");
        super.onDestroyView();
        savedState = saveState();
    }

    private Bundle saveState() { /* called either from onDestroyView() or onSaveInstanceState() */
        Bundle state = new Bundle();
        state.putParcelableArrayList("players", players);
        state.putParcelableArrayList("games", games);
        state.putStringArrayList("playersStringList", playersStringList);
        return state;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("PLF","OnSaveInstanceState ");
        outState.putBundle("SaveBundle", (savedState != null) ? savedState : saveState());
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("PLF","OnAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("PLF","OnDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("PLF","OnDestroy");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("PLF","OnViewCreated");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("PLF","OnPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("PLF","OnResume");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d("PLF","OnViewStateRestored");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("PLF","OnStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("PLF","OnStart");
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        Log.d("PLF","OnInflate");
    }
}