package com.checklist.controller;

import com.checklist.entities.CheckListTag;
import com.checklist.entities.Checklist;
import com.checklist.entities.ChecklistItem;
import com.checklist.exception.ChecklistError;
import com.checklist.exception.ChecklistException;
import com.checklist.jwt.JwtUtil;
import com.checklist.mapper.ChecklistMapper;
import com.checklist.resources.Status;
import com.checklist.service.AppUserDetailsService;
import com.checklist.service.ChecklistManagerService;
import com.model.checklist.ChecklistDto;
import com.model.checklist.ChecklistItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ChecklistManagerControllerTest {

    private static final String TITLE = "title";
    private static final String ENVIRONMENT = "test";
    private static final String TAG = "tag1";
    private static final int VERSION = 1;
    private static final String DESCRIPTION = "description";
    private static final String ID = "0d75f424-0ee4-48f8-83cd-c2067ab0c9bb";
    private static final String CHECKLIST_ITEM_ID = "0d75f424-0ee4-48f8-83cd-c2067ab0c9bb";
    private static final String APPLICATION_ERROR_CHECKLIST_V_1_JSON = "application/error-checklist-v1+json";
    private static final String ADMIN = "admin";
    private static final String TOKEN = "mockToken";

    @MockitoBean
    private ChecklistManagerService checkListManagerService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        UserDetails userDetails = User
                .withUsername(ADMIN)
                .password(ADMIN)
                .build();

        Mockito.when(appUserDetailsService.loadUserByUsername(ADMIN)).thenReturn(userDetails);
        Mockito.when(jwtUtil.getUserNameFromToken(TOKEN)).thenReturn(ADMIN);
        Mockito.when(jwtUtil.validateToken(TOKEN, userDetails)).thenReturn(true);
    }

    @Test
    void createChecklistSucceeded() throws Exception {

        Mockito.when(checkListManagerService.createChecklist(ArgumentMatchers.any(ChecklistDto.class)))
                .thenReturn(createChecklist());
        final var request = MockMvcRequestBuilders
                .post("/api/v1/checklist")
                .contentType(ChecklistManagerController.APPLICATION_CHECKLIST_REQUEST_V_1_JSON)
                .header("Authorization", "Bearer " + TOKEN)
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
                .andDo(print())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.title").value(TITLE))
                .andExpect(jsonPath("$.environment").value(ENVIRONMENT))
                .andExpect(jsonPath("$.tags[0].tag").value(TAG))
                .andExpect(jsonPath("$.version").value(VERSION))
                .andExpect(jsonPath("$.items[0].description").value(DESCRIPTION))
                .andExpect(jsonPath("$.items[0].status").value(Status.DONE.toString()));
    }

    @Test
    void updateChecklistSucceeded() throws Exception {
        var checklistDto = ChecklistMapper.toDto(createChecklist());
        Mockito.when(checkListManagerService.updateChecklist(
                        ArgumentMatchers.eq(UUID.fromString(ID)),
                        ArgumentMatchers.any(ChecklistDto.class)))
                .thenReturn(checklistDto);
        final var request = MockMvcRequestBuilders
                .put(String.format("/api/v1/checklist/%s", ID))
                .contentType(ChecklistManagerController.APPLICATION_CHECKLIST_REQUEST_V_1_JSON)
                .header("Authorization", "Bearer " + TOKEN)
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
                .andDo(print())
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
                .thenReturn(List.of(ChecklistMapper.toDto(createChecklist())));
        final var request = MockMvcRequestBuilders
                .get("/api/v1/checklist")
                .contentType(ChecklistManagerController.APPLICATION_CHECKLIST_REQUEST_V_1_JSON)
                .header("Authorization", "Bearer " + TOKEN);

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
                .thenReturn(Optional.of(ChecklistMapper.toDto(createChecklist())));
        final var request = MockMvcRequestBuilders
                .get(String.format("/api/v1/checklist/%s", ID))
                .header("Authorization", "Bearer " + TOKEN);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(ChecklistManagerController.APPLICATION_CHECKLIST_V_1_JSON))
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
        final var request = MockMvcRequestBuilders
                .get(String.format("/api/v1/checklist/%s", ID))
                .header("Authorization", "Bearer " + TOKEN);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void getCheckListByIdBadRequest() throws Exception {
        final var request = MockMvcRequestBuilders
                .get(String.format("/api/v1/checklist/%s", "1"))
                .header("Authorization", "Bearer " + TOKEN);
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_ERROR_CHECKLIST_V_1_JSON));
    }

    @Test
    void getChecklistItemsSucceeded() throws Exception {
        var checkListItem = new ChecklistItem();
        checkListItem.setId(UUID.fromString(ID));
        checkListItem.setDescription(DESCRIPTION);
        checkListItem.setStatus(Status.DONE);
        checkListItem.setChecklist(createChecklist());
        Mockito.when(checkListManagerService.getChecklistItems(UUID.fromString(ID)))
                .thenReturn(Collections.singletonList(checkListItem));
        final var request = MockMvcRequestBuilders
                .get(String.format("/api/v1/checklist/%s/items", ID))
                .header("Authorization", "Bearer " + TOKEN);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(ChecklistManagerController.APPLICATION_CHECKLIST_ITEM_V_1_JSON))
                .andExpect(jsonPath("$[0].id").value(ID))
                .andExpect(jsonPath("$[0].description").value(DESCRIPTION))
                .andExpect(jsonPath("$[0].status").value(Status.DONE.toString()));
    }

    @Test
    void getChecklistItemsNotFound() throws Exception {
        Mockito.when(checkListManagerService.getChecklistItems(UUID.fromString(ID)))
                .thenThrow(new ChecklistException(ChecklistError.CHECKLIST_NOT_FOUND));
        final var request = MockMvcRequestBuilders
                .get(String.format("/api/v1/checklist/%s/items", ID))
                .header("Authorization", "Bearer " + TOKEN);

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_ERROR_CHECKLIST_V_1_JSON));
    }

    @Test
    void createChecklistItemSucceeded() throws Exception {
        var checkListItem = new ChecklistItem();
        checkListItem.setId(UUID.fromString(ID));
        checkListItem.setDescription(DESCRIPTION);
        checkListItem.setStatus(Status.DONE);
        checkListItem.setChecklist(createChecklist());
        Mockito.when(checkListManagerService.createChecklistItem(ArgumentMatchers.eq(UUID.fromString(ID)), ArgumentMatchers.any(ChecklistItemDto.class)))
                .thenReturn(checkListItem);
        final var request = MockMvcRequestBuilders
                .post(String.format("/api/v1/checklist/%s/items", ID))
                .contentType(ChecklistManagerController.APPLICATION_CHECKLIST_ITEM_REQUEST_V_1_JSON)
                .accept(ChecklistManagerController.APPLICATION_CHECKLIST_ITEM_V_1_JSON)
                .header("Authorization", "Bearer " + TOKEN)
                .content("""
                        {
                         "description": "%s",
                         "status": "DONE"
                        }
                        """.formatted(DESCRIPTION));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(ChecklistManagerController.APPLICATION_CHECKLIST_ITEM_V_1_JSON))
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.description").value(DESCRIPTION))
                .andExpect(jsonPath("$.status").value(Status.DONE.toString()));
    }

    @Test
    void createChecklistItemFails() throws Exception {
        Mockito.when(checkListManagerService.createChecklistItem(ArgumentMatchers.eq(UUID.fromString(ID)), ArgumentMatchers.any(ChecklistItemDto.class)))
                .thenThrow(new ChecklistException(ChecklistError.CHECKLIST_NOT_FOUND));
        final var request = MockMvcRequestBuilders
                .post(String.format("/api/v1/checklist/%s/items", ID))
                .contentType(ChecklistManagerController.APPLICATION_CHECKLIST_ITEM_REQUEST_V_1_JSON)
                .accept(ChecklistManagerController.APPLICATION_CHECKLIST_ITEM_V_1_JSON)
                .header("Authorization", "Bearer " + TOKEN)
                .content("""
                        {
                         "description": "%s",
                         "status": "DONE"
                        }
                        """.formatted(DESCRIPTION));
        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_ERROR_CHECKLIST_V_1_JSON));
    }

    @Test
    void updateChecklistItemSucceeded() throws Exception {
        var checkListItem = new ChecklistItem();
        checkListItem.setId(UUID.fromString(ID));
        checkListItem.setDescription(DESCRIPTION);
        checkListItem.setStatus(Status.DONE);
        checkListItem.setChecklist(createChecklist());
        Mockito.when(checkListManagerService.updateChecklistItem(
                        ArgumentMatchers.eq(UUID.fromString(ID)),
                        ArgumentMatchers.eq(UUID.fromString(CHECKLIST_ITEM_ID)),
                        ArgumentMatchers.any(ChecklistItemDto.class)))
                .thenReturn(checkListItem);
        final var request = MockMvcRequestBuilders
                .put(String.format("/api/v1/checklist/%s/items/%s", ID, CHECKLIST_ITEM_ID))
                .contentType(ChecklistManagerController.APPLICATION_CHECKLIST_ITEM_REQUEST_V_1_JSON)
                .accept(ChecklistManagerController.APPLICATION_CHECKLIST_ITEM_V_1_JSON)
                .header("Authorization", "Bearer " + TOKEN)
                .content("""
                        {
                         "description": "%s",
                         "status": "DONE"
                        }
                        """.formatted(DESCRIPTION));
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(ChecklistManagerController.APPLICATION_CHECKLIST_ITEM_V_1_JSON))
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.description").value(DESCRIPTION))
                .andExpect(jsonPath("$.status").value(Status.DONE.toString()));
    }

    @Test
    void updateChecklistItemFails() throws Exception {
        Mockito.when(checkListManagerService.updateChecklistItem(
                        ArgumentMatchers.eq(UUID.fromString(ID)),
                        ArgumentMatchers.eq(UUID.fromString(CHECKLIST_ITEM_ID)),
                        ArgumentMatchers.any(ChecklistItemDto.class)))
                .thenThrow(new ChecklistException(ChecklistError.CHECKLIST_NOT_FOUND));
        final var request = MockMvcRequestBuilders
                .put(String.format("/api/v1/checklist/%s/items/%s", ID, CHECKLIST_ITEM_ID))
                .contentType(ChecklistManagerController.APPLICATION_CHECKLIST_ITEM_REQUEST_V_1_JSON)
                .accept(ChecklistManagerController.APPLICATION_CHECKLIST_ITEM_V_1_JSON)
                .header("Authorization", "Bearer " + TOKEN)
                .content("""
                        {
                         "description": "%s",
                         "status": "DONE"
                        }
                        """.formatted(DESCRIPTION));
        mockMvc.perform(request)
                .andExpect(status().isNotFound())
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
                .thenReturn(Optional.of(ChecklistMapper.toDto(createChecklist())));

        final var request = MockMvcRequestBuilders.delete(String.format("/api/v1/checklist/%s", ID))
                .header("Authorization", "Bearer " + TOKEN);

        mockMvc.perform(request).andExpect(status().isNoContent());
    }

    @Test
    void deleteCheckListByIdNotFound() throws Exception {

        Mockito.doThrow(new ChecklistException(ChecklistError.CHECKLIST_NOT_FOUND))
                .when(checkListManagerService)
                .deleteChecklistById(UUID.fromString(ID));
        final var request = MockMvcRequestBuilders
                .delete(String.format("/api/v1/checklist/%s", ID))
                .header("Authorization", "Bearer " + TOKEN);

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_ERROR_CHECKLIST_V_1_JSON));
    }
}