package com.campsite.reservation.web.advice;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
@Getter
@Generated
public class RestError {
    private HttpStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime timestamp;

    private String message;
}
