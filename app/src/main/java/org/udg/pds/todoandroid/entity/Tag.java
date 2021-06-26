package org.udg.pds.todoandroid.entity;

import java.io.Serializable;
import java.util.Date;

public class Tag implements Serializable {
    private Long idTag;
    private String name;

    public String getName() {
        return name;
    }
    public Long getIdTag() { return idTag; }

}
