package com.example.checklist.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "checklist_tag")
public class CheckListTag {
    @Id
    @GeneratedValue
    private UUID id;

    private String tag;

    @ManyToOne
    @JoinColumn(name = "checklist_id")
    private Checklist checklist;

    public CheckListTag() {
        // Default constructor
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Checklist getChecklist() {
        return checklist;
    }

    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }
}
