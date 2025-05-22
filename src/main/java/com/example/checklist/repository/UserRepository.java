package com.example.checklist.repository;

import com.example.checklist.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<AppUser, Long> {

    @Query("SELECT u FROM AppUser u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<AppUser> findByUsername(String username);
}
