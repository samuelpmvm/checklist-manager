package com.example.checklist.repository;

import com.example.checklist.entities.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, UUID> {

}
