package com.quizchii.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ResponseData<T> {
    private T data;
    private int status;
    private String error;

    public ResponseData() {
    }

    public ResponseData<T> success(T data) {
        this.data = data;
        this.status = HttpStatus.OK.value();
        this.error = "OK";
        return this;
    }

    public ResponseData<T> error(String error) {
        this.status = HttpStatus.BAD_REQUEST.value();
        this.error = error;
        return this;
    }
    public ResponseData<T> error(HttpStatus status, String error) {
        this.error = error;
        this.status = status.value();
        return this;
    }
}
