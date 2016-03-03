package com.cng.cloud.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by game on 2016/2/27
 */
@Entity
@Table (name = "events")
public class Event {
    @Expose
    private String id;
    private String hostId;
    @Expose
    @SerializedName ("TS")
    private Date timestamp;

    @Expose
    @SerializedName ("T")
    private EventType type;

    @Expose
    @SerializedName ("D")
    private String eventData;

    @Id
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
    @Column (name = "event_type")
    @Enumerated (EnumType.STRING)
    public EventType getType () {
        return type;
    }

    public void setType (EventType eventType) {
        this.type = eventType;
    }

    @Basic
    @Column (name = "event_data")
    public String getEventData () {
        return eventData;
    }

    public void setEventData (String eventData) {
        this.eventData = eventData;
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
