package com.practice.discoveryEvents.util;

import java.time.LocalDateTime;
import java.util.List;

public class ApiError {

    String status;
    String reason ;
    String message;
    LocalDateTime timestamp;

    public ApiError( String message, String reason, String status, LocalDateTime timestamp) {
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
    }
}
