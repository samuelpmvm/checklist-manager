package com.example.checklist.mapper;

import com.example.checklist.entities.Checklist;
import com.example.model.checklist.ChecklistDto;
import com.example.model.checklist.ChecklistItemDto;
import com.example.model.checklist.ChecklistTagDto;

import java.util.List;
import java.util.stream.Collectors;

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
        checklistDto.setCreatedAt(checkList.getCreatedAt());
        checklistDto.setUpdatedAt(checkList.getUpdatedAt());
        return checklistDto;
    }

    @SuppressWarnings("java:S6204")
    public static Checklist toEntity(ChecklistDto checklistDto) {
        var checklist = new Checklist();
        checklist.setEnvironment(checklistDto.getEnvironment());
        checklist.setVersion(checklistDto.getVersion());
        checklist.setTitle(checklistDto.getTitle());
        var checkListItem = checklistDto
                .getItems()
                .stream()
                .map(checklistItemDto -> ChecklistItemMapper.toEntity(checklist, checklistItemDto))
                .collect(Collectors.toList());
        checklist.setItems(checkListItem);
        var checkListTag = checklistDto.getTags()
                .stream()
                .map(CheckListTagMapper::toEntity)
                .collect(Collectors.toList());
        checklist.setTags(checkListTag);

        return checklist;
    }

    public static void updateChecklistItems(Checklist checklist, List<ChecklistItemDto> checklistItemsDto) {
        checklist.getItems().clear();
        for (ChecklistItemDto checklistItemDto : checklistItemsDto) {
            var checklistItem = ChecklistItemMapper.toEntity(checklist, checklistItemDto);
            checklist.getItems().add(checklistItem);
        }
    }

    public static void updateChecklistTags(Checklist checklist, List<ChecklistTagDto> checklistTagsDto) {
        checklist.getTags().clear();
        for (ChecklistTagDto checklistTagDto : checklistTagsDto) {
            var checklistTag = CheckListTagMapper.toEntity(checklistTagDto);
            checklist.getTags().add(checklistTag);
        }
    }
}
