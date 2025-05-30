package com.checklist.mapper;

import com.checklist.entities.Checklist;
import com.checklist.entities.ChecklistItem;
import com.checklist.resources.Status;
import com.model.checklist.ChecklistItemDto;

public final class ChecklistItemMapper {

    private ChecklistItemMapper() {
    }

    public static ChecklistItemDto toDto (ChecklistItem checkListItem) {
        var checklistItemDto = new ChecklistItemDto();
        checklistItemDto.setId(String.valueOf(checkListItem.getId()));
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
