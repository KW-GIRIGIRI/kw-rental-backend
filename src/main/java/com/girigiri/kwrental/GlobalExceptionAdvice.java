package com.girigiri.kwrental;

import com.amazonaws.AmazonServiceException;
import com.girigiri.kwrental.common.exception.BadRequestException;
import com.girigiri.kwrental.common.exception.NotFoundException;
import com.girigiri.kwrental.equipment.exception.EquipmentException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(final NotFoundException notFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest()
                .body(String.format("%s이 잘못된 타입으로 입력됐습니다. 입력값 : %s",
                        e.getParameter().getParameter().getName(), e.getValue()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> handleBindException(final BindException e) {
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
    public ResponseEntity<String> handleInvalidDataAccess(final DataAccessException e) {
        return ResponseEntity.badRequest()
                .body("데이터베이스에 잘못된 접근입니다.");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> handleDuplicateKey(final DuplicateKeyException e) {
        return ResponseEntity.badRequest().body("중복되서는 안되는 값이 중복된 요청입니다.");
    }

    @ExceptionHandler(EquipmentException.class)
    public ResponseEntity<String> handleEquipmentException(final EquipmentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(AmazonServiceException.class)
    public ResponseEntity<String> handleAmazonServiceException(final AmazonServiceException e) {
        return ResponseEntity.internalServerError().body("AWS에 문제가 생겼습니다!!" + e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(final BadRequestException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleInternalServerError(final Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
