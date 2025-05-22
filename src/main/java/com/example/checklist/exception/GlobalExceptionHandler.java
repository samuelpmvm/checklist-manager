package com.example.checklist.exception;

import com.example.model.checklist.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
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

        var httpStatus = getHttpStatusForCheckListException(checklistError);
        return generateResponse(errorResponse, httpStatus);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        LOGGER.error("Illegal Argument exception found: {}", ex.getMessage());
        var errorResponse = getErrorResponse(
                ChecklistError.BAD_REQUEST_ERROR.getErrorTitle(),
                ChecklistError.BAD_REQUEST_ERROR.getErrorCode(),
                ChecklistError.BAD_REQUEST_ERROR.getErrorMessage());
        return generateResponse(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        LOGGER.error("Exception found: {}", ex.getMessage());
        var errorResponse = getErrorResponse(
                ChecklistError.GENERIC_ERROR.getErrorTitle(),
                ChecklistError.GENERIC_ERROR.getErrorCode(),
                ChecklistError.GENERIC_ERROR.getErrorMessage());
        return generateResponse(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(Exception ex) {
        LOGGER.error("Access Denied exception found: {}", ex.getMessage());
        var errorResponse = getErrorResponse(
                ChecklistError.UNAUTHORIZED_USER.getErrorTitle(),
                ChecklistError.UNAUTHORIZED_USER.getErrorCode(),
                ChecklistError.UNAUTHORIZED_USER.getErrorMessage());
        return generateResponse(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    private static ErrorResponse getErrorResponse(String title, int code, String message) {
        var errorResponse = new ErrorResponse();
        errorResponse.setTitle(title);
        errorResponse.setCode(code);
        errorResponse.setMessage(message);
        return errorResponse;
    }

    private ResponseEntity<ErrorResponse> generateResponse(ErrorResponse errorResponse, HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus)
                .contentType(MediaType.valueOf(APPLICATION_ERROR_CHECKLIST_V_1_JSON))
                .body(errorResponse);
    }

    private static HttpStatus getHttpStatusForCheckListException(ChecklistError checklistError) {
        var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (checklistError == ChecklistError.CHECKLIST_NOT_FOUND) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (checklistError == ChecklistError.CHECKLIST_ALREADY_EXISTS) {
            httpStatus = HttpStatus.CONFLICT;
        }
        return httpStatus;
    }
}
