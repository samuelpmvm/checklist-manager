package com.example.checklist.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class CheckListTag implements Serializable {


    private String tag;

    public CheckListTag() {
        // Default constructor
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
