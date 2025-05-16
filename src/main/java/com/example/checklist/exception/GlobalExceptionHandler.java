package com.example.checklist.exception;

import org.openapitools.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    static final String APPLICATION_ERROR_CHECKLIST_V_1_JSON = "application/error-checklist-v1+json";

    @ExceptionHandler(ChecklistException.class)
    public ResponseEntity<ErrorResponse> handleCheckListException(ChecklistException exception) {
        var checklistError = exception.getChecklistError();
        var errorResponse = getErrorResponse(
                checklistError.getErrorTitle(),
                checklistError.getErrorCode(),
                checklistError.getErrorMessage());
        return ResponseEntity.status(errorResponse.getCode())
                .contentType(MediaType.valueOf(APPLICATION_ERROR_CHECKLIST_V_1_JSON))
                .body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        var errorResponse = getErrorResponse(
                ChecklistError.BAD_REQUEST_ERROR.getErrorTitle(),
                ChecklistError.BAD_REQUEST_ERROR.getErrorCode(),
                ChecklistError.BAD_REQUEST_ERROR.getErrorMessage());
        return ResponseEntity.status(errorResponse.getCode())
                .contentType(MediaType.valueOf(APPLICATION_ERROR_CHECKLIST_V_1_JSON))
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        LOGGER.error("Exception found: {}", ex.getMessage());
        var errorResponse = getErrorResponse(
                ChecklistError.GENERIC_ERROR.getErrorTitle(),
                ChecklistError.GENERIC_ERROR.getErrorCode(),
                ChecklistError.GENERIC_ERROR.getErrorMessage());
        return ResponseEntity.status(errorResponse.getCode())
                .contentType(MediaType.valueOf(APPLICATION_ERROR_CHECKLIST_V_1_JSON))
                .body(errorResponse);
    }

    private ErrorResponse getErrorResponse(String title, int code, String message) {
        var errorResponse = new ErrorResponse();
        errorResponse.setTitle(title);
        errorResponse.setCode(code);
        errorResponse.setMessage(message);
        return errorResponse;
    }
}
