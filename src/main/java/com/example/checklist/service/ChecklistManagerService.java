package com.example.checklist.service;

import com.example.checklist.entities.Checklist;
import com.example.checklist.mapper.ChecklistMapper;
import com.example.checklist.repository.ChecklistRepository;
import org.openapitools.model.ChecklistDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChecklistManagerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChecklistManagerService.class);

    private final ChecklistRepository checkListRepository;

    public ChecklistManagerService(ChecklistRepository checkListRepository) {
        this.checkListRepository = checkListRepository;
    }

    public Checklist createCheckList(ChecklistDto checklistDto) {
        LOGGER.info("Creating checklist with title: {}", checklistDto.getTitle());
        var checkList = ChecklistMapper.toEntity(checklistDto);

        return checkListRepository.save(checkList);
    }

}
