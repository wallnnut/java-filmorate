package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalErrorHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.warn("Validation error: {}", ex.getMessage());
        List<FieldError> errorList = ex.getFieldErrors();
        Map<String, String> errorsMap = new HashMap<>();
        errorList.forEach(err -> {
            String key = err.getField();
            String message = err.getDefaultMessage();
            errorsMap.put(key, message);
            log.debug("Field '{}' validation failed: {}", key, message);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                "Ошибка валидации",
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
                             .toString(),
                errorsMap
        );
        log.info("Returning validation error response: {}", errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(@NotNull NotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
                             .toString(),
                null
        );
        log.info("Returning 404 Not Found response: {}", errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected internal server error", ex);
        ErrorResponse body = new ErrorResponse(
                "Внутренняя ошибка сервера",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
                             .toString(),
                null
        );
        log.info("Returning 500 Internal Server Error response: {}", body);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(body);
    }
}