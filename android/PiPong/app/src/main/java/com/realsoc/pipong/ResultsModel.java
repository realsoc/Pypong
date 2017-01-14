package com.realsoc.pipong;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Hugo on 14/01/2017.
 */

public class ResultsModel implements Parcelable{

    private ArrayList<PlayerModel> players;
    private ArrayList<GameModel> games;
    protected ResultsModel(Parcel in) {

    }

    public static final Creator<ResultsModel> CREATOR = new Creator<ResultsModel>() {
        @Override
        public ResultsModel createFromParcel(Parcel in) {
            return new ResultsModel(in);
        }

        @Override
        public ResultsModel[] newArray(int size) {
            return new ResultsModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
