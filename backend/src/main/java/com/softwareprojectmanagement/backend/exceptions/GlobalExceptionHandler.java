package com.softwareprojectmanagement.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.softwareprojectmanagement.backend.dto.ErrorResponse;

/**
 * Global exception handler for the application.
 * This class uses @ControllerAdvice to handle exceptions globally and return
 * error responses with the exception message in the response body.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ApplicationException and returns an error response with status 400 (Bad Request).
     * This method is invoked whenever an ApplicationException is thrown in any controller.
     *
     * @param ex the ApplicationException that was thrown
     * @param request the WebRequest object containing information about the request
     * @return a ResponseEntity containing the ErrorResponse with the exception message
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles generic Exception and returns an error response with status 500 (Internal Server Error).
     * This is a fallback handler for any exception not specifically handled.
     *
     * @param ex the Exception that was thrown
     * @param request the WebRequest object containing information about the request
     * @return a ResponseEntity containing the ErrorResponse with the exception message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles IllegalArgumentException and returns an error response with status 400 (Bad Request).
     * This is thrown when invalid arguments are passed to methods.
     *
     * @param ex the IllegalArgumentException that was thrown
     * @param request the WebRequest object containing information about the request
     * @return a ResponseEntity containing the ErrorResponse with the exception message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage() != null ? ex.getMessage() : "Invalid argument provided",
            HttpStatus.BAD_REQUEST.value(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
