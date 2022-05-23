package io.sorted.app.http;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.Locale;

/**
 * Supports some basic request/response headers/parameters
 * Defines header names and parameter names
 */
public class Http {
  public static final String APPLICATION_JSON = "application/json";
  public static final String LANG_PARAM = "lang";

  /**
   * Get the locale from the routing context
   * Uses the lang query parameter if present
   * @param ctx Represents the context for the handling of a request in Vert.x-Web
   * @return The Locale found
   */
  public static Locale getLocale(RoutingContext ctx) {
    String lang = ctx.queryParams().contains(LANG_PARAM)
      ? ctx.queryParam(LANG_PARAM).get(0)
      : ctx.preferredLanguage() == null ? Locale.getDefault().getLanguage() : ctx.preferredLanguage().subtag();
    return Locale.forLanguageTag(lang);
  }

  /**
   * Set the Content-Type header to json
   * @param ctx Represents the context for the handling of a request in Vert.x-Web
   * @return HttpServerResponse
   */
  public static HttpServerResponse jsonContent(RoutingContext ctx) {
    return ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON);
  }
}
