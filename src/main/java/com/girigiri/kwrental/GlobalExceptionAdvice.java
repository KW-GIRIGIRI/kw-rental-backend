package com.girigiri.kwrental;

import com.girigiri.kwrental.equipment.exception.EquipmentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleInternalServerError(final RuntimeException runtimeException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(runtimeException.getMessage());
    }
}
