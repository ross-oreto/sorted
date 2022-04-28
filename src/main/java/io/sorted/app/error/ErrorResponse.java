package io.sorted.app.error;

import io.sorted.app.http.HttpStatus;
import io.vertx.core.http.HttpServerRequest;

import java.time.LocalDateTime;

/**
 * Standard error response structure
 * @param timestamp Timestamp of the error
 * @param code http status code or custom status code
 * @param error Identify the error
 * @param message Brief message
 * @param detail Detailed explanation of the error
 * @param path URI where the error occurred
 */
public record ErrorResponse(LocalDateTime timestamp
  , int code
  , String error
  , String message
  , String detail
  , String path) {
  public static ErrorResponse of(Throwable error, HttpStatus httpStatus, HttpServerRequest request) {
    return new ErrorResponse(
      LocalDateTime.now()
      , httpStatus.value()
      , error == null ? httpStatus.getReasonPhrase() : error.getClass().getSimpleName()
      , error == null ? null : error.getMessage()
      , error == null ? null : error.getLocalizedMessage()
      , request == null ? null : request.uri()
    );
  }

  public static ErrorResponse of(Throwable error, HttpServerRequest request) {
    return ErrorResponse.of(error, HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  public static ErrorResponse of(Throwable error, HttpStatus httpStatus) {
    return ErrorResponse.of(error, httpStatus, null);
  }

  public static ErrorResponse of(Throwable error) {
    return ErrorResponse.of(error, HttpStatus.INTERNAL_SERVER_ERROR, null);
  }
}
