package com.roman.pres.todos.controller;

import com.roman.pres.todos.model.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

public class BaseController {
    public boolean isFailedResponse(ApiResponse response) {
        return Objects.nonNull(response.getErrorMessage());
    }

    public <T extends ApiResponse> ResponseEntity<ApiResponse> generateError(T response) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (Objects.nonNull(response.getErrorCode())){
            status = HttpStatus.resolve(response.getErrorCode());
        }
        return ResponseEntity
                .status(status)
                .body(new ApiResponse(response.getErrorMessage(), status.value()));
    }
}
