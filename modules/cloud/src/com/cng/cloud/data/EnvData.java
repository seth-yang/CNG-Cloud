package com.cng.cloud.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by game on 2016/2/23
 */
@Entity
@Table (name = "env_data")
public class EnvData {
    private String id;
    @Expose
    private String hostId;
    @Expose
    @SerializedName ("TS")
    private Date timestamp;
    @Expose
    @SerializedName ("T")
    private Double temperature;
    @Expose
    @SerializedName ("H")
    private Double humidity;
    @Expose
    @SerializedName ("S")
    private Double smoke;

    @Id
    @Column (name = "id")
    @GeneratedValue (generator = "system-uuid")
    @GenericGenerator (name = "system-uuid", strategy = "uuid")
    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    @Basic
    @Column (name = "host_id")
    public String getHostId () {
        return hostId;
    }

    public void setHostId (String hostId) {
        this.hostId = hostId;
    }

    @Basic
    @Column (name = "ts")
    public Date getTimestamp () {
        return timestamp;
    }

    public void setTimestamp (Date ts) {
        this.timestamp = ts;
    }

    @Basic
    @Column (name = "temperature")
    public Double getTemperature () {
        return temperature;
    }

    public void setTemperature (Double temperature) {
        this.temperature = temperature;
    }

    @Basic
    @Column (name = "humidity")
    public Double getHumidity () {
        return humidity;
    }

    public void setHumidity (Double humidity) {
        this.humidity = humidity;
    }

    public Double getSmoke () {
        return smoke;
    }

    public void setSmoke (Double smoke) {
        this.smoke = smoke;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass () != o.getClass ()) return false;

        EnvData event = (EnvData) o;

        return id != null && id.equals (event.getId ());
    }

    @Override
    public int hashCode () {
        return id != null ? id.hashCode () : 0;
    }
}