package io.sorted.app.error;

import io.sorted.app.http.HttpStatus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;

/**
 * Class to support common error handling and responses
 */
public record AppErrorHandler(Logger log) {

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
    ctx.response()
      .setStatusCode(status)
      .send(errorResponse(ctx.failure()
        , HttpStatus.valueOf(ctx.statusCode())
        , ctx.request()).encodePrettily());
  }

  /**
   * Handle application level errors
   * @param ctx Represents the context for the handling of a request in Vert.x-Web.
   */
  public void failureHandler(RoutingContext ctx) {
    failureHandler(HttpStatus.INTERNAL_SERVER_ERROR.value(), ctx);
  }
}
