package com.checklist.repository;

import com.checklist.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<AppUser, String> {
}
