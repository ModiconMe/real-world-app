package edu.popov.utils;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public class ApiException {
    private final String message;
    private final Class<?> aClass;
    private HttpStatus httpStatus;

    public ApiException(String message, Class<?> aClass, HttpStatus httpStatus) {
        this.message = message;
        this.aClass = aClass;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public Class getaClass() {
        return aClass;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

}
