package com.cng.cloud.data;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by game on 2016/3/13
 */
@Entity
@Table (name = "card_mapping", schema = "public", catalog = "cng")
public class CardMapping {
    @Expose
    private String id;

    @Expose
    private String host;

    @Expose
    private int card;

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
    public String getHost () {
        return host;
    }

    public void setHost (String host) {
        this.host = host;
    }

    @Basic
    @Column (name = "card_id")
    public int getCard () {
        return card;
    }

    public void setCard (int card) {
        this.card = card;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass () != o.getClass ()) return false;

        CardMapping that = (CardMapping) o;

        if (card != that.card) return false;
        if (id != null ? !id.equals (that.id) : that.id != null) return false;
        if (host != null ? !host.equals (that.host) : that.host != null) return false;

        return true;
    }

    @Override
    public int hashCode () {
        int result = id != null ? id.hashCode () : 0;
        result = 31 * result + (host != null ? host.hashCode () : 0);
        result = 31 * result + card;
        return result;
    }
}
