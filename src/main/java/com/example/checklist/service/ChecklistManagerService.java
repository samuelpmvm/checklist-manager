package com.example.checklist.service;

import com.example.checklist.entities.Checklist;
import com.example.checklist.exception.ChecklistError;
import com.example.checklist.exception.ChecklistException;
import com.example.checklist.mapper.ChecklistMapper;
import com.example.checklist.repository.ChecklistRepository;
import org.openapitools.model.ChecklistDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ChecklistManagerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChecklistManagerService.class);

    private final ChecklistRepository checkListRepository;

    public ChecklistManagerService(ChecklistRepository checkListRepository) {
        this.checkListRepository = checkListRepository;
    }

    public Checklist createCheckList(ChecklistDto checklistDto) throws ChecklistException {
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

    public List<Checklist> getAllCheckList() {
        return checkListRepository.findAll();
    }

}
