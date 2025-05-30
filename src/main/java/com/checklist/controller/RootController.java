package com.checklist.controller;

import com.checklist.resources.ChecklistResource;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RootController {
    private static final String  CHECKLIST_MANAGER_API = "Checklist Manager API";
    private static final String VERSION = "v1";

    @GetMapping
    public ResponseEntity<ChecklistResource> root() {
        var checklistResource = new ChecklistResource(CHECKLIST_MANAGER_API, VERSION);
        checklistResource.addLink(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RootController.class).root()).withSelfRel());
        checklistResource.addLink(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RootController.class).rootForChecklistAPI()).withRel("v1"));
        checklistResource.addLink(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RootController.class).rootForChecklistAPI()).slash("docs").withRel("documentation"));

        return ResponseEntity.ok(checklistResource);
    }

    @GetMapping("/api/v1")
    public ResponseEntity<ChecklistResource> rootForChecklistAPI() {
        var checklistResource = new ChecklistResource(CHECKLIST_MANAGER_API, VERSION);
        checklistResource.addLink(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RootController.class).rootForChecklistAPI()).withSelfRel());
        checklistResource.addLink(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class).registerUser(null)).withRel("registerUser"));

        return ResponseEntity.ok(checklistResource);
    }
}
