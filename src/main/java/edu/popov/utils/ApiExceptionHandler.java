package edu.popov.utils;

import edu.popov.utils.exception.BadRequestException;
import edu.popov.utils.exception.ForbiddenException;
import edu.popov.utils.exception.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;
import java.util.Objects;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<Object> handle404ApiRequestException(RuntimeException e) {
        ApiException apiException = new ApiException(e.getMessage());
        return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<Object> handle442ApiRequestException(RuntimeException e) {
        ApiException apiException = new ApiException(e.getMessage());
        return new ResponseEntity<>(apiException, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = AuthenticationException.class)
    public ResponseEntity<Object> handle401ApiRequestException(RuntimeException e) {
        ApiException apiException = new ApiException(e.getMessage());
        return new ResponseEntity<>(apiException, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = ForbiddenException.class)
    public ResponseEntity<Object> handle403ApiRequestException(RuntimeException e) {
        ApiException apiException = new ApiException(e.getMessage());
        return new ResponseEntity<>(apiException, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleApiRequestValidException(MethodArgumentNotValidException e) {
        ApiException apiException = new ApiException(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
        return new ResponseEntity<>(apiException, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
