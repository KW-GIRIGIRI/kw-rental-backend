package com.girigiri.kwrental;

import java.util.Arrays;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.amazonaws.AmazonServiceException;
import com.girigiri.kwrental.auth.exception.ForbiddenException;
import com.girigiri.kwrental.auth.exception.UnauthorizedException;
import com.girigiri.kwrental.common.exception.BadRequestException;
import com.girigiri.kwrental.common.exception.NotFoundException;

import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(final NotFoundException e) {
        log.warn("{} : {}", e.getClass(), e.getMessage());
        log.warn(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException e) {
        log.warn("{} : {}", e.getClass(), e.getMessage());
        log.warn(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.badRequest()
                .body(String.format("%s이 잘못된 타입으로 입력됐습니다. 입력값 : %s",
                        e.getParameter().getParameter().getName(), e.getValue()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(final BindException e) {
        log.warn("{} : {}", e.getClass(), e.getMessage());
        log.warn(Arrays.toString(e.getStackTrace()));
        StringBuilder builder = new StringBuilder();
        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            builder.append(
                    String.format("%s에 데이터를 바인딩하지 못했습니다. 이유 : %s", error.getObjectName(), error.getDefaultMessage())
            );
        }
        return ResponseEntity.badRequest()
                .body(builder.toString());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleInvalidDataAccess(final DataAccessException e) {
        log.warn("{} : {}", e.getClass(), e.getMessage());
        log.warn(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.badRequest()
                .body("데이터베이스에 잘못된 접근입니다.");
    }

    @ExceptionHandler({DataIntegrityViolationException.class, PersistenceException.class})
    public ResponseEntity<?> handleDataIntegrityViolation(Exception e) {
        log.warn("{} : {}", e.getClass(), e.getMessage());
        log.warn(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.badRequest()
                .body("데이터의 조건이 맞지 않습니다. 유일값이나 null 조건을 확인하세요.");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<?> handleDuplicateKey(final DuplicateKeyException e) {
        log.warn("{} : {}", e.getClass(), e.getMessage());
        log.warn(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.badRequest().body("중복되서는 안되는 값이 중복된 요청입니다.");
    }

    @ExceptionHandler(AmazonServiceException.class)
    public ResponseEntity<?> handleAmazonServiceException(final AmazonServiceException e) {
        log.warn("{} : {}", e.getClass(), e.getMessage());
        log.warn(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.internalServerError().body("AWS에 문제가 생겼습니다!!" + e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(final BadRequestException e) {
        log.warn("{} : {}", e.getClass(), e.getMessage());
        log.warn(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(final ConstraintViolationException e) {
        log.error("{} : {}", e.getClass(), e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.badRequest().body("입력값 검증을 통과하지 못했습니다." + e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpMethodNotSupported(final HttpRequestMethodNotSupportedException e) {
        log.warn("{} : {}", e.getClass(), e.getMessage());
        log.warn(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.badRequest().body("해당 HTTP METHOD는 처리할 수 없습니다.");
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(final UnauthorizedException e) {
        log.warn("{} : {}", e.getClass(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<?> handleForbiddenException(final ForbiddenException e) {
        log.warn("{} : {}", e.getClass(), e.getMessage());
        log.warn(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleInternalServerError(Exception e) {
        log.error("{} : {}", e.getClass(), e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예상하지 못한 예외가 발생했습니다.");
    }

}
