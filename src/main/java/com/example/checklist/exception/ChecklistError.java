package com.example.checklist.exception;

public enum ChecklistError {
    GENERIC_ERROR("Generic error", 100, "An unexpected error occurred"),

    CHECKLIST_NOT_FOUND("Checklist not found", 1001, "Checklist with title {0}  and version {1} not found"),
    ;

    private final String errorTitle;
    private final int errorCode;
    private final String errorMessage;

    ChecklistError(String errorTitle, int errorCode, String errorMessage) {
        this.errorTitle = errorTitle;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorTitle() {
        return errorTitle;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
