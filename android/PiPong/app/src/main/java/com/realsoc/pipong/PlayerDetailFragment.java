package com.realsoc.pipong;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.realsoc.pipong.model.CountModel;
import com.realsoc.pipong.model.PlayerModel;
import com.realsoc.pipong.utils.DataUtils;

/**
 * Created by Hugo on 15/01/2017.
 */

public class PlayerDetailFragment extends Fragment {
    private static final String LOG_TAG = "PlayerDetailFragment";
    private static final String SAVE_BUNDLE = "SaveBundle";
    private static final String NAME = "name";
    private String name;
    private PlayerModel player;
    private CountModel mCount;
    private Bundle savedState;
    private DataUtils dataUtils;
    private Button samePlayer;
    private Button otherPlayer;
    public PlayerDetailFragment(){

    }
    public String getName(){
        return name;
    }
    public static PlayerDetailFragment newInstance(String nName) {
        PlayerDetailFragment eu = new PlayerDetailFragment();
        eu.setName(nName);
        return eu;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null){
            if(savedState == null && savedInstanceState.containsKey(SAVE_BUNDLE)){
                savedState = savedInstanceState.getBundle(SAVE_BUNDLE);
            }
        }
        if(savedState != null){
            name = savedState.getString(NAME);
        }
        Log.d(LOG_TAG,name);
        dataUtils = DataUtils.getInstance(getContext());
        player = dataUtils.getPlayers().get(name);
        mCount = dataUtils.getCounts().get(name);
        super.onCreate(savedInstanceState);
    }

    public void setName(String name) {
        this.name = name;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_player_details,container,false);
        if(player != null){
            samePlayer = (Button) ret.findViewById(R.id.same_player);
            otherPlayer = (Button) ret.findViewById(R.id.other_player);
            if(player.isConflict()){
                samePlayer.setVisibility(Button.VISIBLE);
                otherPlayer.setVisibility(Button.VISIBLE);
            }
            else{
                samePlayer.setVisibility(Button.INVISIBLE);
                otherPlayer.setVisibility(Button.INVISIBLE);
            }
        }
        if(mCount != null){
            String toto = mCount.getName();
            ((TextView) ret.findViewById(R.id.player_name)).setText(toto);
            ((TextView) ret.findViewById(R.id.pl6)).setText(String.valueOf(mCount.getPointLost6()));
            ((TextView) ret.findViewById(R.id.pl11)).setText(String.valueOf(mCount.getPointLost11()));
            ((TextView) ret.findViewById(R.id.pl21)).setText(String.valueOf(mCount.getPointLost21()));
            ((TextView) ret.findViewById(R.id.pl0)).setText(String.valueOf(mCount.getPointLost6()+mCount.getPointLost21()+mCount.getPointLost11()));
            ((TextView) ret.findViewById(R.id.ps6)).setText(String.valueOf(mCount.getPointScored6()));
            ((TextView) ret.findViewById(R.id.ps11)).setText(String.valueOf(mCount.getPointScored11()));
            ((TextView) ret.findViewById(R.id.ps21)).setText(String.valueOf(mCount.getPointScored21()));
            ((TextView) ret.findViewById(R.id.ps0)).setText(String.valueOf(mCount.getPointScored6()+mCount.getPointScored21()+mCount.getPointScored11()));
            ((TextView) ret.findViewById(R.id.gl6)).setText(String.valueOf(mCount.getGameLost6()));
            ((TextView) ret.findViewById(R.id.gl11)).setText(String.valueOf(mCount.getGameLost11()));
            ((TextView) ret.findViewById(R.id.gl21)).setText(String.valueOf(mCount.getGameLost21()));
            ((TextView) ret.findViewById(R.id.gl0)).setText(String.valueOf(mCount.getGameLost6()+mCount.getGameLost21()+mCount.getGameLost11()));
            ((TextView) ret.findViewById(R.id.gw6)).setText(String.valueOf(mCount.getGameWon6()));
            ((TextView) ret.findViewById(R.id.gw11)).setText(String.valueOf(mCount.getGameWon11()));
            ((TextView) ret.findViewById(R.id.gw21)).setText(String.valueOf(mCount.getGameWon21()));
            ((TextView) ret.findViewById(R.id.gw0)).setText(String.valueOf(mCount.getGameWon6()+mCount.getGameWon21()+mCount.getGameWon11()));
            ((TextView) ret.findViewById(R.id.gp6)).setText(String.valueOf(mCount.getGamePlayed6()));
            ((TextView) ret.findViewById(R.id.gp11)).setText(String.valueOf(mCount.getGamePlayed11()));
            ((TextView) ret.findViewById(R.id.gp21)).setText(String.valueOf(mCount.getGamePlayed21()));
            ((TextView) ret.findViewById(R.id.gp0)).setText(String.valueOf(mCount.getGamePlayed6()+mCount.getGamePlayed21()+mCount.getGamePlayed11()));
        }
        return ret;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(SAVE_BUNDLE, (savedState != null) ? savedState : saveState());
    }
    private Bundle saveState() {
        Bundle state = new Bundle();
        state.putString(NAME, name);
        return state;
    }

    public void killYaSelf() {
        getFragmentManager().popBackStack();
    }
}
