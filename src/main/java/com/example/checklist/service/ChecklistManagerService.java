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

@Service
public class ChecklistManagerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChecklistManagerService.class);

    private final ChecklistRepository checkListRepository;

    public ChecklistManagerService(ChecklistRepository checkListRepository) {
        this.checkListRepository = checkListRepository;
    }

    public Checklist createCheckList(ChecklistDto checklistDto) throws ChecklistException {
        LOGGER.info("Creating checklist with title: {}", checklistDto.getTitle());
        try {
            var checkList = ChecklistMapper.toEntity(checklistDto);
            checkListRepository
                    .findByTitleAndVersion(checklistDto.getTitle(), checklistDto.getVersion())
                    .ifPresentOrElse(oldChecklist -> {
                        LOGGER.info("Checklist with title {} and version {} already exists, updating it", checkList.getTitle(), checkList.getVersion());
                        checkList.setCreatedAt(oldChecklist.getCreatedAt());
                        checkList.setUpdatedAt(OffsetDateTime.now());
                        checkListRepository.updateUpdatedAt(checkList.getUpdatedAt(), checkList.getTitle(), checkList.getVersion());
                    }, () -> {
                        LOGGER.info("Checklist with title {} and version {} does not exist, creating a new one", checklistDto.getTitle(), checklistDto.getVersion());
                        var date = OffsetDateTime.now();
                        checkList.setCreatedAt(date);
                        checkList.setUpdatedAt(date);
                        checkListRepository.save(checkList);
                    });
            return checkList;
        }
        catch (Exception exception) {
            throw new ChecklistException(ChecklistError.GENERIC_ERROR, exception);
        }
    }

    public List<Checklist> getAllCheckList() {
        return checkListRepository.findAll();
    }

}
