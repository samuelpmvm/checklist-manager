package com.checklist.repository;

import com.checklist.entities.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, UUID> {

    @Query("SELECT c FROM ChecklistItem c WHERE c.checklist.id = :id")
    List<ChecklistItem> findChecklistItemsByChecklistId(@Param("id") UUID id);
}
