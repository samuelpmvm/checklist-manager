package com.example.checklist.exception;

import java.io.Serial;

public final class ChecklistException extends Exception {
    @Serial
    private static final long serialVersionUID = -8840146801390279753L;

    private final ChecklistError checklistError;

    public ChecklistException(ChecklistError checklistError, Exception exception) {
        this.checklistError = checklistError;
        initCause(exception);
    }

    public ChecklistException(ChecklistError checklistError) {
        this.checklistError = checklistError;
    }

    public ChecklistError getChecklistError() {
        return checklistError;
    }
}
