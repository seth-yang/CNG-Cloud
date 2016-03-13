package com.cng.cloud.data;

import com.google.gson.annotations.Expose;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by game on 2016/3/13
 */
@Entity
public class Card {
    @Expose
    private int id;

    @Expose
    private String userName;

    @Expose
    private Date writeDate;

    @Expose
    private Date expire;

    @Expose
    private boolean admin;

    @Id
    @Column (name = "id")
    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    @Basic
    @Column (name = "user_name")
    public String getUserName () {
        return userName;
    }

    public void setUserName (String userName) {
        this.userName = userName;
    }

    @Basic
    @Column (name = "write_date")
    public Date getWriteDate () {
        return writeDate;
    }

    public void setWriteDate (Date writeDate) {
        this.writeDate = writeDate;
    }

    @Basic
    @Column (name = "expire")
    public Date getExpire () {
        return expire;
    }

    public void setExpire (Date expire) {
        this.expire = expire;
    }

    @Basic
    @Column (name = "is_admin")
    public boolean isAdmin () {
        return admin;
    }

    public void setAdmin (boolean admin) {
        this.admin = admin;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass () != o.getClass ()) return false;

        Card card = (Card) o;

        return id != 0 && id == card.id;
    }

    @Override
    public int hashCode () {
        return id;
    }
}
