package com.realsoc.pipong;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.realsoc.pipong.model.CountModel;
import com.realsoc.pipong.model.PlayerModel;
import com.realsoc.pipong.utils.DataUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.ACCOUNT_SERVICE;
import static com.realsoc.pipong.MainActivity.ACCOUNT;

/**
 * Created by Hugo on 13/01/2017.
 */

public class PlayerListFragment extends ListFragment {

    private static final String LOG_TAG = "PlayerListFragment";

    private HashMap<String,PlayerModel> players;
    private HashMap<String,CountModel> counts;
    private List<String> playersStringList;
    private PlayerHMAdapter playerHMAdapter;
    private Handler mHandler;
    private DataUtils dataUtils;
    private SwipeRefreshLayout swipeContainer;
    public static final String ACTION_FINISHED_SYNC = "your.package.ACTION_FINISHED_SYNC";
    private static IntentFilter syncIntentFilter = new IntentFilter(ACTION_FINISHED_SYNC);
    private BroadcastReceiver syncBroadcastReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            if(swipeContainer!=null)
            {
                swipeContainer.setRefreshing(false);
            }
        }
    };
    public static PlayerListFragment newInstance(){
            PlayerListFragment eu = new PlayerListFragment();
        return eu;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("PLF","OnCreate");
        dataUtils = DataUtils.getInstance(getContext());
        players = dataUtils.getPlayers();
        dataUtils.registerPlayerFragment(this);
        counts = dataUtils.getCounts();
        playersStringList = dataUtils.getPlayerAsStringList();


        this.mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                Log.d(LOG_TAG,"MESSAGE RECEIVED");
                switch(msg.what){
                    case 0:
                        players = dataUtils.getPlayers();
                        playersStringList = dataUtils.getPlayerAsStringList();
                        //Log.d(LOG_TAG,"SIZE "+playersStringList.size());
                        Log.d(LOG_TAG," size +"+players.size());
                        playerHMAdapter.actualizeSet(players);
                        break;
                    case 1:
                        createArrayAdapter();
                        break;
                }
            }
        };
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(syncBroadcastReceiver,syncIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(syncBroadcastReceiver);
    }

    public void adviseDataChanged(){
        Log.d(LOG_TAG,"ADVISE DATA CHANGED");
        Message msg = Message.obtain();
        msg.what = 0;
        mHandler.sendMessage(msg);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_players,container,false);
        Log.d("PLF","OnCreateView ");
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        if(players != null){
            createArrayAdapter();
        }
        return v;
    }
    public void fetchTimelineAsync() {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        Account newAccount = new Account(ACCOUNT, MainActivity.ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) getActivity().getSystemService(ACCOUNT_SERVICE);
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
        ContentResolver.requestSync(newAccount,"com.realsoc.pipong.data.provider",settingsBundle);

    }


    public void createArrayAdapter(){
        Log.d("PLF","CreateArrayAdapter");
        if(getListAdapter() == null){
            this.playerHMAdapter = new PlayerHMAdapter(R.layout.playerlist_item, this.players);
            setListAdapter(playerHMAdapter);
        }
        else
            Log.d("PLF","CAA listadapter already exists");
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d("PLF","OnListItemClick pos "+position+" size "+players.size());
        if(getActivity()instanceof PlayersActivity)
            ((PlayersActivity)getActivity()).launchDetailFragment(playersStringList.get(position));

    }


   /* public void addPlayer(FragmentManager supportFragmentManager){
        new NewPlayerDialogFragment().show(supportFragmentManager, "NewPlayer");
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataUtils.unregisterPlayerFragment();
    }
    private class PlayerHMAdapter extends BaseAdapter {
        int mResource;
        ArrayList<Map.Entry<String,PlayerModel>> players;

        public PlayerHMAdapter( int res,Map<String,PlayerModel> nPlayers){
            mResource = res;
            players = new ArrayList<Map.Entry<String,PlayerModel>>(nPlayers.entrySet());
            Collections.sort(
                    players
                    ,   new Comparator<Map.Entry<String,PlayerModel>>() {
                        public int compare(Map.Entry<String,PlayerModel> a, Map.Entry<String,PlayerModel> b) {
                            return a.getKey().compareToIgnoreCase(b.getKey());
                        }
                    }
            );
        }
        public void actualizeSet(HashMap<String,PlayerModel> nPlayers){
                players.clear();
                players.addAll(nPlayers.entrySet());
            Collections.sort(
                    players
                    ,   new Comparator<Map.Entry<String,PlayerModel>>() {
                        public int compare(Map.Entry<String,PlayerModel> a, Map.Entry<String,PlayerModel> b) {
                            return a.getKey().compareToIgnoreCase(b.getKey());
                        }
                    }
            );
                this.notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return players.size();
        }

        @Override
        public Map.Entry<String,PlayerModel> getItem(int position) {
            return players.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO implement you own logic with ID
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View result;

            if (convertView == null) {
                result = LayoutInflater.from(parent.getContext()).inflate(mResource, parent, false);
            } else {
                result = convertView;
            }

            Map.Entry<String,PlayerModel> player = getItem(position);

            // TODO replace findViewById by ViewHolder
            TextView nameTextView =  ((TextView) result.findViewById(R.id.name));
            TextView nameConflictTextView =  ((TextView) result.findViewById(R.id.nameConflict));
            nameTextView.setText(player.getKey());
            nameTextView.setVisibility(player.getValue().isConflict()?View.INVISIBLE:View.VISIBLE);
            nameConflictTextView.setText(player.getKey());
            nameConflictTextView.setVisibility(player.getValue().isConflict()?View.VISIBLE:View.INVISIBLE);
            ((TextView) result.findViewById(R.id.advise)).setVisibility(player.getValue().isConflict()?View.VISIBLE:View.INVISIBLE);
            return result;
        }
    }

    /*
            @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("PLF","OnActivityCreated");
    }
            @Override
    public void onDestroyView() {
        Log.d("PLF","OnDestroyView");
        super.onDestroyView();
        savedState = saveState();
    }
            private Bundle saveState() { // called either from onDestroyView() or onSaveInstanceState()
        Bundle state = new Bundle();
    state.putSerializable("players", players);
    state.putStringArrayList("playersStringList", playersStringList);
    return state;
}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("PLF","OnSaveInstanceState ");
        outState.putBundle("SaveBundle", (savedState != null) ? savedState : saveState());
    }
         public void setArguments(Bundle args){
        Log.d("PLF","SetArguments");
        if(args.containsKey("players")) {
            this.players = (HashMap<String,PlayerModel>)args.getSerializable("players");
            playersStringList =new ArrayList<>(players.keySet());
        }else{
            Log.d("PLF", "BUG does not have players array list");
        }
    }
        if(savedInstanceState == null && savedState == null){
            if(players != null){
                if(playersStringList == null){
                    playersStringList = new ArrayList<>(players.keySet());
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
                    players = (HashMap<String,PlayerModel>)savedState.getSerializable("players");
                    if(playersStringList == null){
                        if(savedState.containsKey("playersStringList")){
                            playersStringList = savedState.getStringArrayList("playersStringList");
                        }else{
                            playersStringList = new ArrayList<>(players.keySet());
                        }
                    }
                }
            }
        }
        if(players == null)
            Log.d("PLF","OC : players null");
        if(playersStringList == null)
            Log.d("PLF","OC : playersStringList null");*/
/*

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
    }*/
}