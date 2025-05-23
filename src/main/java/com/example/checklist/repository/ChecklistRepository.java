package com.example.checklist.repository;

import com.example.checklist.entities.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, UUID> {

    @Query("SELECT c FROM Checklist c WHERE c.title = :title AND c.version = :version")
    Optional<Checklist> findByTitleAndVersion(@Param("title") String title, @Param("version") String version);
}
