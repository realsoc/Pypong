package com.realsoc.pipong.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by Hugo on 26/01/2017.
 */

public class CountModel implements Parcelable {
    private static final String LOG_TAG = "CountModel";
    private long id = -1;
    private String name;
    private int pointLost6 = 0;
    private int pointLost11 = 0;
    private int pointLost21 = 0;
    private int pointScored6 = 0;
    private int pointScored11 = 0;
    private int pointScored21 = 0;
    private int gameWon6 = 0;
    private int gameWon11 = 0;
    private int gameWon21 = 0;
    private int gameLost6 = 0;
    private int gameLost11 = 0;
    private int gameLost21 = 0;
    private int gamePlayed6 = 0;
    private int gamePlayed11 = 0;
    private int gamePlayed21 = 0;

    public CountModel(String name){
        this.name = name;
    }
    public CountModel(long id, String name, int pointLost6, int pointLost11, int pointLost21, int pointScored6, int pointScored11, int pointScored21, int gameWon6, int gameWon11, int gameWon21, int gameLost6, int gameLost11, int gameLost21, int gamePlayed6, int gamePlayed11, int gamePlayed21) {
        this.id = id;
        this.name = name;
        this.pointLost6 = pointLost6;
        this.pointLost11 = pointLost11;
        this.pointLost21 = pointLost21;
        this.pointScored6 = pointScored6;
        this.pointScored11 = pointScored11;
        this.pointScored21 = pointScored21;
        this.gameWon6 = gameWon6;
        this.gameWon11 = gameWon11;
        this.gameWon21 = gameWon21;
        this.gameLost6 = gameLost6;
        this.gameLost11 = gameLost11;
        this.gameLost21 = gameLost21;
        this.gamePlayed6 = gamePlayed6;
        this.gamePlayed11 = gamePlayed11;
        this.gamePlayed21 = gamePlayed21;
    }

    protected CountModel(Parcel in) {
        id = in.readLong();
        name = in.readString();
        pointLost6 = in.readInt();
        pointLost11 = in.readInt();
        pointLost21 = in.readInt();
        pointScored6 = in.readInt();
        pointScored11 = in.readInt();
        pointScored21 = in.readInt();
        gameWon6 = in.readInt();
        gameWon11 = in.readInt();
        gameWon21 = in.readInt();
        gameLost6 = in.readInt();
        gameLost11 = in.readInt();
        gameLost21 = in.readInt();
        gamePlayed6 = in.readInt();
        gamePlayed11 = in.readInt();
        gamePlayed21 = in.readInt();
    }

    public static final Creator<CountModel> CREATOR = new Creator<CountModel>() {
        @Override
        public CountModel createFromParcel(Parcel in) {
            return new CountModel(in);
        }

        @Override
        public CountModel[] newArray(int size) {
            return new CountModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
    public void addGame(GameModel gameModel){
        int type = gameModel.getType();
        if(gameModel.getPlayer1().equals(name)){
            addGame(type,gameModel.getScorePlayer1(),gameModel.getScorePlayer2());
        }else if(gameModel.getPlayer2().equals(name)){
            addGame(type,gameModel.getScorePlayer2(),gameModel.getScorePlayer1());
        }else{
            Log.d(LOG_TAG,"Error add game, player not in game");
        }
    }
    public void addGame(int type, int scoreFor, int scoreAgainst){
        int result = scoreFor > scoreAgainst ? 1 : -1;
        if (scoreFor == scoreAgainst){
            result = 0;
        }
        switch (type){
            case 6:
                gamePlayed6++;
                pointLost6 += scoreAgainst;
                pointScored6 += scoreFor;
                switch (result){
                    case -1:
                        gameLost6++;
                        break;
                    case 1:
                        gameWon6++;
                        break;
                }
                break;
            case 11:
                gamePlayed11++;
                pointLost11 += scoreAgainst;
                pointScored11 += scoreFor;
                switch (result){
                    case -1:
                        gameLost11++;
                        break;
                    case 1:
                        gameWon11++;
                        break;
                }
                break;
            case 21:
                gamePlayed21++;
                pointLost21 += scoreAgainst;
                pointScored21 += scoreFor;
                switch (result){
                    case -1:
                        gameLost21++;
                        break;
                    case 1:
                        gameWon21++;
                        break;
                }
                break;
        }
    }

    public int getPointLost6() {
        return pointLost6;
    }

    @Override
    public String toString() {
        return "Count : "+pointLost6+" "+pointScored21;
    }

    public void setPointLost6(int pointLost6) {
        this.pointLost6 = pointLost6;
    }

    public int getPointLost11() {
        return pointLost11;
    }

    public void setPointLost11(int pointLost11) {
        this.pointLost11 = pointLost11;
    }

    public int getPointLost21() {
        return pointLost21;
    }

    public void setPointLost21(int pointLost21) {
        this.pointLost21 = pointLost21;
    }

    public int getPointScored6() {
        return pointScored6;
    }

    public void setPointScored6(int pointScored6) {
        this.pointScored6 = pointScored6;
    }

    public int getPointScored11() {
        return pointScored11;
    }

    public void setPointScored11(int pointScored11) {
        this.pointScored11 = pointScored11;
    }

    public int getPointScored21() {
        return pointScored21;
    }

    public void setPointScored21(int pointScored21) {
        this.pointScored21 = pointScored21;
    }

    public int getGameWon6() {
        return gameWon6;
    }

    public void setGameWon6(int gameWon6) {
        this.gameWon6 = gameWon6;
    }

    public int getGameWon11() {
        return gameWon11;
    }

    public void setGameWon11(int gameWon11) {
        this.gameWon11 = gameWon11;
    }

    public int getGameWon21() {
        return gameWon21;
    }

    public void setGameWon21(int gameWon21) {
        this.gameWon21 = gameWon21;
    }

    public int getGameLost6() {
        return gameLost6;
    }

    public void setGameLost6(int gameLost6) {
        this.gameLost6 = gameLost6;
    }

    public int getGameLost11() {
        return gameLost11;
    }

    public void setGameLost11(int gameLost11) {
        this.gameLost11 = gameLost11;
    }

    public int getGameLost21() {
        return gameLost21;
    }

    public void setGameLost21(int gameLost21) {
        this.gameLost21 = gameLost21;
    }

    public int getGamePlayed6() {
        return gamePlayed6;
    }

    public void setGamePlayed6(int gamePlayed6) {
        this.gamePlayed6 = gamePlayed6;
    }

    public int getGamePlayed11() {
        return gamePlayed11;
    }

    public void setGamePlayed11(int gamePlayed11) {
        this.gamePlayed11 = gamePlayed11;
    }

    public int getGamePlayed21() {
        return gamePlayed21;
    }

    public void setGamePlayed21(int gamePlayed21) {
        this.gamePlayed21 = gamePlayed21;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(pointLost6);
        dest.writeInt(pointLost11);
        dest.writeInt(pointLost21);
        dest.writeInt(pointScored6);
        dest.writeInt(pointScored11);
        dest.writeInt(pointScored21);
        dest.writeInt(gameWon6);
        dest.writeInt(gameWon11);
        dest.writeInt(gameWon21);
        dest.writeInt(gameLost6);
        dest.writeInt(gameLost11);
        dest.writeInt(gameLost21);
        dest.writeInt(gamePlayed6);
        dest.writeInt(gamePlayed11);
        dest.writeInt(gamePlayed21);
    }
}
