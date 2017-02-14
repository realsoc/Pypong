package com.realsoc.pipong.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.realsoc.pipong.data.DataContract.GameEntry;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Hugo on 14/01/2017.
 */

public class GameModel implements Parcelable {
    private long id = -1;
    private int type;
    private boolean isOnline;
    private long player1_id = -1;
    private long player2_id = -1;
    private String player1_name;
    private String player2_name;
    private String user = "0";
    private int player1_score;
    private int player2_score;
    private long date;

    public GameModel(){

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getPlayer1_id() {
        return player1_id;
    }

    public void setPlayer1_id(long player1_id) {
        this.player1_id = player1_id;
    }

    public long getPlayer2_id() {
        return player2_id;
    }

    public void setPlayer2_id(long player2_id) {
        this.player2_id = player2_id;
    }

    public JSONObject toJSON(){
        JSONObject ret = new JSONObject();
        try {
            if (id != -1)
                ret.put(GameEntry.COLUMN_ID, id);
            if (type == 6 || type == 11 || type == 21)
                ret.put(GameEntry.COLUMN_TYPE, type);
            ret.put(GameEntry.COLUMN_IS_ONLINE, isOnline);
            if (!player1_name.equals(""))
                ret.put(GameEntry.COLUMN_PLAYER1_NAME, player1_name);
            if (!player2_name.equals(""))
                ret.put(GameEntry.COLUMN_PLAYER2_NAME, player2_name);
            ret.put(GameEntry.COLUMN_PLAYER1_SCORE,player1_score);
            ret.put(GameEntry.COLUMN_PLAYER2_SCORE,player2_score);
            ret.put(GameEntry.COLUMN_DATE, date);
            ret.put(GameEntry.COLUMN_USER,user);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return ret;
    }
    public GameModel(JSONObject gameObj){
        try {
            if(gameObj.has(GameEntry.COLUMN_ID))
                id = gameObj.getLong(GameEntry.COLUMN_ID);
            if(gameObj.has(GameEntry.COLUMN_TYPE))
                type = gameObj.getInt(GameEntry.COLUMN_TYPE);
            if(gameObj.has(GameEntry.COLUMN_IS_ONLINE))
                isOnline = gameObj.getBoolean(GameEntry.COLUMN_IS_ONLINE);
            if(gameObj.has(GameEntry.COLUMN_PLAYER1_NAME))
                player1_name = gameObj.getString(GameEntry.COLUMN_PLAYER1_NAME);
            if(gameObj.has(GameEntry.COLUMN_PLAYER2_NAME))
                player2_name = gameObj.getString(GameEntry.COLUMN_PLAYER2_NAME);
            if(gameObj.has(GameEntry.COLUMN_PLAYER1_SCORE))
                player1_score = gameObj.getInt(GameEntry.COLUMN_PLAYER1_SCORE);
            if(gameObj.has(GameEntry.COLUMN_PLAYER2_SCORE))
                player2_score = gameObj.getInt(GameEntry.COLUMN_PLAYER2_SCORE);
            if(gameObj.has(GameEntry.COLUMN_USER))
                user = gameObj.getString(GameEntry.COLUMN_USER);
            if(gameObj.has(GameEntry.COLUMN_DATE))
                date = gameObj.getLong(GameEntry.COLUMN_DATE);
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {

        }
    }
    protected GameModel(Parcel in) {
        id = in.readLong();
        type =  in.readInt();
        isOnline = in.readInt() == 1;
        player1_id = in.readLong();
        player2_id = in.readLong();
        player1_name = in.readString();
        player2_name = in.readString();
        user = in.readString();
        player1_score = in.readInt();
        player2_score = in.readInt();
        date =  in.readLong();
    }

    public GameModel(long id, int type, boolean isOnline, long player1_id,long player2_id,String player1, String player2_name,String user, int player1_score, int player2_score, long date) {
        this.id = id;
        this.type = type;
        this.isOnline = isOnline;
        this.player1_id = player1_id;
        this.player2_id = player2_id;
        this.player1_name = player1;
        this.player2_name = player2_name;
        this.user = user;
        this.player1_score = player1_score;
        this.player2_score = player2_score;
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
        dest.writeLong(player1_id);
        dest.writeLong(player2_id);
        dest.writeString(player1_name);
        dest.writeString(player2_name);
        dest.writeString(user);
        dest.writeInt(player1_score);
        dest.writeInt(player2_score);
        dest.writeLong(date);
    }

    public String getPlayer1() {
        return player1_name;
    }
    public String getPlayer2() {
        return player2_name;
    }
    public int getType() {
        return type;
    }
    public int getScorePlayer1() {
        return player1_score;
    }
    public int getScorePlayer2() {
        return player2_score;
    }
    public long getId() {
        return id;
    }
    public boolean isOnline(){
        return isOnline;
    }
    public long getDate() {
        return date;
    }


    public void setId(long id) {
        this.id = id;
    }
    public void setOnline(boolean online){
        this.isOnline = online;
    }
    public void setScorePlayer1(int SPlayer1) {
        this.player1_score = SPlayer1;
    }
    public void setScorePlayer2(int SPlayer2) {
        this.player2_score = SPlayer2;
    }
    public void setType(int type) {
        this.type = type;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public void setPlayer1(String player1) {
        this.player1_name = player1;
    }
    public void setPlayer2_name(String player2_name) {
        this.player2_name = player2_name;
    }

}
