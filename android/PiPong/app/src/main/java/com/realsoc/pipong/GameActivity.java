package com.realsoc.pipong;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.realsoc.pipong.data.DataContract;
import com.realsoc.pipong.data.DataContract.GameEntry;
import com.realsoc.pipong.model.GameModel;
import com.realsoc.pipong.utils.DataUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Hugo on 20/11/2016.
 */

public class GameActivity extends AppCompatActivity{
    private static final String LOG_TAG = "GAME_ACTIVITY";
    private String player1;
    private String player2;
    private int type;
    private int firstServe;
    private int scoreP1 = 0;
    private int scoreP2 = 0;
    private ImageView serveP1, serveP2;
    private TextView scoreP1TextView;
    private TextView scoreP2TextView;
    private DataUtils dataUtils;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataUtils = DataUtils.getInstance(getApplicationContext());
        Bundle extras = getIntent().getExtras();
        if(savedInstanceState!= null){
            scoreP1 = savedInstanceState.getInt(GameEntry.COLUMN_PLAYER1_SCORE);
            scoreP2 = savedInstanceState.getInt(GameEntry.COLUMN_PLAYER2_SCORE);
            type = savedInstanceState.getInt(GameEntry.COLUMN_TYPE);
        }else{
            type = extras.getInt(DataContract.GameEntry.COLUMN_TYPE);
        }
        setContentView(R.layout.activity_game);
        player1 = extras.getString(GameEntry.COLUMN_PLAYER1_NAME);
        player2 = extras.getString(GameEntry.COLUMN_PLAYER2_NAME);
        firstServe = extras.getInt("serve");
        serveP1 = (ImageView) findViewById(R.id.serveP1);
        serveP2 = (ImageView) findViewById(R.id.serveP2);
        updateServeShow();
        scoreP1TextView = (TextView) findViewById(R.id.player1_score);
        scoreP2TextView = (TextView) findViewById(R.id.player2_score);
        scoreP1TextView.setText(String.format(Locale.getDefault(),"%d",scoreP1));
        scoreP2TextView.setText(String.format(Locale.getDefault(),"%d",scoreP2));
        ((TextView) findViewById(R.id.player1_name)).setText(player1);
        ((TextView) findViewById(R.id.player2_name)).setText(player2);
    }

    private void updateServeShow() {
        if(type == 6 || type == 11){
            if((scoreP1+scoreP2)%4<2){
                if(firstServe == 0){
                    serveP1.setVisibility(View.VISIBLE);
                    serveP2.setVisibility(View.INVISIBLE);
                }else{
                    serveP1.setVisibility(View.INVISIBLE);
                    serveP2.setVisibility(View.VISIBLE);
                }
            }else{
                if(firstServe == 1){
                    serveP1.setVisibility(View.VISIBLE);
                    serveP2.setVisibility(View.INVISIBLE);
                }else{
                    serveP1.setVisibility(View.INVISIBLE);
                    serveP2.setVisibility(View.VISIBLE);
                }
            }
        }else{
            if((scoreP1+scoreP2)%10<5){
                if(firstServe == 0){
                    serveP1.setVisibility(View.VISIBLE);
                    serveP2.setVisibility(View.INVISIBLE);
                }else{
                    serveP1.setVisibility(View.INVISIBLE);
                    serveP2.setVisibility(View.VISIBLE);
                }
            }else{
                if(firstServe == 1){
                    serveP1.setVisibility(View.VISIBLE);
                    serveP2.setVisibility(View.INVISIBLE);
                }else{
                    serveP1.setVisibility(View.INVISIBLE);
                    serveP2.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    private void testEndGame() {
        if((scoreP1 >= type && scoreP1>scoreP2+1)||(scoreP2>=type && scoreP2>scoreP1+1)){
            showDialog();
        }
    }

    private void showDialog() {
        final int nextType = (type == 6) ? 11: 21;
        String passToNext = "Continue to "+Integer.toString(nextType);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogView = factory.inflate(R.layout.autosend_dialog, null);
        alert.setView(dialogView);
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.cancel();
            }
        });
        if(type == 6 ||type == 11){
            alert.setNegativeButton(passToNext,
                    new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            type = nextType;
                            dialog.cancel();
                        }
                    });
        }

        alert.setPositiveButton("Game finished",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveGameAndQuit();
                        dialog.cancel();
                    }
                });
        alert.create().show();
    }

    public void player1Plus(View v){
        scoreP1++;
        testEndGame();
        updateServeShow();
        scoreP1TextView.setText(String.format(Locale.getDefault(),"%d",scoreP1));
    }

    public void player2Plus(View v){
        scoreP2++;
        testEndGame();
        updateServeShow();
        scoreP2TextView.setText(String.format(Locale.getDefault(),"%d",scoreP2));
    }
    public void player1Less(View v){
        if(scoreP1>0){
            scoreP1--;
            updateServeShow();
            scoreP1TextView.setText(String.format(Locale.getDefault(),"%d",scoreP1));
        }
    }
    public void player2Less(View v){
        if(scoreP2>0){
            scoreP2--;
            updateServeShow();
            scoreP2TextView.setText(String.format(Locale.getDefault(),"%d",scoreP2));
        }
    }
    private void saveGameAndQuit(){
        saveGame();
        quit();
    }
    private void saveGame(){
        GameModel newGame = new GameModel();
        newGame.setPlayer1(player1);
        newGame.setPlayer2(player2);
        newGame.setType(type);
        newGame.setScorePlayer1(scoreP1);
        newGame.setScorePlayer2(scoreP2);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        newGame.setDate(dateFormat.format(new Date()));
        dataUtils.addGame(newGame);
    }
    public void sendData(View v){
        saveGameAndQuit();
    }
    private void quit(){
        Intent intent = new Intent ( getApplicationContext() , MainActivity.class );
        intent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity ( intent );
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(GameEntry.COLUMN_PLAYER1_SCORE,scoreP1);
        outState.putInt(GameEntry.COLUMN_PLAYER2_SCORE,scoreP2);
        outState.putInt(GameEntry.COLUMN_TYPE,type);
        super.onSaveInstanceState(outState);
    }
}
