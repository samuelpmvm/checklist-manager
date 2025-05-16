package com.example.checklist.controller;

import com.example.checklist.exception.ChecklistException;
import com.example.checklist.mapper.ChecklistMapper;
import com.example.checklist.service.ChecklistManagerService;
import org.openapitools.api.ChecklistManagerApi;
import org.openapitools.model.ChecklistDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ChecklistManagerController implements ChecklistManagerApi {

    static final String APPLICATION_CHECKLIST_REQUEST_V_1_JSON= "application/checklist-request-v1+json";
    static final String APPLICATION_CHECKLIST_V_1_JSON= "application/checklist-v1+json";
    private final ChecklistManagerService checkListManagerService;

    public ChecklistManagerController(ChecklistManagerService checkListManagerService) {
        this.checkListManagerService = checkListManagerService;
    }

    @Override
    public ResponseEntity<ChecklistDto> createChecklist(ChecklistDto checklistDto
    ) throws ChecklistException {
        var checkList = checkListManagerService.createCheckList(checklistDto);
        return ResponseEntity.ok(ChecklistMapper.toDto(checkList));
    }

    @Override
    public ResponseEntity<List<ChecklistDto>> getCheckLists() {
        var checkLists = checkListManagerService.getAllCheckList();
        return ResponseEntity.ok(checkLists.stream().map(ChecklistMapper::toDto).toList());
    }
}
