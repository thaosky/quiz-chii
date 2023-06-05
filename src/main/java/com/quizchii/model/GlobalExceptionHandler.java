package com.quizchii.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ResponseData> handleException(Exception e) {
        e.printStackTrace();
        ResponseData responseData = new ResponseData();
        responseData.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<ResponseData> handleException(BusinessException e) {
        e.printStackTrace();
        ResponseData responseData = new ResponseData();
        responseData.error(e.getMessage());
        return ResponseEntity.status(e.getStatusCode()).body(responseData);
    }
}
