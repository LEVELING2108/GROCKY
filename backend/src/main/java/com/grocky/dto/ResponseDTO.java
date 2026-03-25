package com.grocky.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {
    
    private boolean success;
    private String message;
    private Object data;
    private Map<String, String> errors;
    
    public static ResponseDTO success(String message, Object data) {
        return ResponseDTO.builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
    
    public static ResponseDTO success(Object data) {
        return ResponseDTO.builder()
                .success(true)
                .data(data)
                .build();
    }
    
    public static ResponseDTO error(String message) {
        return ResponseDTO.builder()
                .success(false)
                .message(message)
                .build();
    }
    
    public static ResponseDTO error(String message, Map<String, String> errors) {
        return ResponseDTO.builder()
                .success(false)
                .message(message)
                .errors(errors)
                .build();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginatedResponse<T> {
        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;
        private boolean empty;
    }
}
