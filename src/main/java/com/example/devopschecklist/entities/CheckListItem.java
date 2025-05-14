package com.example.devopschecklist.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "checklist_item")
public class CheckListItem {
    @Id
    @GeneratedValue
    UUID id;
    String description;
    @Enumerated(EnumType.STRING)
    Status status;
    @ManyToOne(fetch = FetchType.LAZY)
    private CheckList checklist;

    public CheckListItem() {
        // Default constructor
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public CheckList getChecklist() {
        return checklist;
    }

    public void setChecklist(CheckList checklist) {
        this.checklist = checklist;
    }
}
