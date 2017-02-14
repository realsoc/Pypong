package com.realsoc.pipong.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.realsoc.pipong.data.DataContract.PlayerEntry;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Hugo on 14/01/2017.
 */

public class PlayerModel implements Parcelable{
    private long id = -1;
    private String name;
    private String user = "0";
    private boolean isOnline;
    private boolean conflict = false;

    protected PlayerModel(Parcel in) {
        id = in.readLong();
        name = in.readString();
        user = in.readString();
        isOnline = in.readInt() == 1;
        conflict = in.readInt() == 1;
    }
    public JSONObject toJSON(){
        JSONObject ret = new JSONObject();
        try{
            if(id!=-1)
                ret.put(PlayerEntry.COLUMN_ID,id);
            if(!name.equals(""))
                ret.put(PlayerEntry.COLUMN_PLAYER_NAME,name);
            ret.put(PlayerEntry.COLUMN_USER,user);
            ret.put(PlayerEntry.COLUMN_IS_ONLINE,isOnline);
            ret.put(PlayerEntry.COLUMN_CONFLICT,conflict);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return ret;
    }
    public PlayerModel(JSONObject obj){
        try {
            if(obj.has(PlayerEntry.COLUMN_ID))
                id = obj.getLong(PlayerEntry.COLUMN_ID);
            if(obj.has(PlayerEntry.COLUMN_PLAYER_NAME))
                name = obj.getString(PlayerEntry.COLUMN_PLAYER_NAME);
            if(obj.has(PlayerEntry.COLUMN_IS_ONLINE))
                isOnline = obj.getBoolean(PlayerEntry.COLUMN_IS_ONLINE);
            if(obj.has(PlayerEntry.COLUMN_CONFLICT))
                conflict = obj.getBoolean(PlayerEntry.COLUMN_CONFLICT);
            if(obj.has(PlayerEntry.COLUMN_USER))
                user = obj.getString(PlayerEntry.COLUMN_USER);
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

    public PlayerModel(long id, String name,String user, boolean online) {
        this.id = id;
        this.name = name;
        this.user = user;
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
        dest.writeString(user);
        dest.writeInt(isOnline ? 1:0);
        dest.writeInt(conflict ? 1:0);
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
    public void setConflict(boolean conflict){
        this.conflict = conflict;
    }
    public boolean isConflict(){
        return conflict;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }
    public void setId(long mId) {
        this.id = mId;
    }
}
