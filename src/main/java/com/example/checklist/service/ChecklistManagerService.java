package com.example.checklist.service;

import com.example.checklist.entities.Checklist;
import com.example.checklist.entities.ChecklistItem;
import com.example.checklist.exception.ChecklistError;
import com.example.checklist.exception.ChecklistException;
import com.example.checklist.mapper.ChecklistItemMapper;
import com.example.checklist.mapper.ChecklistMapper;
import com.example.checklist.repository.ChecklistItemRepository;
import com.example.checklist.repository.ChecklistRepository;
import com.example.model.checklist.ChecklistDto;
import com.example.model.checklist.ChecklistItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChecklistManagerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChecklistManagerService.class);

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

    public Checklist updateChecklist(UUID id) throws ChecklistException {
        var checkListOpt = checkListRepository.findById(id);
        if (checkListOpt.isPresent()) {
            var checklist = checkListOpt.get();
            LOGGER.info("Updating checklist with id: {}", id);
            checklist.setUpdatedAt(OffsetDateTime.now());
            checkListRepository.updateUpdatedAt(checklist.getUpdatedAt(), id);
            return checklist;
        } else {
            LOGGER.error("Checklist with id {} was not found", id);
            throw new ChecklistException(ChecklistError.CHECKLIST_NOT_FOUND);
        }
    }

    public List<Checklist> getAllChecklist() {
        LOGGER.info("Getting all Checklists");
        return checkListRepository.findAll();
    }

    public Optional<Checklist> getChecklistById(UUID id) {
        LOGGER.info("Getting Checklist with id: {}", id);
        return checkListRepository.findById(id);
    }

    public void deleteChecklistById(UUID id) throws ChecklistException {
        var checkListOpt = checkListRepository.findById(id);
        if (checkListOpt.isPresent()) {
            LOGGER.info("Deleting checklist with id: {}", id);
            checkListRepository.deleteById(id);
        } else {
            LOGGER.error("Checklist with id {} was not found", id);
            throw new ChecklistException(ChecklistError.CHECKLIST_NOT_FOUND);
        }
    }

    public List<ChecklistItem> getChecklistItems(UUID id) throws ChecklistException {
        LOGGER.info("Getting checklist items for checklist with id: {}", id);
        var checkListOpt = checkListRepository.findById(id);
        if (checkListOpt.isPresent()) {
            return checkListItemRepository.findChecklistItemsByChecklistId(id);
        } else {
            throw new ChecklistException(ChecklistError.CHECKLIST_NOT_FOUND);
        }
    }

    public ChecklistItem createChecklistItem(UUID id, ChecklistItemDto checklistItemDto) throws ChecklistException {
        LOGGER.info("Creating checklist item for checklist with id: {}", id);
        var checkListOpt = checkListRepository.findById(id);
        if (checkListOpt.isPresent()) {
            var checklistItem = ChecklistItemMapper.toEntity(checkListOpt.get(), checklistItemDto);
            checklistItem.setChecklist(checkListOpt.get());
            return checkListItemRepository.save(checklistItem);
        } else {
            throw new ChecklistException(ChecklistError.CHECKLIST_NOT_FOUND);
        }
    }
}
