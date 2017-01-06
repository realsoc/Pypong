package com.realsoc.pipong;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.realsoc.pipong.Constants.GAME_ACTIVITY_NAME;
import static com.realsoc.pipong.Constants.PLAYERS_ACTIVITY_NAME;
import static com.realsoc.pipong.Constants.REMOTE_SERVER_ADDRESS;

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

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();

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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GameActivity.this);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("You really want to persist datas");

        alertDialog.setPositiveButton("Send",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String req = "{\"player1\":\""+player1+"\",\"player2\":\""+player2+"\",\"sPlayer1\":"+scoreP1+",\"sPlayer2\":"+scoreP2+",\"type\":"+type+"}";
                        Log.d(PLAYERS_ACTIVITY_NAME,req);
                        RequestBody body = RequestBody.create(JSON,req);
                        Request request = new Request.Builder()
                                .url(REMOTE_SERVER_ADDRESS+"/api/games")
                                .post(body)
                                .build();
                        Log.d(PLAYERS_ACTIVITY_NAME,body.toString());
                        Call call = client.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),"Failed to persist",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onResponse(Call call, final Response response) throws IOException {
                                Log.d(GAME_ACTIVITY_NAME,response.body().string());
                                Intent intent = new Intent ( getApplicationContext() , MainActivity.class );
                                intent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                startActivity ( intent );
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
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt("scoreP1",scoreP1);
        outState.putInt("scoreP2",scoreP2);
        super.onSaveInstanceState(outState);
    }
}
