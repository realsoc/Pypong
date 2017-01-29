package com.realsoc.pipong.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Hugo on 14/01/2017.
 */

public class PlayerModel implements Parcelable{
    private long id = -1;
    private String name;
    private boolean isOnline;

    protected PlayerModel(Parcel in) {
        id = in.readLong();
        name = in.readString();
        isOnline = in.readInt() == 1;
    }
    //TODO
    public PlayerModel(JSONObject obj){
        try {
            name = obj.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public PlayerModel(String name){
        this.name = name;
    }

    public PlayerModel(String playerName, boolean isOnline) {
        this.name = playerName;
        this.isOnline = isOnline;
    }

    public PlayerModel(long id, String name, boolean online) {
        this.id = id;
        this.name = name;
        this.isOnline = online;
    }

    public static final Creator<PlayerModel> CREATOR = new Creator<PlayerModel>() {
        @Override
        public PlayerModel createFromParcel(Parcel in) {
            return new PlayerModel(in);
        }

        @Override
        public PlayerModel[] newArray(int size) {
            return new PlayerModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(isOnline ? 1:0);
    }
    public void setName(String nName){
        this.name = nName;
    }
    public String getName() {
        return name;
    }

    public void setOnline(boolean online) {
        this.isOnline = online;
    }
    public boolean isOnline(){
        return isOnline;
    }


    public long getId() {
        return id;
    }
    public void setId(long mId) {
        this.id = mId;
    }
}
