package com.cng.cloud.data;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by game on 2016/2/23
 */
@Entity
@Table (name = "host_pc")
public class Host {
    private String id, mac, shortcut, name, address, contact, memo;

    @Id
    @GeneratedValue (generator = "system-uuid")
    @GenericGenerator (name = "system-uuid", strategy = "uuid")
    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getMac () {
        return mac;
    }

    public void setMac (String mac) {
        this.mac = mac;
    }

    public String getShortcut () {
        return shortcut;
    }

    public void setShortcut (String shortcut) {
        this.shortcut = shortcut;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getAddress () {
        return address;
    }

    public void setAddress (String address) {
        this.address = address;
    }

    public String getContact () {
        return contact;
    }

    public void setContact (String contact) {
        this.contact = contact;
    }

    public String getMemo () {
        return memo;
    }

    public void setMemo (String memo) {
        this.memo = memo;
    }
}