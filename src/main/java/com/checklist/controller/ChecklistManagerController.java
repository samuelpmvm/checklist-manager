package com.checklist.controller;

import com.api.checklist.ChecklistManagerApi;
import com.checklist.exception.ChecklistException;
import com.checklist.mapper.ChecklistItemMapper;
import com.checklist.mapper.ChecklistMapper;
import com.checklist.service.ChecklistManagerService;
import com.model.checklist.ChecklistDto;
import com.model.checklist.ChecklistItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ChecklistManagerController implements ChecklistManagerApi {

    static final String APPLICATION_CHECKLIST_REQUEST_V_1_JSON= "application/checklist-request-v1+json";
    static final String APPLICATION_CHECKLIST_V_1_JSON= "application/checklist-v1+json";
    static final String APPLICATION_CHECKLIST_ITEM_REQUEST_V_1_JSON = "application/checklist-item-request-v1+json";
    static final String APPLICATION_CHECKLIST_ITEM_V_1_JSON = "application/checklist-item-v1+json";
    private final ChecklistManagerService checkListManagerService;

    public ChecklistManagerController(ChecklistManagerService checkListManagerService) {
        this.checkListManagerService = checkListManagerService;
    }

    @Override
    public ResponseEntity<ChecklistDto> createChecklist(ChecklistDto checklistDto) throws ChecklistException {
        var checkList = checkListManagerService.createChecklist(checklistDto);
        return ResponseEntity.ok(ChecklistMapper.toDto(checkList));
    }

    @Override
    public ResponseEntity<List<ChecklistDto>> getCheckLists() {
        return ResponseEntity.ok(checkListManagerService.getAllChecklist());
    }

    @Override
    public ResponseEntity<ChecklistDto> updateChecklist(String id, ChecklistDto checklistDto) throws ChecklistException {
        return ResponseEntity.ok(checkListManagerService.updateChecklist(UUID.fromString(id), checklistDto));
    }

    @Override
    public ResponseEntity<ChecklistDto> getChecklistById(String id) {
        return checkListManagerService.getChecklistById(UUID.fromString(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteChecklistById(String id) throws ChecklistException {
        checkListManagerService.deleteChecklistById(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ChecklistItemDto>> getChecklistItems(String id) throws ChecklistException {
        var checklistItems = checkListManagerService.getChecklistItems(UUID.fromString(id));
        return ResponseEntity.ok(checklistItems.stream().map(ChecklistItemMapper::toDto).toList());
    }

    @Override
    public ResponseEntity<ChecklistItemDto> createChecklistItem(String id, ChecklistItemDto checklistItemDto) throws ChecklistException {
        var checklistItem = checkListManagerService.createChecklistItem(UUID.fromString(id), checklistItemDto);
        return ResponseEntity.ok(ChecklistItemMapper.toDto(checklistItem));
    }

    @Override
    public ResponseEntity<ChecklistItemDto> updateChecklistItem(String id, String itemId, ChecklistItemDto checklistItemDto) throws ChecklistException {
        var checklistItem = checkListManagerService.updateChecklistItem(UUID.fromString(id), UUID.fromString(itemId), checklistItemDto);
        return ResponseEntity.ok(ChecklistItemMapper.toDto(checklistItem));
    }
}
