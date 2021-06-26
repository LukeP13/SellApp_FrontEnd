package org.udg.pds.todoandroid.entity;

import java.io.Serializable;
import java.util.Date;

public class Image implements Serializable {
    private Long idImage;
    private String name;
    private Long idProduct;

    public Image(String name) {
        this.name = name;
    }

    public Long getIdImage() {
        return idImage;
    }

    public String getName() {
        return name;
    }

    public Long getProductId() {
        return idProduct;
    }

    public void setIdImage(Long idImage) {
        this.idImage = idImage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIdProduct(Long idProduct) {
        this.idProduct = idProduct;
    }
}
