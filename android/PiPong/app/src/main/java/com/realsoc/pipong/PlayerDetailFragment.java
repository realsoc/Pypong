package com.realsoc.pipong;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Hugo on 15/01/2017.
 */

public class PlayerDetailFragment extends Fragment {
    private PlayerModel mPlayer;
    private Bundle savedState;
    public PlayerDetailFragment(){

    }
    public static PlayerDetailFragment newInstance(PlayerModel nPlayer){
        Log.d("PDF", "NewInstance");
        PlayerDetailFragment eu = new PlayerDetailFragment();
        eu.setPlayer(nPlayer);
        return eu;
    }

    public void setPlayer(PlayerModel player) {
        this.mPlayer = player;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("PDF","OnCreate");
        if(savedInstanceState != null || savedState != null){
            if(savedState == null){
                if(savedInstanceState.containsKey("SaveBundle"));
                savedState = savedInstanceState.getBundle("SaveBundle");
            }
            if(savedState == null)
                throw new AssertionError("savedState can't be null");
            if(mPlayer == null){
                if(savedState.containsKey("player")){
                    mPlayer = savedState.getParcelable("player");
                }
            }
        }
        if(mPlayer == null)
            Log.d("PDF","OC : player null");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("PDF","OnActivityCreated");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("PDF","OnAttach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("PDF","OnDestroyView");
        savedState = saveState();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("PDF","OnDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("PDF","OnDestroy");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("PDF","OnViewCreated");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("PDF","OnPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("PDF","OnResume");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d("PDF","OnViewStateRestored");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("PDF","OnStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("PDF","OnStart");
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        Log.d("PDF","OnInflate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("PDF","OnCreateView");
        if(savedInstanceState != null){
            /*mPlayer = savedInstanceState.getParcelable("player");*/
        }
        Log.d("PDF",""+mPlayer);
        View ret = inflater.inflate(R.layout.fragment_player_details,container,false);
        ((TextView) ret.findViewById(R.id.player_name)).setText(mPlayer.getName());
        if(mPlayer.isInitialized()){
            ((TextView) ret.findViewById(R.id.pl6)).setText(mPlayer.getCountByKey("6PL"));
            ((TextView) ret.findViewById(R.id.pl11)).setText(mPlayer.getCountByKey("11PL"));
            ((TextView) ret.findViewById(R.id.pl21)).setText(mPlayer.getCountByKey("21PL"));
            ((TextView) ret.findViewById(R.id.pl0)).setText(mPlayer.getCountByKey("0PL"));
            ((TextView) ret.findViewById(R.id.ps6)).setText(mPlayer.getCountByKey("6PS"));
            ((TextView) ret.findViewById(R.id.ps11)).setText(mPlayer.getCountByKey("11PS"));
            ((TextView) ret.findViewById(R.id.ps21)).setText(mPlayer.getCountByKey("21PS"));
            ((TextView) ret.findViewById(R.id.ps0)).setText(mPlayer.getCountByKey("0PS"));
            ((TextView) ret.findViewById(R.id.gl6)).setText(mPlayer.getCountByKey("6GL"));
            ((TextView) ret.findViewById(R.id.gl11)).setText(mPlayer.getCountByKey("11GL"));
            ((TextView) ret.findViewById(R.id.gl21)).setText(mPlayer.getCountByKey("21GL"));
            ((TextView) ret.findViewById(R.id.gl0)).setText(mPlayer.getCountByKey("0GL"));
            ((TextView) ret.findViewById(R.id.gw6)).setText(mPlayer.getCountByKey("6GW"));
            ((TextView) ret.findViewById(R.id.gw11)).setText(mPlayer.getCountByKey("11GW"));
            ((TextView) ret.findViewById(R.id.gw21)).setText(mPlayer.getCountByKey("21GW"));
            ((TextView) ret.findViewById(R.id.gw0)).setText(mPlayer.getCountByKey("0GW"));
            ((TextView) ret.findViewById(R.id.gp6)).setText(mPlayer.getCountByKey("6GP"));
            ((TextView) ret.findViewById(R.id.gp11)).setText(mPlayer.getCountByKey("11GP"));
            ((TextView) ret.findViewById(R.id.gp21)).setText(mPlayer.getCountByKey("21GP"));
            ((TextView) ret.findViewById(R.id.gp0)).setText(mPlayer.getCountByKey("0GP"));
        }
        return ret;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("PDF","OnStateInstanceState");
        super.onSaveInstanceState(outState);
        outState.putBundle("SaveBundle", (savedState != null) ? savedState : saveState());
    }
    private Bundle saveState() { /* called either from onDestroyView() or onSaveInstanceState() */
        Bundle state = new Bundle();
        state.putParcelable("player", mPlayer);
        return state;
    }

}
