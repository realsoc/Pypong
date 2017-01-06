package com.realsoc.pipong;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Hugo on 20/11/2016.
 */

public class GameActivity extends AppCompatActivity{

    private String player1;
    private String player2;
    private int type;
    private int scoreP1 = 0;
    private int scoreP2 = 0;
    private TextView scoreP1TextView;
    private TextView scoreP2TextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!= null){
            scoreP1 = savedInstanceState.getInt("scoreP1");
            scoreP2 = savedInstanceState.getInt("scoreP2");
        }
        setContentView(R.layout.activity_game);
        Bundle extras = getIntent().getExtras();
        this.player1 = extras.getString("player1");
        this.player2 = extras.getString("player2");
        this.type = extras.getInt("type");
        scoreP1TextView = (TextView) findViewById(R.id.player1_score);
        scoreP2TextView = (TextView) findViewById(R.id.player2_score);
        scoreP1TextView.setText(Integer.toString(scoreP1));
        scoreP2TextView.setText(Integer.toString(scoreP2));
        ((TextView) findViewById(R.id.player1_name)).setText(player1);
        ((TextView) findViewById(R.id.player2_name)).setText(player2);
    }
    public void player1Plus(View v){
        scoreP1++;
        scoreP1TextView.setText(Integer.toString(scoreP1));
    }
    public void player2Plus(View v){
        scoreP2++;
        scoreP2TextView.setText(Integer.toString(scoreP2));
    }
    public void player1Less(View v){
        if(scoreP1>0){
            scoreP1--;
            scoreP1TextView.setText(Integer.toString(scoreP1));
        }
    }
    public void player2Less(View v){
        if(scoreP2>0){
            scoreP2--;
            scoreP2TextView.setText(Integer.toString(scoreP2));
        }
    }
    public void sendData(View v){
       //TODO : send game data to server
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt("scoreP1",scoreP1);
        outState.putInt("scoreP2",scoreP2);
        super.onSaveInstanceState(outState);
    }
}
