package com.cng.cloud.data;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by game on 2016/2/23
 */
@Entity
@Table (name = "events")
public class Event {
    private String id;
    private String hostId;
    private Timestamp timestamp;
    private double temperature;
    private double humidity;

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
    public Timestamp getTimestamp () {
        return timestamp;
    }

    public void setTimestamp (Timestamp ts) {
        this.timestamp = ts;
    }

    @Basic
    @Column (name = "temperature")
    public double getTemperature () {
        return temperature;
    }

    public void setTemperature (double temperature) {
        this.temperature = temperature;
    }

    @Basic
    @Column (name = "humidity")
    public double getHumidity () {
        return humidity;
    }

    public void setHumidity (double humidity) {
        this.humidity = humidity;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass () != o.getClass ()) return false;

        Event event = (Event) o;

        return id != null && id.equals (event.getId ());
    }

    @Override
    public int hashCode () {
        return id != null ? id.hashCode () : 0;
    }
}