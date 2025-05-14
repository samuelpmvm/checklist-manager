package com.example.devopschecklist.controller;

import com.example.devopschecklist.service.CheckListManagerService;
import org.openapitools.api.CheckListManagerApi;
import org.openapitools.model.ChecklistDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class CheckListManagerController implements CheckListManagerApi {
    @Autowired
    private CheckListManagerService checkListManagerService;
}
