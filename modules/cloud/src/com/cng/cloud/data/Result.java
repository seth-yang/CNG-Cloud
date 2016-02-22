package com.cng.cloud.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by game on 2016/2/23
 */
public class Result<T> {
    @Expose
    @SerializedName ("data")
    private T userData;

    @Expose
    private String state;

    public Result () {};

    public Result (String state, T userData) {
        this.state = state;
        this.userData = userData;
    }

    public T getUserData () {
        return userData;
    }

    public void setUserData (T userData) {
        this.userData = userData;
    }

    public String getState () {
        return state;
    }

    public void setState (String state) {
        this.state = state;
    }
}