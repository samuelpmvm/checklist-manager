package com.example.checklist.service;

import com.example.checklist.entities.Checklist;
import com.example.checklist.repository.ChecklistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.ChecklistDto;
import org.openapitools.model.ChecklistItemDto;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ChecklistManagerServiceTest {

    private static final String TITLE = "title";
    private static final String ENVIRONMENT = "test";
    private static final Set<String> TAGS_SET = Set.of("tag1", "tag2");
    private static final String VERSION = "1.0.0";
    private static final String DESCRIPTION = "description";

    @InjectMocks
    private ChecklistManagerService checkListManagerService;

    @Mock
    private ChecklistRepository checkListRepository;



    @Test
    void createCheckList() {
        var checklistDto = createChecklistDto();
        var checklistItemDto = checklistDto.getItems().getFirst();
        ArgumentCaptor<Checklist> checkListArgumentCaptor = ArgumentCaptor.forClass(Checklist.class);

        checkListManagerService.createCheckList(checklistDto);
        Mockito.verify(checkListRepository).save(checkListArgumentCaptor.capture());

        var checklist = checkListArgumentCaptor.getValue();
        assertEquals(checklistDto.getTitle(), checklist.getTitle());
        assertEquals(checklistDto.getEnvironment(), checklist.getEnvironment());
        assertEquals(checklistDto.getTags(), checklist.getTags());
        assertEquals(checklistDto.getVersion(), checklist.getVersion());

        var checklistItem = checklist.getItems().getFirst();
        assertEquals(checklistItemDto.getDescription(), checklistItem.getDescription());
        assertEquals(checklistItemDto.getStatus().name(), checklistItem.getStatus().name());
    }

    private static ChecklistDto createChecklistDto() {
        var checklistDto = new ChecklistDto();
        checklistDto.setTitle(TITLE);
        checklistDto.environment(ENVIRONMENT);
        checklistDto.setTags(TAGS_SET);
        checklistDto.setVersion(VERSION);

        var checkListItemDto = new ChecklistItemDto();
        checkListItemDto.setDescription(DESCRIPTION);
        checkListItemDto.setStatus(ChecklistItemDto.StatusEnum.DONE);
        checklistDto.setItems(List.of(checkListItemDto));

        return checklistDto;
    }
}