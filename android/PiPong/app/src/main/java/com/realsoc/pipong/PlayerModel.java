package com.realsoc.pipong;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Hugo on 14/01/2017.
 */

public class PlayerModel implements Parcelable{
    private String name;
    private HashMap<String,Integer> gameCount = new HashMap<>();
    protected PlayerModel(Parcel in) {
        name = in.readString();
        Serializable hashmap = in.readSerializable();
        if(hashmap instanceof HashMap)
            gameCount = (HashMap<String,Integer>)hashmap;
    }
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
    public void setGameCount(JSONObject obj){
        Iterator<String> keys = obj.keys();
        String tmp;
        while(keys.hasNext()){
            tmp = keys.next();
            try {
                gameCount.put(tmp,obj.getInt(tmp));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
        dest.writeString(name);
        dest.writeSerializable(gameCount);
    }

    public String getName() {
        return name;
    }
}
