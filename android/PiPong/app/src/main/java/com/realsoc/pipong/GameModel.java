package com.realsoc.pipong;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hugo on 14/01/2017.
 */

public class GameModel implements Parcelable {
    private String player1;
    private String player2;
    private int sPlayer1;
    private int sPlayer2;
    private Date date;
    private int type;
    private DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
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
    public GameModel(JSONObject gameObj){
        try {
            player1 = gameObj.getString("player1");
            player2 = gameObj.getString("player2");
            sPlayer1 = gameObj.getInt("sPlayer1");
            sPlayer2 = gameObj.getInt("sPlayer2");
            date = formatter.parse(gameObj.getString("date"));
            type = gameObj.getInt("type");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
    protected GameModel(Parcel in) {
        player1 = in.readString();
        player2 = in.readString();
        sPlayer1 = in.readInt();
        sPlayer2 = in.readInt();
        date = (Date) in.readSerializable();
        type =  in.readInt();
    }


    // Using the `in` variable, we can retrieve the values that
    // we originally wrote into the `Parcel`.  This constructor is usually
    // private so that only the `CREATOR` field can access.
   /* private MyParcelable(Parcel in) {
        mData = in.readInt();
        mName = in.readString();
        mInfo = in.readParcelable(MySubParcelable.class.getClassLoader());*/
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
        dest.writeString(player1);
        dest.writeString(player2);
        dest.writeInt(sPlayer1);
        dest.writeInt(sPlayer2);
        dest.writeSerializable(date);
        dest.writeInt(type);
    }
}
