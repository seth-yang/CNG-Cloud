package com.cng.cloud.data;

import com.google.gson.annotations.Expose;

import javax.persistence.*;

/**
 * Created by game on 2016/3/13
 */
@Entity
@Table (name = "ir_code")
public class IRCode {
    @Expose
    private int id;

    @Expose
    private String host;

    @Expose
    private String name;

    @Expose
    private String chinese;

    @Expose
    private int code;

    @Id
    @Column (name = "id")
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    public int getId () {
        return id;
    }

    public void setId (int id) {
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
    @Column (name = "name")
    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    @Basic
    @Column (name = "chinese")
    public String getChinese () {
        return chinese;
    }

    public void setChinese (String chinese) {
        this.chinese = chinese;
    }

    @Basic
    @Column (name = "code")
    public int getCode () {
        return code;
    }

    public void setCode (int code) {
        this.code = code;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass () != o.getClass ()) return false;

        IRCode irCode = (IRCode) o;

        return code != 0 && code == irCode.code;
    }

    @Override
    public int hashCode () {
        return code;
    }
}
