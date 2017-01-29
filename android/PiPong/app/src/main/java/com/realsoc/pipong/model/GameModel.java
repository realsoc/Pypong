package com.realsoc.pipong.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.realsoc.pipong.data.DataContract.GameEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Created by Hugo on 14/01/2017.
 */

public class GameModel implements Parcelable {
    private long id = -1;
    private int type;
    private boolean isOnline;
    private String player1;
    private String player2;
    private int sPlayer1;
    private int sPlayer2;
    private String date;
    private final DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");

    public GameModel(){

    }
    //{"_id":String,
    // "player1":String,
    // "player2":String,
    // "sPlayer1":Integer,
    // "sPlayer2":Integer,
    // "date":String ex :"2016-11-17",
    // "type":Integer,
    // "__v":Integer}

    //TODO
    public GameModel(JSONObject gameObj, HashMap<String,PlayerModel> players){
        try {
            id = gameObj.getLong(GameEntry.COLUMN_ID);
            type = gameObj.getInt(GameEntry.COLUMN_TYPE);
            isOnline = gameObj.getInt(GameEntry.COLUMN_IS_ONLINE) == 1;
            player1 = gameObj.getString(GameEntry.COLUMN_PLAYER1_NAME);
            player2 = gameObj.getString(GameEntry.COLUMN_PLAYER2_NAME);
            sPlayer1 = gameObj.getInt(GameEntry.COLUMN_PLAYER1_SCORE);
            sPlayer2 = gameObj.getInt(GameEntry.COLUMN_PLAYER2_SCORE);
            date = gameObj.getString(GameEntry.COLUMN_DATE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    protected GameModel(Parcel in) {
        id = in.readLong();
        type =  in.readInt();
        isOnline = in.readInt() == 1;
        player1 = in.readString();
        player2 = in.readString();
        sPlayer1 = in.readInt();
        sPlayer2 = in.readInt();
        date =  in.readString();
    }

    public GameModel(long id, int type, boolean isOnline, String player1, String player2, int sPlayer1, int sPlayer2, String date) {
        this.id = id;
        this.type = type;
        this.isOnline = isOnline;
        this.player1 = player1;
        this.player2 = player2;
        this.sPlayer1 = sPlayer1;
        this.sPlayer2 = sPlayer2;
        this.date = date;
    }

    public static final Creator<GameModel> CREATOR = new Creator<GameModel>() {
        @Override
        public GameModel createFromParcel(Parcel in) {
            return new GameModel(in);
        }

        @Override
        public GameModel[] newArray(int size) {
            return new GameModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(type);
        dest.writeInt(isOnline?1:0);
        dest.writeString(player1);
        dest.writeString(player2);
        dest.writeInt(sPlayer1);
        dest.writeInt(sPlayer2);
        dest.writeString(date);
    }

    public String getPlayer1() {
        return player1;
    }
    public String getPlayer2() {
        return player2;
    }
    public int getType() {
        return type;
    }
    public int getScorePlayer1() {
        return sPlayer1;
    }
    public int getScorePlayer2() {
        return sPlayer2;
    }
    public long getId() {
        return id;
    }
    public boolean isOnline(){
        return isOnline;
    }
    public String getDate() {
        return date;
    }


    public void setId(long id) {
        this.id = id;
    }
    public void setOnline(boolean online){
        this.isOnline = online;
    }
    public void setScorePlayer1(int SPlayer1) {
        this.sPlayer1 = SPlayer1;
    }
    public void setScorePlayer2(int SPlayer2) {
        this.sPlayer2 = SPlayer2;
    }
    public void setType(int type) {
        this.type = type;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setPlayer1(String player1) {
        this.player1 = player1;
    }
    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

}
