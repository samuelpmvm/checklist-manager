package com.example.checklist.controller;

import com.example.checklist.entities.CheckListTag;
import com.example.checklist.entities.Checklist;
import com.example.checklist.entities.ChecklistItem;
import com.example.checklist.exception.ChecklistError;
import com.example.checklist.exception.ChecklistException;
import com.example.checklist.exception.GlobalExceptionHandler;
import com.example.checklist.resources.Status;
import com.example.checklist.service.ChecklistManagerService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openapitools.model.ChecklistDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChecklistManagerController.class)
@Import(GlobalExceptionHandler.class)
class ChecklistManagerControllerTest {

    private static final String TITLE = "title";
    private static final String ENVIRONMENT = "test";
    private static final String TAG = "tag1";
    private static final String VERSION = "1.0.0";
    private static final String DESCRIPTION = "description";
    private static final String ID = "0d75f424-0ee4-48f8-83cd-c2067ab0c9bb";
    public static final String APPLICATION_ERROR_CHECKLIST_V_1_JSON = "application/error-checklist-v1+json";

    @MockitoBean
    private ChecklistManagerService checkListManagerService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createChecklistSucceeded() throws Exception {

        Mockito.when(checkListManagerService.createChecklist(ArgumentMatchers.any(ChecklistDto.class)))
                .thenReturn(createChecklist());
        final var request = MockMvcRequestBuilders
                .post("/api/v1/checklist")
                .contentType(ChecklistManagerController.APPLICATION_CHECKLIST_REQUEST_V_1_JSON)
                .accept(ChecklistManagerController.APPLICATION_CHECKLIST_V_1_JSON)
                .content("""
                        {
                          "title": "%s",
                          "environment": "%s",
                          "tags": [
                            {
                              "tag": "%s"
                            }
                          ],
                          "version": "%s",
                          "items": [
                            {
                              "description": "%s",
                              "status": "DONE"
                            }
                          ]
                        }
                        """.formatted(TITLE, ENVIRONMENT, TAG, VERSION, DESCRIPTION));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.title").value(TITLE))
                .andExpect(jsonPath("$.environment").value(ENVIRONMENT))
                .andExpect(jsonPath("$.tags[0].tag").value(TAG))
                .andExpect(jsonPath("$.version").value(VERSION))
                .andExpect(jsonPath("$.items[0].description").value(DESCRIPTION))
                .andExpect(jsonPath("$.items[0].status").value(Status.DONE.toString()));
    }

    @Test
    void createChecklistAlreadyExistsFails() throws Exception {

        Mockito.doThrow(new ChecklistException(ChecklistError.CHECKLIST_ALREADY_EXISTS))
                .when(checkListManagerService)
                .createChecklist(ArgumentMatchers.any(ChecklistDto.class));
        final var request = MockMvcRequestBuilders
                .post("/api/v1/checklist")
                .contentType(ChecklistManagerController.APPLICATION_CHECKLIST_REQUEST_V_1_JSON)
                .accept(ChecklistManagerController.APPLICATION_CHECKLIST_V_1_JSON)
                .content("""
                        {
                          "title": "%s",
                          "environment": "%s",
                          "tags": [
                            {
                              "tag": "%s"
                            }
                          ],
                          "version": "%s",
                          "items": [
                            {
                              "description": "%s",
                              "status": "DONE"
                            }
                          ]
                        }
                        """.formatted(TITLE, ENVIRONMENT, TAG, VERSION, DESCRIPTION));
        var checklistError = ChecklistError.CHECKLIST_ALREADY_EXISTS;
        mockMvc.perform(request)
                .andExpect(status().is(checklistError.getErrorCode()))
                .andExpect(content().contentType(APPLICATION_ERROR_CHECKLIST_V_1_JSON))
                .andExpect(jsonPath("$.title").value(checklistError.getErrorTitle()))
                .andExpect(jsonPath("$.code").value(checklistError.getErrorCode()))
                .andExpect(jsonPath("$.message").value(checklistError.getErrorMessage()));
    }

    @Test
    void updateChecklistSucceeded() throws Exception {

        Mockito.when(checkListManagerService.updateChecklist(UUID.fromString(ID)))
                .thenReturn(createChecklist());
        final var request = MockMvcRequestBuilders
                .put(String.format("/api/v1/checklist/%s", ID))
                .contentType(ChecklistManagerController.APPLICATION_CHECKLIST_REQUEST_V_1_JSON)
                .accept(ChecklistManagerController.APPLICATION_CHECKLIST_V_1_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.title").value(TITLE))
                .andExpect(jsonPath("$.environment").value(ENVIRONMENT))
                .andExpect(jsonPath("$.tags[0].tag").value(TAG))
                .andExpect(jsonPath("$.version").value(VERSION))
                .andExpect(jsonPath("$.items[0].description").value(DESCRIPTION))
                .andExpect(jsonPath("$.items[0].status").value(Status.DONE.toString()));
    }

    @Test
    void getCheckListsSucceeded() throws Exception {

        Mockito.when(checkListManagerService.getAllChecklist())
                .thenReturn(List.of(createChecklist()));
        final var request = MockMvcRequestBuilders
                .get("/api/v1/checklist")
                .contentType(ChecklistManagerController.APPLICATION_CHECKLIST_REQUEST_V_1_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(ID))
                .andExpect(jsonPath("$.[0].title").value(TITLE))
                .andExpect(jsonPath("$.[0].environment").value(ENVIRONMENT))
                .andExpect(jsonPath("$.[0].tags[0].tag").value(TAG))
                .andExpect(jsonPath("$.[0].version").value(VERSION))
                .andExpect(jsonPath("$.[0].items[0].description").value(DESCRIPTION))
                .andExpect(jsonPath("$.[0].items[0].status").value(Status.DONE.toString()));
    }

    @Test
    void getCheckListByIdSucceeded() throws Exception {

        Mockito.when(checkListManagerService.getChecklistById(UUID.fromString(ID)))
                .thenReturn(Optional.of(createChecklist()));
        final var request = MockMvcRequestBuilders.get(String.format("/api/v1/checklist/%s", ID));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.title").value(TITLE))
                .andExpect(jsonPath("$.environment").value(ENVIRONMENT))
                .andExpect(jsonPath("$.tags[0].tag").value(TAG))
                .andExpect(jsonPath("$.version").value(VERSION))
                .andExpect(jsonPath("$.items[0].description").value(DESCRIPTION))
                .andExpect(jsonPath("$.items[0].status").value(Status.DONE.toString()));
    }

    @Test
    void getCheckListByIdNotFound() throws Exception {

        Mockito.when(checkListManagerService.getChecklistById(UUID.fromString(ID)))
                .thenReturn(Optional.empty());
        final var request = MockMvcRequestBuilders.get(String.format("/api/v1/checklist/%s", ID));

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void getCheckListByIdBadRequest() throws Exception {
        final var request = MockMvcRequestBuilders.get(String.format("/api/v1/checklist/%s", "1"));
        mockMvc.perform(request)
                .andExpect(status().is(ChecklistError.BAD_REQUEST_ERROR.getErrorCode()))
                .andExpect(content().contentType(APPLICATION_ERROR_CHECKLIST_V_1_JSON));
    }

    private static Checklist createChecklist() {
        var checklist = new Checklist();
        checklist.setId(UUID.fromString(ID));
        checklist.setTitle(TITLE);
        checklist.setEnvironment(ENVIRONMENT);
        checklist.setVersion(VERSION);

        var checkListTag = new CheckListTag();
        checkListTag.setTag(TAG);
        checklist.setTags(List.of(checkListTag));

        var checkListItem = new ChecklistItem();
        checkListItem.setDescription(DESCRIPTION);
        checkListItem.setStatus(Status.DONE);
        checklist.setItems(List.of(checkListItem));

        return checklist;
    }

    @Test
    void deleteCheckListByIdSucceeded() throws Exception {
        Mockito.when(checkListManagerService.getChecklistById(UUID.fromString(ID)))
                .thenReturn(Optional.of(createChecklist()));
        final var request = MockMvcRequestBuilders.delete(String.format("/api/v1/checklist/%s", ID));

        mockMvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    void deleteCheckListByIdNotFound() throws Exception {

        Mockito.doThrow(new ChecklistException(ChecklistError.CHECKLIST_NOT_FOUND))
                .when(checkListManagerService)
                .deleteChecklistById(UUID.fromString(ID));
        final var request = MockMvcRequestBuilders.delete(String.format("/api/v1/checklist/%s", ID));

        mockMvc.perform(request)
                .andExpect(status().is(ChecklistError.CHECKLIST_NOT_FOUND.getErrorCode()))
                .andExpect(content().contentType(APPLICATION_ERROR_CHECKLIST_V_1_JSON));
    }
}