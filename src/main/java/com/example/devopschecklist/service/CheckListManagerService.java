package com.example.devopschecklist.service;

import com.example.devopschecklist.entities.CheckList;
import com.example.devopschecklist.repository.CheckListRepository;
import org.openapitools.model.ChecklistDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckListManagerService {
    @Autowired
    private CheckListRepository checkListRepository;

}
