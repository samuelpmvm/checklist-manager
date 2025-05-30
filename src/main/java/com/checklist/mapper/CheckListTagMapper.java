package com.checklist.mapper;

import com.checklist.entities.CheckListTag;
import com.model.checklist.ChecklistTagDto;

public final class CheckListTagMapper {
    private CheckListTagMapper() {
    }

    public static ChecklistTagDto toDto (CheckListTag checkListTag) {
        var checklistTagDto = new ChecklistTagDto();
        checklistTagDto.setTag(checkListTag.getTag());
        return checklistTagDto;
    }

    public static CheckListTag toEntity (ChecklistTagDto checkListTagDto) {
        var checkListTag = new CheckListTag();
        checkListTag.setTag(checkListTagDto.getTag());
        return checkListTag;
    }
}
