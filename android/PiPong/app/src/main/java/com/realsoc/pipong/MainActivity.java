package com.realsoc.pipong;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.realsoc.pipong.Constants.CONFIGURATION_ACTIVITY_ID;
import static com.realsoc.pipong.Constants.GAME_ACTIVITY_ID;
import static com.realsoc.pipong.Constants.PLAYERS_ACTIVITY_ID;

public class MainActivity extends AppCompatActivity {

    public void addPlayer(View v){
        new GetPlayersAndGoTask(this,PLAYERS_ACTIVITY_ID).execute("http://192.168.1.82:8080/api/players");
    }

    public void newGame(View v){
        new GetPlayersAndGoTask(this,GAME_ACTIVITY_ID).execute("http://192.168.1.82:8080/api/players");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    private class GetPlayersAndGoTask extends AsyncTask<String, Integer, String> {
        private Context context;
        private int id;
        private String ret = "";
        public GetPlayersAndGoTask(Context context, int id){
            this.context = context;
            this.id = id;
        }
        protected String doInBackground(String... urls) {
            String url = urls[0];
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                ret = response.body().string();            } catch (IOException e) {
                e.printStackTrace();
            }
            return ret;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }
        private ArrayList<String> createPlayerObjFromString(String strObj){
            ArrayList<String> ret= new ArrayList<String>();
            JSONArray players = null;
            try {
                players = new JSONArray(strObj);
                for(int i=0;i<players.length();i++){
                    ret.add(players.getJSONObject(i).getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return ret;
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Intent activityIntent;
            switch (id){
                case PLAYERS_ACTIVITY_ID:
                    activityIntent = new Intent(context,PlayersActivity.class);
                    break;
                default:
                case CONFIGURATION_ACTIVITY_ID:
                    activityIntent = new Intent(context,ConfigurationActivity.class);
                    break;
            }
            ArrayList<String> players = createPlayerObjFromString(result);
            Bundle mBundle = new Bundle();
            mBundle.putStringArrayList("players",players);
            activityIntent.putExtras(mBundle);
            startActivity(activityIntent);
            //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
            //showDialog("Downloaded " + result + " bytes");
        }
    }
}
