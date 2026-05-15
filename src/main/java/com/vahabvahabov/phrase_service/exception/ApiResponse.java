package com.vahabvahabov.phrase_service.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T> {

    private String message;
    private int statusCode;
    private T data;
    private String path;
    private Instant statusTime = Instant.now();

    public static <T> ApiResponse<T> success(String message, T data, int statusCode, String path) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setData(data);
        apiResponse.setPath(path);
        apiResponse.setMessage(message);
        apiResponse.setStatusCode(statusCode);

        return apiResponse;

    }

    public static <T> ApiResponse<T> success(String message) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setData(null);
        apiResponse.setPath(null);
        apiResponse.setMessage(message);
        apiResponse.setStatusCode(200);

        return apiResponse;

    }

    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setData(data);
        apiResponse.setMessage(message);

        return apiResponse;

    }
}
