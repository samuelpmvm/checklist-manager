package com.example.checklist.mapper;

import com.example.checklist.entities.Checklist;
import com.example.checklist.entities.ChecklistItem;
import com.example.checklist.entities.Status;
import org.openapitools.model.ChecklistItemDto;

public final class ChecklistItemMapper {

    private ChecklistItemMapper() {
    }

    public static ChecklistItemDto toDto (ChecklistItem checkListItem) {
        var checklistItemDto = new ChecklistItemDto();
        checklistItemDto.setDescription(checkListItem.getDescription());
        checklistItemDto.setStatus(ChecklistItemDto.StatusEnum.valueOf(checkListItem.getStatus().name()));
        return checklistItemDto;
    }

    public static ChecklistItem toEntity (Checklist checkList, ChecklistItemDto checklistItemDto) {
        var checkListItem = new ChecklistItem();
        checkListItem.setDescription(checklistItemDto.getDescription());
        checkListItem.setStatus(Status.valueOf(checklistItemDto.getStatus().name()));
        checkListItem.setChecklist(checkList);
        return checkListItem;
    }
}
