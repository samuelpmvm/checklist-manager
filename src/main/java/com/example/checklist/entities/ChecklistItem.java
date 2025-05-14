package com.example.checklist.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "checklist_item")
public class ChecklistItem {
    @Id
    @GeneratedValue
    UUID id;
    String description;
    @Enumerated(EnumType.STRING)
    Status status;
    @ManyToOne(fetch = FetchType.LAZY)
    private Checklist checklist;

    public ChecklistItem() {
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

    public Checklist getChecklist() {
        return checklist;
    }

    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }
}
