package com.softwareprojectmanagement.backend.exceptions;

/**
 * Custom application exception class that extends RuntimeException.
 * This exception is used for application-specific errors and business logic failures.
 */
public class ApplicationException extends RuntimeException {

    /**
     * Constructs an ApplicationException with the specified detail message.
     *
     * @param message the detail message
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * Constructs an ApplicationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an ApplicationException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public ApplicationException(Throwable cause) {
        super(cause);
    }

    /**
     * Returns the exception message.
     * This method overrides the getMessage() method to explicitly return the exception message.
     *
     * @return the exception message
     */
    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
