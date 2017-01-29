package com.realsoc.pipong;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.realsoc.pipong.model.CountModel;
import com.realsoc.pipong.utils.DataUtils;

/**
 * Created by Hugo on 15/01/2017.
 */

public class PlayerDetailFragment extends Fragment {
    private static final String LOG_TAG = "PlayerDetailFragment";
    //private PlayerModel mPlayer;
    private String name;
    private CountModel mCount;
    private Bundle savedState;
    private DataUtils dataUtils;
    public PlayerDetailFragment(){

    }

    public static PlayerDetailFragment newInstance(String nName) {
        Log.d(LOG_TAG, "NewInstance");
        PlayerDetailFragment eu = new PlayerDetailFragment();
        eu.setName(nName);
        return eu;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG,"OnCreate");
        if(savedInstanceState != null){
            if(savedState == null && savedInstanceState.containsKey("SaveBundle")){
                savedState = savedInstanceState.getBundle("SaveBundle");
            }
        }
        if(savedState != null){
            name = savedState.getString("name");
        }
        Log.d(LOG_TAG,name);


        dataUtils = DataUtils.getInstance(getContext());
        mCount = dataUtils.getCounts().get(name);
        if(mCount == null)
            Log.d(LOG_TAG,"OC : count null");
        super.onCreate(savedInstanceState);
    }

    public void setName(String name) {
        this.name = name;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG,"OnCreateView");
        View ret = inflater.inflate(R.layout.fragment_player_details,container,false);
        if(mCount != null){
            ((TextView) ret.findViewById(R.id.player_name)).setText(mCount.getName());
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
        }else{
            Log.d(LOG_TAG,"count null how is it possible");
        }
        return ret;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG,"OnStateInstanceState");
        super.onSaveInstanceState(outState);
        outState.putBundle("SaveBundle", (savedState != null) ? savedState : saveState());
    }
    private Bundle saveState() {
        Bundle state = new Bundle();
        state.putString("name", name);
        return state;
    }
/*

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG,"OnActivityCreated");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(LOG_TAG,"OnAttach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(LOG_TAG,"OnDestroyView");
        savedState = saveState();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(LOG_TAG,"OnDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG,"OnDestroy");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(LOG_TAG,"OnViewCreated");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG,"OnPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG,"OnResume");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(LOG_TAG,"OnViewStateRestored");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG,"OnStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG,"OnStart");
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        Log.d(LOG_TAG,"OnInflate");
    }
*/

}
