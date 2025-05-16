package com.example.checklist.exception;

public enum ChecklistError {
    GENERIC_ERROR("Generic error", 100, "An unexpected error occurred"),
    BAD_REQUEST_ERROR("Bad Request error", 101, "Bad Request error"),

    CHECKLIST_NOT_FOUND("Checklist not found", 1001, "Checklist with id {0}"),
    CHECKLIST_ALREADY_EXISTS("Checklist already exists", 1002, "Checklist with title {0} and version {1} already exists"),
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
