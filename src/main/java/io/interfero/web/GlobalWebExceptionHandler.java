package io.interfero.web;

import io.interfero.clusters.ClusterConnectionVerificationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
class GlobalWebExceptionHandler extends ResponseEntityExceptionHandler
{
    @ExceptionHandler(ClusterConnectionVerificationException.class)
    ResponseEntity<ErrorResponse> handleClusterConnectionVerificationException(
            ClusterConnectionVerificationException ex)
    {
        var errorResponse = new ErrorResponse(ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    private record ErrorResponse(String error) {}
}
