package com.example.checklist.controller;

import com.example.checklist.entities.Checklist;
import com.example.checklist.entities.ChecklistItem;
import com.example.checklist.entities.Status;
import com.example.checklist.service.ChecklistManagerService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openapitools.model.ChecklistDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChecklistManagerController.class)
class ChecklistManagerControllerTest {

    private static final String TITLE = "title";
    private static final String ENVIRONMENT = "test";
    private static final Set<String> TAG_SET = Set.of("tag1", "tag2");
    private static final String VERSION = "1.0.0";
    private static final String DESCRIPTION = "description";
    private static final String ID = "0d75f424-0ee4-48f8-83cd-c2067ab0c9bb";

    @MockitoBean
    private ChecklistManagerService checkListManagerService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createChecklistSucceeded() throws Exception {

        Mockito.when(checkListManagerService.createCheckList(ArgumentMatchers.any(ChecklistDto.class))).thenReturn(createChecklist());

        final var request = MockMvcRequestBuilders
                .post("/api/v1/checklist")
                .contentType(ChecklistManagerController.APPLICATION_CHECKLIST_REQUEST_V_1_JSON)
                .accept(ChecklistManagerController.APPLICATION_CHECKLIST_V_1_JSON)
                .content("""
                        {
                          "title": "%s",
                          "environment": "%s",
                          "tags": [
                            "%s",
                            "%s"
                          ],
                          "version": "%s",
                          "items": [
                            {
                              "description": "%s",
                              "status": "DONE"
                            }
                          ]
                        }
                        """.formatted(TITLE, ENVIRONMENT, TAG_SET.toArray()[0], TAG_SET.toArray()[1], VERSION, DESCRIPTION));
        mockMvc.perform(request).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.title").value(TITLE))
                .andExpect(jsonPath("$.environment").value(ENVIRONMENT))
                .andExpect(jsonPath("$.tags[0]").value(TAG_SET.toArray()[0]))
                .andExpect(jsonPath("$.tags[1]").value(TAG_SET.toArray()[1]))
                .andExpect(jsonPath("$.version").value(VERSION))
                .andExpect(jsonPath("$.items[0].description").value(DESCRIPTION))
                .andExpect(jsonPath("$.items[0].status").value(Status.DONE.toString()));
    }

    private static Checklist createChecklist() {
        var checklist = new Checklist();
        checklist.setId(UUID.fromString(ID));
        checklist.setTitle(TITLE);
        checklist.setEnvironment(ENVIRONMENT);
        checklist.setTags(TAG_SET);
        checklist.setVersion(VERSION);

        var checkListItem = new ChecklistItem();
        checkListItem.setDescription(DESCRIPTION);
        checkListItem.setStatus(Status.DONE);
        checklist.setItems(List.of(checkListItem));

        return checklist;
    }
}