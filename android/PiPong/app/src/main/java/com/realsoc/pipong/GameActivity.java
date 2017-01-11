package com.realsoc.pipong;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
    private Dialog sendDialog;
    private int countdownSendData =5;
    private TextView autoSendMessage;
    private boolean autoSend = true;
    private boolean exit = false;
    private String player1;
    private String player2;
    private int type;
    private int firstServe;
    private int scoreP1 = 0;
    private int scoreP2 = 0;
    private ImageView serveP1, serveP2;
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
        this.firstServe = extras.getInt("serve");
        serveP1 = (ImageView) findViewById(R.id.serveP1);
        serveP2 = (ImageView) findViewById(R.id.serveP2);
        updateServeShow();
        scoreP1TextView = (TextView) findViewById(R.id.player1_score);
        scoreP2TextView = (TextView) findViewById(R.id.player2_score);
        scoreP1TextView.setText(Integer.toString(scoreP1));
        scoreP2TextView.setText(Integer.toString(scoreP2));
        ((TextView) findViewById(R.id.player1_name)).setText(player1);
        ((TextView) findViewById(R.id.player2_name)).setText(player2);
        //sendAndShowDialog();
    }

    private void updateServeShow() {
        if(type == 6|| type == 11){
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
        if(autoSend == true){
            if((scoreP1 >= type && scoreP1>scoreP2+1)||(scoreP2>=type && scoreP2>scoreP1+1)){
                sendAndShowDialog();
            }
        }
    }

    private void sendAndShowDialog() {
        final int nextType = (type == 6) ? 11: 21;
        String passToNext = "Continue to "+Integer.toString(nextType);
        String test = "Hello";
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogView = factory.inflate(R.layout.autosend_dialog, null);
        autoSendMessage=(TextView)dialogView.findViewById(R.id.autosend_dialog_message);
        //alert.setMessage(test);
        alert.setView(dialogView);
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                exit = true;
                dialog.cancel();
            }
        });
        if(type == 6 ||type == 11){
            alert.setPositiveButton(passToNext,
                    new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            exit = true;
                            type = nextType;
                            dialog.cancel();
                        }
                    });
        }

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        autoSend = false;
                        dialog.cancel();
                    }
                });
        sendDialog = alert.create();
        sendDialog.show();
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (autoSend&&!exit) {
                        Log.d("THERE","BITCH + "+countdownSendData);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(countdownSendData == 0){
                                    autoSend = false;
                                    sendData(null);
                                }else{
                                    //alert.setMessage("Sending datas in ("+countdownSendData+")...");
                                    autoSendMessage.setText("Sending datas in ("+countdownSendData+")...");
                                    countdownSendData--;
                                }
                            }
                        });
                        Thread.sleep(1000);
                    }
                    countdownSendData = 5;
                    exit = false;
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    public void player1Plus(View v){
        scoreP1++;
        testEndGame();
        updateServeShow();
        scoreP1TextView.setText(Integer.toString(scoreP1));
    }

    public void player2Plus(View v){
        scoreP2++;
        testEndGame();
        updateServeShow();
        scoreP2TextView.setText(Integer.toString(scoreP2));
    }
    public void player1Less(View v){
        if(scoreP1>0){
            scoreP1--;
            updateServeShow();
            scoreP1TextView.setText(Integer.toString(scoreP1));
        }
    }
    public void player2Less(View v){
        if(scoreP2>0){
            scoreP2--;
            updateServeShow();
            scoreP2TextView.setText(Integer.toString(scoreP2));
        }
    }
    public void sendToServer(){
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
    public void sendData(View v){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GameActivity.this);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("You really want to persist datas");

        alertDialog.setPositiveButton("Send",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       sendToServer();
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        if(v == null){
            sendToServer();
        }else{
            alertDialog.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(sendDialog != null){
            sendDialog.dismiss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt("scoreP1",scoreP1);
        outState.putInt("scoreP2",scoreP2);
        super.onSaveInstanceState(outState);
    }
}
