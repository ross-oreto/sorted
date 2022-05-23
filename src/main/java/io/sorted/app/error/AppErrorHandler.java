package io.sorted.app.error;

import am.ik.yavi.core.ConstraintViolationsException;
import io.sorted.app.http.Http;
import io.sorted.app.http.HttpStatus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;

import java.util.HashMap;

/**
 * Class to support common error handling and responses
 */
public record AppErrorHandler(Logger log) {
  /**
   * Map associating Exceptions with HTTP statuses
   */
  public static HashMap<String, HttpStatus> errorMap = new HashMap<>() {{
      put(ConstraintViolationsException.class.getName(), HttpStatus.BAD_REQUEST);
    }};

  /**
   * Lookup the proper HTTP status for a given exception
   * @param t Throwable exception
   * @return The HTTP status code
   */
  public static int errorCode(Throwable t) {
    return t == null
      ? HttpStatus.INTERNAL_SERVER_ERROR.value()
      : errorMap.getOrDefault(t.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR).value();
  }

  /**
   * Handle application level errors
   * @param status The http status error code
   * @param ctx Represents the context for the handling of a request in Vert.x-Web.
   */
  public static void handle(int status, RoutingContext ctx) {
    HttpStatus httpStatus = HttpStatus.valueOf(status);
    Throwable failure = ctx.failure();
    HttpServerResponse response = Http.jsonContent(ctx)
      .setStatusCode(ctx.statusCode() > -1 ? ctx.statusCode() : errorCode(failure));

    if (failure instanceof ConstraintViolationsException) {
      response
        .send(JsonObject.mapFrom(new ValidationErrors((ConstraintViolationsException) failure)).encode());
    } else {
      response
        .send(errorResponse(failure
          , httpStatus
          , ctx.request()).encode());
    }
  }

  /**
   * Build an error response for API responses
   * @param t The error
   * @param httpStatus HTTP status code of the response
   * @param request The request on which the error occurred
   * @return The error response as a JSON object
   */
  public static JsonObject errorResponse(Throwable t, HttpStatus httpStatus, HttpServerRequest request) {
    return JsonObject.mapFrom(ErrorResponse.of(t, httpStatus, request));
  }

  /**
   * Handle application level errors
   * @param status The http status error code
   * @param ctx Represents the context for the handling of a request in Vert.x-Web.
   */
  public void failureHandler(int status, RoutingContext ctx) {
    log.error(String.valueOf(status), ctx.failure());
    handle(status, ctx);
  }

  /**
   * Handle application level errors
   * @param ctx Represents the context for the handling of a request in Vert.x-Web.
   */
  public void failureHandler(RoutingContext ctx) {
    failureHandler(ctx.statusCode(), ctx);
  }
}
