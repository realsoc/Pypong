package com.realsoc.pipong;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.realsoc.pipong.data.DataContract.GameEntry;
import com.realsoc.pipong.utils.DataUtils;

import java.util.ArrayList;
import java.util.Random;

import static com.realsoc.pipong.utils.Constants.SELECT_DIFFERENT_PLAYER;

/**
 * Created by Hugo on 20/11/2016.
 */

public class ConfigurationActivity extends AppCompatActivity {
    private static final String LOG_TAG = "CONFIGURATION_ACTIVITY";
    private final ArrayList<Integer> gameType = new ArrayList<Integer>();
    private RadioButton p1, p2, random;
    private ArrayList<String> playersStrArrayList;

    public void initGameType(){
        gameType.add(6);
        gameType.add(11);
        gameType.add(21);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGameType();
        DataUtils dataUtils = DataUtils.getInstance(this);
        playersStrArrayList = dataUtils.getPlayerAsStringList();
        setContentView(R.layout.activity_configuration);
        initView();
    }
    private void initView(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, playersStrArrayList);
        ArrayAdapter<Integer> adapterType = new ArrayAdapter<Integer>(
                this, android.R.layout.simple_spinner_dropdown_item, gameType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner player1 = (Spinner) findViewById(R.id.player1_spinner);
        Spinner player2 = (Spinner) findViewById(R.id.player2_spinner);
        p1 = (RadioButton) findViewById(R.id.p1_serve_radio);
        p2 = (RadioButton) findViewById(R.id.p2_serve_radio);
        random = (RadioButton) findViewById(R.id.random_serve_radio);
        random.setChecked(true);
        p1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p1.setChecked(true);
                p2.setChecked(false);
                random.setChecked(false);
            }
        });
        p2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p1.setChecked(false);
                p2.setChecked(true);
                random.setChecked(false);
            }
        });
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p1.setChecked(false);
                p2.setChecked(false);
                random.setChecked(true);
            }
        });
        Spinner type = (Spinner) findViewById(R.id.type_spinner);
        player1.setAdapter(adapter);
        player2.setAdapter(adapter);
        type.setAdapter(adapterType);
    }
    public int getServe() {
        int ret;
        Random r = new Random();
        if(p1.isChecked()){
            ret = 0;
        }else if(p2.isChecked()){
            ret = 1;
        }else{
            ret = Math.abs(r.nextInt())%2;
        }
        return ret;
    }
    public void startGame(View v){
        Intent gameIntent = new Intent(this, GameActivity.class);
        String  player1 = ((Spinner) findViewById(R.id.player1_spinner)).getSelectedItem().toString();
        String player2 = ((Spinner) findViewById(R.id.player2_spinner)).getSelectedItem().toString();
        int type = (int)((Spinner) findViewById(R.id.type_spinner)).getSelectedItem();
        int serve = getServe();
        if(!player1.equals(player2)){
            Bundle mBundle = new Bundle();
            mBundle.putString(GameEntry.COLUMN_PLAYER1_NAME,player1);
            mBundle.putString(GameEntry.COLUMN_PLAYER2_NAME,player2);
            mBundle.putInt(GameEntry.COLUMN_TYPE,type);
            mBundle.putInt("serve",serve);
            gameIntent.putExtras(mBundle);
            startActivity(gameIntent);
        }else{
            Toast.makeText(this,SELECT_DIFFERENT_PLAYER,Toast.LENGTH_SHORT).show();
        }
    }


}
