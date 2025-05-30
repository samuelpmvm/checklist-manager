package com.checklist.service;

import com.checklist.entities.Checklist;
import com.checklist.entities.ChecklistItem;
import com.checklist.exception.ChecklistError;
import com.checklist.exception.ChecklistException;
import com.checklist.mapper.ChecklistItemMapper;
import com.checklist.mapper.ChecklistMapper;
import com.checklist.repository.ChecklistItemRepository;
import com.checklist.repository.ChecklistRepository;
import com.checklist.resources.Status;
import com.model.checklist.ChecklistDto;
import com.model.checklist.ChecklistItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChecklistManagerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChecklistManagerService.class);
    private static final String CHECKLIST_WITH_ID_WAS_NOT_FOUND = "Checklist with id {} was not found";

    private final ChecklistRepository checkListRepository;
    private final ChecklistItemRepository checkListItemRepository;

    public ChecklistManagerService(ChecklistRepository checkListRepository, ChecklistItemRepository checkListItemRepository) {
        this.checkListRepository = checkListRepository;
        this.checkListItemRepository = checkListItemRepository;
    }

    public Checklist createChecklist(ChecklistDto checklistDto) throws ChecklistException {
        LOGGER.info("Creating checklist with title {} and version {}", checklistDto.getTitle(), checklistDto.getVersion());
        var checkList = ChecklistMapper.toEntity(checklistDto);
        if (checkListRepository.findByTitleAndVersion(checklistDto.getTitle(), checklistDto.getVersion()).isEmpty()) {
            LOGGER.info("Checklist with title {} and version {} does not exist, creating a new one", checklistDto.getTitle(), checklistDto.getVersion());
            var date = OffsetDateTime.now();
            checkList.setCreatedAt(date);
            checkList.setUpdatedAt(date);
            return checkListRepository.save(checkList);
        } else {
            throw new ChecklistException(ChecklistError.CHECKLIST_ALREADY_EXISTS);
        }
    }

    @Transactional
    public ChecklistDto updateChecklist(UUID id, ChecklistDto checklistDto) throws ChecklistException {
        var checkListOpt = checkListRepository.findById(id);
        if (checkListOpt.isPresent()) {
            var checklist = checkListOpt.get();
            LOGGER.info("Updating checklist with id: {}", id);
            updateChecklistAttributes(checklist, checklistDto);
            checkListRepository.save(checklist);
            return ChecklistMapper.toDto(checklist);
        } else {
            LOGGER.error(CHECKLIST_WITH_ID_WAS_NOT_FOUND, id);
            throw new ChecklistException(ChecklistError.CHECKLIST_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    public List<ChecklistDto> getAllChecklist() {
        LOGGER.info("Getting all Checklists");
        var checklists = checkListRepository.findAll();
        return checklists.stream().map(ChecklistMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Optional<ChecklistDto> getChecklistById(UUID id) {
        LOGGER.info("Getting Checklist with id: {}", id);
        var checkListOpt = checkListRepository.findById(id);
        if (checkListOpt.isPresent()) {
            LOGGER.info("Checklist with id {} was found", id);
            return Optional.of(ChecklistMapper.toDto(checkListOpt.get()));
        } else {
            LOGGER.error(CHECKLIST_WITH_ID_WAS_NOT_FOUND, id);
            return Optional.empty();
        }
    }

    public void deleteChecklistById(UUID id) throws ChecklistException {
        var checkListOpt = checkListRepository.findById(id);
        if (checkListOpt.isPresent()) {
            LOGGER.info("Deleting checklist with id: {}", id);
            checkListRepository.deleteById(id);
        } else {
            LOGGER.error(CHECKLIST_WITH_ID_WAS_NOT_FOUND, id);
            throw new ChecklistException(ChecklistError.CHECKLIST_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    public List<ChecklistItem> getChecklistItems(UUID id) throws ChecklistException {
        LOGGER.info("Getting checklist items for checklist with id: {}", id);
        var checkListOpt = checkListRepository.findById(id);
        if (checkListOpt.isPresent()) {
            return checkListItemRepository.findChecklistItemsByChecklistId(id);
        } else {
            LOGGER.error(CHECKLIST_WITH_ID_WAS_NOT_FOUND, id);
            throw new ChecklistException(ChecklistError.CHECKLIST_NOT_FOUND);
        }
    }

    @Transactional
    public ChecklistItem createChecklistItem(UUID id, ChecklistItemDto checklistItemDto) throws ChecklistException {
        LOGGER.info("Creating checklist item for checklist with id: {}", id);
        var checkListOpt = checkListRepository.findById(id);
        if (checkListOpt.isPresent()) {
            var checklistItem = ChecklistItemMapper.toEntity(checkListOpt.get(), checklistItemDto);
            checklistItem.setChecklist(checkListOpt.get());
            LOGGER.info("Saving checklist item: {}", checklistItem);
            return checkListItemRepository.save(checklistItem);
        } else {
            LOGGER.error(CHECKLIST_WITH_ID_WAS_NOT_FOUND, id);
            throw new ChecklistException(ChecklistError.CHECKLIST_NOT_FOUND);
        }
    }

    @Transactional
    public ChecklistItem updateChecklistItem(UUID id, UUID idItem, ChecklistItemDto checklistItemDto) throws ChecklistException {
        LOGGER.info("Updating checklist item with id: {} for checklist with id: {}", idItem, id);
        var checkListOpt = checkListRepository.findById(id);
        if (checkListOpt.isPresent()) {
            var checklistItemOpt = checkListItemRepository.findById(idItem);
            if (checklistItemOpt.isPresent()) {
                var checklistItem = checklistItemOpt.get();
                checklistItem.setDescription(checklistItemDto.getDescription());
                checklistItem.setStatus(Status.valueOf(checklistItemDto.getStatus().getValue()));
                LOGGER.info("Updating checklist item: {}", checklistItem);
                return checkListItemRepository.save(checklistItem);
            } else {
                LOGGER.error("ChecklistItem with id {} was not found", idItem);
                throw new ChecklistException(ChecklistError.CHECKLIST_ITEM_NOT_FOUND);
            }
        } else {
            LOGGER.error(CHECKLIST_WITH_ID_WAS_NOT_FOUND, id);
            throw new ChecklistException(ChecklistError.CHECKLIST_NOT_FOUND);
        }
    }

    private static void updateChecklistAttributes(Checklist checklist, ChecklistDto checklistDto) {
        checklist.setTitle(checklistDto.getTitle());
        checklist.setVersion(checklistDto.getVersion());
        checklist.setEnvironment(checklistDto.getEnvironment());
        checklist.setUpdatedAt(OffsetDateTime.now());
        ChecklistMapper.updateChecklistItems(checklist, checklistDto.getItems());
        ChecklistMapper.updateChecklistTags(checklist, checklistDto.getTags());
    }
}
