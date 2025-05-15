package com.example.checklist.mapper;

import com.example.checklist.entities.CheckListTag;
import com.example.checklist.entities.Checklist;
import org.openapitools.model.ChecklistTagDto;

public final class CheckListTagMapper {
    private CheckListTagMapper() {
    }

    public static ChecklistTagDto toDto (CheckListTag checkListTag) {
        var checklistTagDto = new ChecklistTagDto();
        checklistTagDto.setTag(checkListTag.getTag());
        return checklistTagDto;
    }

    public static CheckListTag toEntity (Checklist checkList, ChecklistTagDto checkListTagDto) {
        var checkListTag = new CheckListTag();
        checkListTag.setTag(checkListTagDto.getTag());
        checkListTag.setChecklist(checkList);
        return checkListTag;
    }
}
