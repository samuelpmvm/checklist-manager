package com.example.devopschecklist.repository;

import com.example.devopschecklist.entities.CheckList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CheckListRepository extends JpaRepository<CheckList, UUID> {

}
