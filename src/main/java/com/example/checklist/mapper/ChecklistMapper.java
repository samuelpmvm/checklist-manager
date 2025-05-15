package com.example.checklist.mapper;

import com.example.checklist.entities.Checklist;
import org.openapitools.model.ChecklistDto;

public final class ChecklistMapper {

    private ChecklistMapper() {
    }

    public static ChecklistDto toDto(Checklist checkList) {
        var checklistDto = new ChecklistDto();
        checklistDto.setId(String.valueOf(checkList.getId()));
        checklistDto.setTitle(checkList.getTitle());
        checklistDto.setEnvironment(checkList.getEnvironment());
        checklistDto.setVersion(checkList.getVersion());
        var checkListItemDto = checkList.getItems().stream().map(ChecklistItemMapper::toDto).toList();
        checklistDto.setItems(checkListItemDto);
        var checkListTagDto = checkList.getTags().stream().map(CheckListTagMapper::toDto).toList();
        checklistDto.setTags(checkListTagDto);

        return checklistDto;
    }

    public static Checklist toEntity(ChecklistDto checklistDto) {
        var checklist = new Checklist();
        checklist.setEnvironment(checklistDto.getEnvironment());
        checklist.setVersion(checklistDto.getVersion());
        checklist.setTitle(checklistDto.getTitle());
        var checkListItem = checklistDto.getItems().stream().map(ChecklistItemMapper::toEntity).toList();
        checklist.setItems(checkListItem);
        var checkListTag = checklistDto.getTags().stream().map(CheckListTagMapper::toEntity).toList();
        checklist.setTags(checkListTag);

        return checklist;
    }
}
