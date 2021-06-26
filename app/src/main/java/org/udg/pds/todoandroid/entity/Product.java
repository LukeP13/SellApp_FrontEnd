package org.udg.pds.todoandroid.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Product implements Serializable {
    private Long idProduct;
    private String name;
    private String description;
    private Double price;
    private Date dateAdded;
    private byte state;
    private Date dateSold;
    private String location;
    private Long ownerId;
    private Long buyerId;
    private ArrayList<String> tags;

    public Product(String name, String description, Double price, Date dateAdded, byte state, Date dateSold, String location) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.dateAdded = dateAdded;
        this.state = state;
        this.dateSold = dateSold;
        this.location = location;
    }



    public Long getIdProduct() {
        return idProduct;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public byte getState() {
        return state;
    }

    public Date getDateSold() {
        return dateSold;
    }

    public String getLocation() {
        return location;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setIdProduct(Long idProduct) {
        this.idProduct = idProduct;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public void setDateSold(Date dateSold) {
        this.dateSold = dateSold;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public void setTags(ArrayList<String> tags){ this.tags = tags;}
}
