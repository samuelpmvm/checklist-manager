package com.checklist.repository;

import com.checklist.entities.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, UUID> {

    @Query("SELECT MAX(c.version) FROM Checklist c WHERE c.title = :title")
    Optional<Integer> findMaxVersionByTitle(@Param("title") String title);
}
