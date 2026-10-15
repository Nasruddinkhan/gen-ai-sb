package com.mypractice.genai.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRequestBody(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request body. Please check the JSON or text payload.",
                request,
                ex);
    }

    @ExceptionHandler(OpenAIChatException.class)
    public ResponseEntity<ApiErrorResponse> handleOpenAIChatException(
            OpenAIChatException ex,
            HttpServletRequest request) {

        return buildResponse(HttpStatus.BAD_GATEWAY, ex.getMessage(), request, ex);
    }

    @ExceptionHandler(TransientAiException.class)
    public ResponseEntity<ApiErrorResponse> handleTransientAiException(
            TransientAiException ex,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "AI service is temporarily unavailable. Please try again later.",
                request,
                ex);
    }

    @ExceptionHandler(NonTransientAiException.class)
    public ResponseEntity<ApiErrorResponse> handleNonTransientAiException(
            NonTransientAiException ex,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.BAD_GATEWAY,
                "AI service returned an error: " + safeMessage(ex),
                request,
                ex);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error: " + safeMessage(ex),
                request,
                ex);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Exception ex) {

        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                ex.getClass().getSimpleName()
        );

        return ResponseEntity.status(status).body(response);
    }

    private String safeMessage(Exception ex) {
        return ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
    }
}
