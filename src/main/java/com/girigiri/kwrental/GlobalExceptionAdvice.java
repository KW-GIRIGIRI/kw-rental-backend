package com.girigiri.kwrental;

import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(EquipmentNotFoundException.class)
    public ResponseEntity<String> handleEquipmentNotFound(final EquipmentNotFoundException equipmentNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(equipmentNotFoundException.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest()
                .body(String.format("%s이 잘못된 타입으로 입력됐습니다. 입력값 : %s",
                        e.getParameter().getParameter().getName(), e.getValue()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(final ConstraintViolationException e) {
        StringBuilder builder = new StringBuilder();
        e.getConstraintViolations()
                .forEach(violation ->
                        builder.append(violation.getPropertyPath())
                                .append(String.format("에 %s를(을) 입력하면 안됩니다.", violation.getInvalidValue())));
        return ResponseEntity.badRequest()
                .body(builder.toString());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleInternalServerError(final RuntimeException runtimeException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(runtimeException.getMessage());
    }
}
