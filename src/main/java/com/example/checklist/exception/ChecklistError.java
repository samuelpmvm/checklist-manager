package com.example.checklist.exception;

public enum ChecklistError {
    BAD_REQUEST_ERROR("Bad Request error", 400, "Bad Request error"),
    CHECKLIST_NOT_FOUND("Checklist not found", 401, "Checklist with id {0}"),
    CHECKLIST_ALREADY_EXISTS("Checklist already exists", 402, "Checklist with title {0} and version {1} already exists"),

    GENERIC_ERROR("Generic error", 500, "An unexpected error occurred"),
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
