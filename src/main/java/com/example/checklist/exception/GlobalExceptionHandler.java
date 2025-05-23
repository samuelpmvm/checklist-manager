package com.example.checklist.exception;

import com.example.model.checklist.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        LOGGER.error("Http request method not supported exception:{}", exception.getMessage());

        var errorResponse = getErrorResponse(
                ChecklistError.METHOD_NOT_ALLOWED.getErrorTitle(),
                ChecklistError.METHOD_NOT_ALLOWED.getErrorCode(),
                ChecklistError.METHOD_NOT_ALLOWED.getErrorMessage());
        return generateResponse(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        LOGGER.error("Http media type not supported exception:{}", exception.getMessage());
        var errorResponse = getErrorResponse(
                ChecklistError.MEDIA_TYPE_NOT_SUPPORTED.getErrorTitle(),
                ChecklistError.MEDIA_TYPE_NOT_SUPPORTED.getErrorCode(),
                ChecklistError.MEDIA_TYPE_NOT_SUPPORTED.getErrorMessage());
        return generateResponse(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        LOGGER.error("Http message not readable exception: {}", exception.getMessage());
        var errorResponse = getErrorResponse(
                ChecklistError.UNAUTHORIZED_USER.getErrorTitle(),
                ChecklistError.UNAUTHORIZED_USER.getErrorCode(),
                ChecklistError.UNAUTHORIZED_USER.getErrorMessage());
        return generateResponse(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(NoResourceFoundException exception) {
        LOGGER.error("Resource not found exception: {}", exception.getMessage());
        var errorResponse = getErrorResponse(
                ChecklistError.RESOURCE_NOT_FOUND.getErrorTitle(),
                ChecklistError.RESOURCE_NOT_FOUND.getErrorCode(),
                ChecklistError.RESOURCE_NOT_FOUND.getErrorMessage());
        return generateResponse(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        LOGGER.error("Method argument is not valid: {}", exception.getMessage());
        var errorResponse = getErrorResponse(
                ChecklistError.METHOD_ARG_NOT_VALID.getErrorTitle(),
                ChecklistError.METHOD_ARG_NOT_VALID.getErrorCode(),
                ChecklistError.METHOD_ARG_NOT_VALID.getErrorMessage());
        return generateResponse(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
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
