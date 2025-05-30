package com.checklist.resources;

import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;

public class ChecklistResource {
    private String name;
    private String version;
    private final List<Link> links = new ArrayList<>();

    public ChecklistResource(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void addLink(Link link) {
        if (link != null) {
            links.add(link);
        }
    }
}
