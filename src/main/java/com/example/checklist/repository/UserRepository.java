package com.example.checklist.repository;

import com.example.checklist.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<AppUser, String> {
}
