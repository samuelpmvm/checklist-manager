package com.checklist.service;

import com.checklist.entities.Checklist;
import com.checklist.entities.ChecklistItem;
import com.checklist.exception.ChecklistException;
import com.checklist.mapper.ChecklistItemMapper;
import com.checklist.mapper.ChecklistMapper;
import com.checklist.repository.ChecklistItemRepository;
import com.checklist.repository.ChecklistRepository;
import com.model.checklist.ChecklistDto;
import com.model.checklist.ChecklistItemDto;
import com.model.checklist.ChecklistTagDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private static final int VERSION = 1;
    private static final String DESCRIPTION = "description";
    private static final UUID CHECKLIST_ID = UUID.fromString("b5ac6cd8-85d2-459c-b593-5b9fea7247f7");
    private static final UUID CHECKLISTITEM_ID = UUID.fromString("b5ac6cd8-85d2-459c-b593-5b9fea7247f7");

    @InjectMocks
    private ChecklistManagerService checklistManagerService;

    @Mock
    private ChecklistRepository checklistRepository;

    @Mock
    private ChecklistItemRepository checklistItemRepository;


    @Test
    void createChecklistSuccess() {
        var checklistDto = createChecklistDto();
        var checklistItemDto = checklistDto.getItems().getFirst();
        ArgumentCaptor<Checklist> checkListArgumentCaptor = ArgumentCaptor.forClass(Checklist.class);

        checklistManagerService.createChecklist(checklistDto);
        Mockito.verify(checklistRepository).save(checkListArgumentCaptor.capture());

        var checklist = checkListArgumentCaptor.getValue();
        assertEquals(checklistDto.getTitle(), checklist.getTitle());
        assertEquals(checklistDto.getEnvironment(), checklist.getEnvironment());
        assertEquals(checklistDto.getTags().getFirst().getTag(), checklist.getTags().getFirst().getTag());
        assertEquals(VERSION, checklist.getVersion());

        var checklistItem = checklist.getItems().getFirst();
        assertEquals(checklistItemDto.getDescription(), checklistItem.getDescription());
        assertEquals(checklistItemDto.getStatus().name(), checklistItem.getStatus().name());
    }

    @Test
    void createChecklistAlreadyCreatedIncreaseVersion() {
        var checklistDto = createChecklistDto();
        var checklistItemDto = checklistDto.getItems().getFirst();
        Mockito.when(checklistRepository.findMaxVersionByTitle(checklistDto.getTitle()))
                .thenReturn(Optional.of(1));
        ArgumentCaptor<Checklist> checkListArgumentCaptor = ArgumentCaptor.forClass(Checklist.class);

        checklistManagerService.createChecklist(checklistDto);
        Mockito.verify(checklistRepository).save(checkListArgumentCaptor.capture());

        var checklist = checkListArgumentCaptor.getValue();
        assertEquals(checklistDto.getTitle(), checklist.getTitle());
        assertEquals(checklistDto.getEnvironment(), checklist.getEnvironment());
        assertEquals(checklistDto.getTags().getFirst().getTag(), checklist.getTags().getFirst().getTag());
        assertEquals(VERSION + 1, checklist.getVersion());

        var checklistItem = checklist.getItems().getFirst();
        assertEquals(checklistItemDto.getDescription(), checklistItem.getDescription());
        assertEquals(checklistItemDto.getStatus().name(), checklistItem.getStatus().name());
    }

    @Test
    void updateCheckList() throws ChecklistException {
        var checklistDto = createChecklistDto();
        var checklistItemDto = checklistDto.getItems().getFirst();
        var checkList = ChecklistMapper.toEntity(checklistDto);
        checkList.setId(CHECKLIST_ID);
        var date = OffsetDateTime.now();

        try (MockedStatic<OffsetDateTime> mockedStatic = Mockito.mockStatic(OffsetDateTime.class)) {
            mockedStatic.when(OffsetDateTime::now).thenReturn(date);
            Mockito.when(checklistRepository.findById(CHECKLIST_ID)).thenReturn(Optional.of(checkList));
            ArgumentCaptor<Checklist> checkListArgumentCaptor = ArgumentCaptor.forClass(Checklist.class);

            checklistManagerService.updateChecklist(CHECKLIST_ID, checklistDto);
            Mockito.verify(checklistRepository).save(checkListArgumentCaptor.capture());
            var checklist = checkListArgumentCaptor.getValue();
            assertEquals(checklistDto.getTitle(), checklist.getTitle());
            assertEquals(checklistDto.getEnvironment(), checklist.getEnvironment());
            assertEquals(checklistDto.getTags().getFirst().getTag(), checklist.getTags().getFirst().getTag());
            assertEquals(checklistDto.getVersion(), checklist.getVersion());

            var checklistItem = checklist.getItems().getFirst();
            assertEquals(checklistItemDto.getDescription(), checklistItem.getDescription());
            assertEquals(checklistItemDto.getStatus().name(), checklistItem.getStatus().name());
        }
    }

    @Test
    void updateNonExistingCheckListFails() {
        Mockito.when(checklistRepository.findById(CHECKLIST_ID))
                .thenReturn(Optional.empty());
        var checkListException = assertThrows(ChecklistException.class, () ->
                checklistManagerService.updateChecklist(CHECKLIST_ID, createChecklistDto()));
        assertEquals(2000, checkListException.getChecklistError().getErrorCode());
    }

    @Test
    void getAllChecklist() {
        var checkList = ChecklistMapper.toEntity(createChecklistDto());
        Mockito.when(checklistRepository.findAll()).thenReturn(List.of(checkList));

        var listOfCheckListDto = checklistManagerService.getAllChecklist();
        Mockito.verify(checklistRepository, Mockito.times(1)).findAll();
        var checklistDto = listOfCheckListDto.getFirst();
        assertEquals(checkList.getTitle(), checklistDto.getTitle());
        assertEquals(checkList.getEnvironment(), checklistDto.getEnvironment());
        assertEquals(checkList.getVersion(), checklistDto.getVersion());
        assertEquals(checkList.getTags().getFirst().getTag(), checklistDto.getTags().getFirst().getTag());
        assertEquals(checkList.getItems().getFirst().getDescription(), checklistDto.getItems().getFirst().getDescription());
        assertEquals(checkList.getItems().getFirst().getStatus().name(), checklistDto.getItems().getFirst().getStatus().name());
        assertEquals(checkList.getCreatedAt(), checklistDto.getCreatedAt());
        assertEquals(checkList.getUpdatedAt(), checklistDto.getUpdatedAt());
    }

    @Test
    void getChecklistByIdSuccess() {
        var checkList = ChecklistMapper.toEntity(createChecklistDto());
        Mockito.when(checklistRepository.findById(CHECKLIST_ID)).thenReturn(Optional.of(checkList));

        var checklistDtoOptional = checklistManagerService.getChecklistById(CHECKLIST_ID);
        Mockito.verify(checklistRepository, Mockito.times(1)).findById(CHECKLIST_ID);
        var checklistDto = checklistDtoOptional.get();
        assertEquals(checkList.getTitle(), checklistDto.getTitle());
        assertEquals(checkList.getEnvironment(), checklistDto.getEnvironment());
        assertEquals(checkList.getVersion(), checklistDto.getVersion());
        assertEquals(checkList.getTags().getFirst().getTag(), checklistDto.getTags().getFirst().getTag());
        assertEquals(checkList.getItems().getFirst().getDescription(), checklistDto.getItems().getFirst().getDescription());
        assertEquals(checkList.getItems().getFirst().getStatus().name(), checklistDto.getItems().getFirst().getStatus().name());
    }

    @Test
    void getChecklistByIdReturnsEmpty() {
        Mockito.when(checklistRepository.findById(CHECKLIST_ID)).thenReturn(Optional.empty());

        var checklistOptional = checklistManagerService.getChecklistById(CHECKLIST_ID);
        Mockito.verify(checklistRepository, Mockito.times(1)).findById(CHECKLIST_ID);
        assertTrue(checklistOptional.isEmpty());
    }

    @Test
    void getChecklistItemByIdSuccess() throws ChecklistException {
        var checkListItemDto = new ChecklistItemDto();
        checkListItemDto.setId(String.valueOf(CHECKLISTITEM_ID));
        checkListItemDto.setDescription(DESCRIPTION);
        checkListItemDto.setStatus(ChecklistItemDto.StatusEnum.BLOCKED);
        var checkList = ChecklistMapper.toEntity(createChecklistDto());
        Mockito.when(checklistRepository.findById(CHECKLIST_ID)).thenReturn(Optional.of(checkList));
        Mockito.when(checklistItemRepository
                .findChecklistItemsByChecklistId(CHECKLIST_ID))
                .thenReturn(List.of(ChecklistItemMapper.toEntity(checkList, checkListItemDto)));

        checklistManagerService.getChecklistItems(CHECKLIST_ID);

        Mockito.verify(checklistRepository, Mockito.times(1)).findById(CHECKLIST_ID);
        Mockito.verify(checklistItemRepository, Mockito.times(1)).findChecklistItemsByChecklistId(CHECKLIST_ID);
    }

    @Test
    void getChecklistItemByIdFails() {
        Mockito.when(checklistRepository.findById(CHECKLIST_ID)).thenReturn(Optional.empty());

        var checklistException = assertThrows(ChecklistException.class, () -> checklistManagerService.getChecklistItems(CHECKLIST_ID));
        assertEquals(2000, checklistException.getChecklistError().getErrorCode());
        Mockito.verify(checklistRepository, Mockito.times(1)).findById(CHECKLIST_ID);
        Mockito.verify(checklistItemRepository, Mockito.never()).findChecklistItemsByChecklistId(CHECKLIST_ID);
    }

    @Test
    void createChecklistItemSuccess() throws ChecklistException {
        var checkList = ChecklistMapper.toEntity(createChecklistDto());
        Mockito.when(checklistRepository.findById(CHECKLIST_ID)).thenReturn(Optional.of(checkList));
        var checkListItemDto = new ChecklistItemDto();
        checkListItemDto.setStatus(ChecklistItemDto.StatusEnum.BLOCKED);
        checklistManagerService.createChecklistItem(CHECKLIST_ID, checkListItemDto);

        Mockito.verify(checklistRepository, Mockito.times(1)).findById(CHECKLIST_ID);
        Mockito.verify(checklistItemRepository, Mockito.times(1)).save(ArgumentMatchers.any(ChecklistItem.class));
    }

    @Test
    void createChecklistItemFails() {
        Mockito.when(checklistRepository.findById(CHECKLIST_ID)).thenReturn(Optional.empty());

        var checklistException = assertThrows(ChecklistException.class, () -> checklistManagerService.createChecklistItem(CHECKLIST_ID, new ChecklistItemDto()));
        assertEquals(2000, checklistException.getChecklistError().getErrorCode());
        Mockito.verify(checklistRepository, Mockito.times(1)).findById(CHECKLIST_ID);
        Mockito.verify(checklistItemRepository, Mockito.never()).save(ArgumentMatchers.any(ChecklistItem.class));
    }

    @Test
    void updateChecklistItemSuccess() throws ChecklistException {
        var checkList = ChecklistMapper.toEntity(createChecklistDto());
        Mockito.when(checklistRepository.findById(CHECKLIST_ID)).thenReturn(Optional.of(checkList));
        var checkListItemDto = new ChecklistItemDto();
        checkListItemDto.setStatus(ChecklistItemDto.StatusEnum.BLOCKED);
        Mockito.when(checklistItemRepository.findById(CHECKLISTITEM_ID)).thenReturn(Optional.ofNullable(checkList.getItems().getFirst()));
        checklistManagerService.updateChecklistItem(CHECKLIST_ID, CHECKLISTITEM_ID, checkListItemDto);

        Mockito.verify(checklistRepository, Mockito.times(1)).findById(CHECKLIST_ID);
        Mockito.verify(checklistItemRepository, Mockito.times(1)).findById(CHECKLIST_ID);
        Mockito.verify(checklistItemRepository, Mockito.times(1)).save(ArgumentMatchers.any(ChecklistItem.class));

    }

    @Test
    void updateChecklistItemEmptyChecklistFails() {
        Mockito.when(checklistRepository.findById(CHECKLIST_ID)).thenReturn(Optional.empty());

        var checklistException = assertThrows(ChecklistException.class,
                () -> checklistManagerService.updateChecklistItem(CHECKLIST_ID, CHECKLISTITEM_ID, new ChecklistItemDto()));
        assertEquals(2000, checklistException.getChecklistError().getErrorCode());
        Mockito.verify(checklistRepository, Mockito.times(1)).findById(CHECKLIST_ID);
        Mockito.verify(checklistItemRepository, Mockito.never()).save(ArgumentMatchers.any(ChecklistItem.class));

    }

    @Test
    void updateChecklistItemEmptyChecklistItemFails() {
        var checkList = ChecklistMapper.toEntity(createChecklistDto());
        Mockito.when(checklistRepository.findById(CHECKLIST_ID)).thenReturn(Optional.of(checkList));
        Mockito.when(checklistItemRepository.findById(CHECKLISTITEM_ID)).thenReturn(Optional.empty());

        var checklistException = assertThrows(ChecklistException.class,
                () -> checklistManagerService.updateChecklistItem(CHECKLIST_ID, CHECKLISTITEM_ID, new ChecklistItemDto()));
        assertEquals(2002, checklistException.getChecklistError().getErrorCode());
        Mockito.verify(checklistRepository, Mockito.times(1)).findById(CHECKLIST_ID);
        Mockito.verify(checklistItemRepository, Mockito.never()).save(ArgumentMatchers.any(ChecklistItem.class));
    }

    private static ChecklistDto createChecklistDto() {
        var checklistDto = new ChecklistDto();
        checklistDto.setTitle(TITLE);
        checklistDto.environment(ENVIRONMENT);

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