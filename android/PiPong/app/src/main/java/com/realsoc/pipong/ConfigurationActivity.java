package com.realsoc.pipong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import static com.realsoc.pipong.Constants.SELECT_DIFFERENT_PLAYER;

/**
 * Created by Hugo on 20/11/2016.
 */

public class ConfigurationActivity extends AppCompatActivity {
    private ArrayList<String> players;
    private final ArrayList<Integer> gameType = new ArrayList<Integer>();
    public void initGameType(){
        gameType.add(6);
        gameType.add(11);
        gameType.add(21);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGameType();
        Bundle extras = getIntent().getExtras();
        this.players = extras.getStringArrayList("players");
        setContentView(R.layout.activity_configuration);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, players);
        ArrayAdapter<Integer> adapterType = new ArrayAdapter<Integer>(
                this, android.R.layout.simple_spinner_dropdown_item, gameType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner player1 = (Spinner) findViewById(R.id.player1_spinner);
        Spinner player2 = (Spinner) findViewById(R.id.player2_spinner);
        Spinner type = (Spinner) findViewById(R.id.type_spinner);
        player1.setAdapter(adapter);
        player2.setAdapter(adapter);
        type.setAdapter(adapterType);
    }

    public void startGame(View v){
        Intent gameIntent = new Intent(this, GameActivity.class);
        String player1 = ((Spinner) findViewById(R.id.player1_spinner)).getSelectedItem().toString();
        String player2 = ((Spinner) findViewById(R.id.player2_spinner)).getSelectedItem().toString();
        int type = (int)((Spinner) findViewById(R.id.type_spinner)).getSelectedItem();
        if(!player1.equals(player2)){
            Bundle mBundle = new Bundle();
            mBundle.putString("player1",player1);
            mBundle.putString("player2",player2);
            mBundle.putInt("type",type);
            gameIntent.putExtras(mBundle);
            startActivity(gameIntent);
        }else{
            Toast.makeText(this,SELECT_DIFFERENT_PLAYER,Toast.LENGTH_SHORT).show();
        }
    }
}
