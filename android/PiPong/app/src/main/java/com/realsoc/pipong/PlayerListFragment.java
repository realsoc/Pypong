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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.realsoc.pipong.data.DataContract;
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
            return new PlayerListFragment();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        dataUtils = DataUtils.getInstance(getContext());
        players = dataUtils.getPlayers();
        dataUtils.registerPlayerFragment(this);
        playersStringList = dataUtils.getPlayerAsStringList();


        this.mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case 0:
                        players = dataUtils.getPlayers();
                        playersStringList = dataUtils.getPlayerAsStringList();
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
        Message msg = Message.obtain();
        msg.what = 0;
        mHandler.sendMessage(msg);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_players,container,false);
        boolean conflict= dataUtils.hasConflict();
        v.findViewById(R.id.conflictAdviser).setVisibility(conflict?View.VISIBLE:View.GONE);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });
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
        Account newAccount = new Account(ACCOUNT, MainActivity.ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) getActivity().getSystemService(ACCOUNT_SERVICE);
        accountManager.addAccountExplicitly(newAccount, null, null);
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(newAccount, DataContract.CONTENT_AUTHORITY,settingsBundle);
    }
    public void createArrayAdapter(){
        if(getListAdapter() == null){
            this.playerHMAdapter = new PlayerHMAdapter(R.layout.playerlist_item, this.players);
            setListAdapter(playerHMAdapter);
        }
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(getActivity()instanceof PlayersActivity)
            ((PlayersActivity)getActivity()).launchDetailFragment(playersStringList.get(position));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataUtils.unregisterPlayerFragment();
    }
    private class PlayerHMAdapter extends BaseAdapter {
        int mResource;
        ArrayList<Map.Entry<String,PlayerModel>> players;

        private PlayerHMAdapter( int res,Map<String,PlayerModel> nPlayers){
            mResource = res;
            players = new ArrayList<>(nPlayers.entrySet());
            Collections.sort(
                    players
                    ,   new Comparator<Map.Entry<String,PlayerModel>>() {
                        public int compare(Map.Entry<String,PlayerModel> a, Map.Entry<String,PlayerModel> b) {
                            return a.getKey().compareToIgnoreCase(b.getKey());
                        }
                    }
            );
        }
        private void actualizeSet(HashMap<String,PlayerModel> nPlayers){
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
            PlayerViewHolder playerViewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(mResource, parent, false);
                playerViewHolder = new PlayerViewHolder();
                playerViewHolder.advise = (TextView)convertView.findViewById(R.id.advise);
                playerViewHolder.nameConflictTextView = ((TextView) convertView.findViewById(R.id.nameConflict));
                playerViewHolder.nameTextView = ((TextView) convertView.findViewById(R.id.name));
                convertView.setTag(playerViewHolder);
            } else {
                playerViewHolder = (PlayerViewHolder) convertView.getTag();
            }
            Map.Entry<String,PlayerModel> player = getItem(position);
            if(player!=null){
                playerViewHolder.nameTextView.setText(player.getKey());
                playerViewHolder.nameTextView.setVisibility(player.getValue().isConflict()?View.INVISIBLE:View.VISIBLE);
                playerViewHolder.nameConflictTextView.setText(player.getKey());
                playerViewHolder.nameConflictTextView.setVisibility(player.getValue().isConflict()?View.VISIBLE:View.INVISIBLE);
                playerViewHolder.advise.setVisibility(player.getValue().isConflict()?View.VISIBLE:View.INVISIBLE);
            }
            return convertView;
        }
        private class PlayerViewHolder{
            TextView nameTextView;
            TextView nameConflictTextView;
            TextView advise;
        }
    }
}