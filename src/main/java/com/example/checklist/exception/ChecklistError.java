package com.example.checklist.exception;

public enum ChecklistError {
    BAD_REQUEST_ERROR("Bad Request error", 1000, "Bad Request error"),
    UNAUTHORIZED_USER("Unauthorized User", 1001, "Only user with ADMIN role can access this resource"),
    METHOD_NOT_ALLOWED("Method not allowed", 1002, "Method not allowed for this request"),
    MEDIA_TYPE_NOT_SUPPORTED("Media type not supported", 1003, "Media type not supported"),
    RESOURCE_NOT_FOUND("Resource not found", 1004, "Resource not found"),
    METHOD_ARG_NOT_VALID("Method Argument not valid", 1005, "Method argument is not valid"),

    CHECKLIST_NOT_FOUND("Checklist not found", 2000, "Checklist with id {0}"),
    CHECKLIST_ALREADY_EXISTS("Checklist already exists", 2001, "Checklist with title {0} and version {1} already exists"),

    GENERIC_ERROR("Generic error", 3000, "An unexpected error occurred. For more information please check the logs"),
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
