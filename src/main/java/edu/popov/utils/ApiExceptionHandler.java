package edu.popov.utils;

import edu.popov.utils.exception.BadRequestException;
import edu.popov.utils.exception.ForbiddenException;
import edu.popov.utils.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;
import java.util.Objects;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value =
            {
                    NotFoundException.class
            }
    )
    public ResponseEntity<Object> handle404ApiRequestException(RuntimeException e) {
        HttpStatus badRequest = HttpStatus.NOT_FOUND;
        ApiException apiException = new ApiException(
                e.getMessage(),
                e.getClass(),
                badRequest
        );
        return new ResponseEntity<>(apiException, badRequest);
    }

    @ExceptionHandler(value =
            {
                    BadRequestException.class
            }
    )
    public ResponseEntity<Object> handle442ApiRequestException(RuntimeException e) {
        HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        ApiException apiException = new ApiException(
                e.getMessage(),
                e.getClass(),
                httpStatus
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value =
            {
                    AuthenticationException.class
            }
    )
    public ResponseEntity<Object> handle401ApiRequestException(RuntimeException e) {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        ApiException apiException = new ApiException(
                e.getMessage(),
                e.getClass(),
                httpStatus
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value =
            {
                    ForbiddenException.class
            }
    )
    public ResponseEntity<Object> handle403ApiRequestException(RuntimeException e) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        ApiException apiException = new ApiException(
                e.getMessage(),
                e.getClass(),
                httpStatus
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value =
            {
                    MethodArgumentNotValidException.class
            }
    )
    public ResponseEntity<Object> handleApiRequestValidException(MethodArgumentNotValidException e) {
        HttpStatus badRequest = HttpStatus.UNPROCESSABLE_ENTITY;
        ApiException apiException = new ApiException(
                Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage(),
                e.getClass(),
                badRequest
        );
        return new ResponseEntity<>(apiException, badRequest);
    }
}
