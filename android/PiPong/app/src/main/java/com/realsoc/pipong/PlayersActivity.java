package com.realsoc.pipong;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * Created by Hugo on 03/01/2017.
 */

public class PlayersActivity extends AppCompatActivity {
    private ArrayList<PlayerModel> players;
    private ArrayList<GameModel> games;
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private PlayerListFragment playerListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);
        Bundle extras = getIntent().getExtras();
        if(savedInstanceState != null){
            this.players = savedInstanceState.getParcelableArrayList("players");
            this.games = savedInstanceState.getParcelableArrayList("games");
            Log.d("DEBUG",Integer.toString(this.players.size()));
        }else{
            this.players = extras.getParcelableArrayList("players");
            this.games = extras.getParcelableArrayList("games");
            Log.d("oncreate PA","size players"+players.size()+" size games "+games.size());
        }
        playerListFragment = PlayerListFragment.newInstance(players,games, client);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.players_frame_container, playerListFragment);
        //transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
/*

        mRecyclerView = (RecyclerView) findViewById(R.id.player_recyclerview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(this.players);
        mRecyclerView.setAdapter(mAdapter);*/
    }/*
    public void addPlayer(View v){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlayersActivity.this);
        alertDialog.setTitle("Player");
        alertDialog.setMessage("New Players' Name");

        final EditText input = new EditText(PlayersActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setPositiveButton("Send",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String nPlayerName = input.getText().toString();
                        players.add(nPlayerName);
                        String req = "{\"name\":\""+nPlayerName+"\"}";
                        Log.d(PLAYERS_ACTIVITY_NAME,req);
                        RequestBody body = RequestBody.create(JSON,req);
                        Request request = new Request.Builder()
                                .url(REMOTE_SERVER_ADDRESS+"/api/players")
                                .post(body)
                                .build();
                        Log.d(PLAYERS_ACTIVITY_NAME,body.toString());
                            Call call = client.newCall(request);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onResponse(Call call, final Response response) throws IOException {
                                    Log.d(PLAYERS_ACTIVITY_NAME,response.body().string());
                                    /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });*//*
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
    }*/
    public void addPlayer(View v){
        playerListFragment.addPlayer();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("players",this.players);
        outState.putParcelableArrayList("games",this.games);
        super.onSaveInstanceState(outState);

    }
    /*
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<String> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            private TextView mTextView;
            private ImageView avatar;
            public ViewHolder(View v) {
                super(v);
                mTextView = (TextView) v.findViewById(R.id.name_line);
                avatar = (ImageView) v.findViewById(R.id.avatar);
            }
            public String getText(){
                return mTextView.getText().toString();
            }
            public Void setText(String text){
                mTextView.setText(text);
                return null;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(ArrayList<String> myDataset) {
            mDataset = myDataset;
        }
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.playerlist_item, parent, false);
            // set the view's size, margins, paddings and layout parameters

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.setText(mDataset.get(position));

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
    */
}
