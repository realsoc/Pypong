package com.realsoc.pipong;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.realsoc.pipong.model.CountModel;
import com.realsoc.pipong.model.PlayerModel;
import com.realsoc.pipong.utils.DataUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hugo on 13/01/2017.
 */

public class PlayerListFragment extends ListFragment {

    private static final String LOG_TAG = "PlayerListFragment";
    private HashMap<String,PlayerModel> players;
    private HashMap<String,CountModel> counts;
    private ArrayList<String> playersStringList;
    private ArrayAdapter<String> aa;
    private Handler mHandler;
    private DataUtils dataUtils;
    public static PlayerListFragment newInstance(){


            PlayerListFragment eu = new PlayerListFragment();
        return eu;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("PLF","OnCreate");
        dataUtils = DataUtils.getInstance(getContext());
        players = dataUtils.getPlayers();
        counts = dataUtils.getCounts();
        playersStringList = dataUtils.getPlayerAsStringList();

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
    public void adviseNewPlayer(String name){
        if(!playersStringList.contains(name)){
            Log.d(LOG_TAG,"Error adding player");
            //playersStringList.add(name);
            //players.put(name,new PlayerModel(name));
        }
        Message msg = Message.obtain();
        msg.what = 0;
        mHandler.sendMessage(msg);
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
        PlayerDetailFragment newFragment = PlayerDetailFragment.newInstance(playersStringList.get(position));
       /* Account mAccount = new Account("dummyaccount","realsoc.com");
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(mAccount,"com.realsoc.pipong.provider",settingsBundle);*/
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.players_frame_container, newFragment);
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();
    }


    public void addPlayer(FragmentManager supportFragmentManager){
        new NewPlayerDialogFragment().show(supportFragmentManager, "NewPlayer");
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