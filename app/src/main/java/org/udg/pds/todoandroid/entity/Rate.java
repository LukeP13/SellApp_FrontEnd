package org.udg.pds.todoandroid.entity;


import java.io.Serializable;
import java.util.Date;

public class Rate implements Serializable {
    public Long idRate;
    public String ratingUserName;
    public String description;
    public Date date;
    public Float rate;

    public Rate(){}

    public Rate(Long idRate, String name, String description, Date date, Float rate) {
        this.idRate = idRate;
        this.ratingUserName = name;
        this.description = description;
        this.date = date;
        this.rate = rate;
    }

    public Long getIdRate() {
        return idRate;
    }

    public void setIdRate(Long id) {
        this.idRate = id;
    }

    public String getName() {
        return ratingUserName;
    }

    public void setName(String name) {
        this.ratingUserName = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }
}
