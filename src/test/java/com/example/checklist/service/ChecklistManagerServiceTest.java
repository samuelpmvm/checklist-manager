package com.example.checklist.service;

import com.example.checklist.entities.Checklist;
import com.example.checklist.exception.ChecklistException;
import com.example.checklist.mapper.ChecklistMapper;
import com.example.checklist.repository.ChecklistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.ChecklistDto;
import org.openapitools.model.ChecklistItemDto;
import org.openapitools.model.ChecklistTagDto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChecklistManagerServiceTest {

    private static final String TITLE = "title";
    private static final String ENVIRONMENT = "test";
    private static final String TAG = "tag1";
    private static final String VERSION = "1.0.0";
    private static final String DESCRIPTION = "description";
    private static final UUID CHECKLIST_ID = UUID.fromString("b5ac6cd8-85d2-459c-b593-5b9fea7247f7");

    @InjectMocks
    private ChecklistManagerService checkListManagerService;

    @Mock
    private ChecklistRepository checkListRepository;



    @Test
    void createCheckListSuccess() throws ChecklistException {
        var checklistDto = createChecklistDto();
        var checklistItemDto = checklistDto.getItems().getFirst();
        ArgumentCaptor<Checklist> checkListArgumentCaptor = ArgumentCaptor.forClass(Checklist.class);

        checkListManagerService.createCheckList(checklistDto);
        Mockito.verify(checkListRepository).save(checkListArgumentCaptor.capture());

        var checklist = checkListArgumentCaptor.getValue();
        assertEquals(checklistDto.getTitle(), checklist.getTitle());
        assertEquals(checklistDto.getEnvironment(), checklist.getEnvironment());
        assertEquals(checklistDto.getTags().getFirst().getTag(), checklist.getTags().getFirst().getTag());
        assertEquals(checklistDto.getVersion(), checklist.getVersion());

        var checklistItem = checklist.getItems().getFirst();
        assertEquals(checklistItemDto.getDescription(), checklistItem.getDescription());
        assertEquals(checklistItemDto.getStatus().name(), checklistItem.getStatus().name());
    }

    @Test
    void createCheckListAlreadyCreatedFails() {
        var checklistDto = createChecklistDto();
        Mockito.when(checkListRepository.findByTitleAndVersion(checklistDto.getTitle(), checklistDto.getVersion()))
                .thenReturn(Optional.of(ChecklistMapper.toEntity(checklistDto)));
        var checkListException = assertThrows(ChecklistException.class, () -> checkListManagerService.createCheckList(checklistDto));
        assertEquals(1002, checkListException.getChecklistError().getErrorCode());
    }

    @Test
    void updateCheckList() throws ChecklistException {
        var checklistDto = createChecklistDto();

        var checkList = ChecklistMapper.toEntity(checklistDto);
        checkList.setId(CHECKLIST_ID);
        var date = OffsetDateTime.now();

        try (MockedStatic<OffsetDateTime> mockedStatic = Mockito.mockStatic(OffsetDateTime.class)) {
            mockedStatic.when(OffsetDateTime::now).thenReturn(date);
            Mockito.when(checkListRepository.findById(CHECKLIST_ID)).thenReturn(Optional.of(checkList));
            checkListManagerService.updateChecklist(CHECKLIST_ID);
            Mockito.verify(checkListRepository, Mockito.times(1)).updateUpdatedAt(date, checkList.getId());
        }
    }

    @Test
    void updateNonExistingCheckListFails() {
        Mockito.when(checkListRepository.findById(CHECKLIST_ID))
                .thenReturn(Optional.empty());
        var checkListException = assertThrows(ChecklistException.class, () -> checkListManagerService.updateChecklist(CHECKLIST_ID));
        assertEquals(1001, checkListException.getChecklistError().getErrorCode());
    }

    @Test
    void getAllChecklist() {
        var checkList = ChecklistMapper.toEntity(createChecklistDto());
        Mockito.when(checkListRepository.findAll()).thenReturn(List.of(checkList));

        var listOfCheckList = checkListManagerService.getAllChecklist();
        Mockito.verify(checkListRepository, Mockito.times(1)).findAll();
        assertEquals(checkList, listOfCheckList.getFirst());
    }

    @Test
    void getChecklistByIdSuccess() {
        var checkList = ChecklistMapper.toEntity(createChecklistDto());
        Mockito.when(checkListRepository.findById(CHECKLIST_ID)).thenReturn(Optional.of(checkList));

        var checklistOptional = checkListManagerService.getCheckListById(CHECKLIST_ID);
        Mockito.verify(checkListRepository, Mockito.times(1)).findById(CHECKLIST_ID);
        assertEquals(checkList, checklistOptional.get());
    }

    @Test
    void getChecklistByIdReturnsEmpty() {
        Mockito.when(checkListRepository.findById(CHECKLIST_ID)).thenReturn(Optional.empty());

        var checklistOptional = checkListManagerService.getCheckListById(CHECKLIST_ID);
        Mockito.verify(checkListRepository, Mockito.times(1)).findById(CHECKLIST_ID);
        assertTrue(checklistOptional.isEmpty());
    }

    private static ChecklistDto createChecklistDto() {
        var checklistDto = new ChecklistDto();
        checklistDto.setTitle(TITLE);
        checklistDto.environment(ENVIRONMENT);
        checklistDto.setVersion(VERSION);

        var checkListTagDto = new ChecklistTagDto();
        checkListTagDto.setTag(TAG);
        checklistDto.setTags(List.of(checkListTagDto));

        var checkListItemDto = new ChecklistItemDto();
        checkListItemDto.setDescription(DESCRIPTION);
        checkListItemDto.setStatus(ChecklistItemDto.StatusEnum.DONE);
        checklistDto.setItems(List.of(checkListItemDto));

        return checklistDto;
    }
}