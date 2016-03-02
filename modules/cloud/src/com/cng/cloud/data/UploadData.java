package com.cng.cloud.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by game on 2016/2/27
 */
public class UploadData implements Serializable {
    @Expose
    @SerializedName ("D")
    private List<EnvData> data;

    @Expose
    @SerializedName ("E")
    private List<Event> event;

    public List<EnvData> getData () {
        return data;
    }

    public void setData (List<EnvData> data) {
        this.data = data;
    }

    public List<Event> getEvent () {
        return event;
    }

    public void setEvent (List<Event> event) {
        this.event = event;
    }
}