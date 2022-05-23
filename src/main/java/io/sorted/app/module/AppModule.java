package io.sorted.app.module;

import am.ik.yavi.core.ConstraintViolationsException;
import am.ik.yavi.core.Validator;
import io.sorted.app.conf.IMode;
import io.sorted.app.http.Http;
import io.sorted.app.http.HttpStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * App modules are deployed as verticles by the main verticle and mounted as a sub-router to the main router
 */
public abstract class AppModule extends AbstractVerticle implements IMode {
  /**
   * Get the name of the module, which implies the subdomain route
   * For example name 'info' would resolve to /info/*
   * All the module routes will be available on /info/
   * @return Module name
   */
  public abstract String getName();

  protected final Logger log;
  protected final Router router;
  private final IMode mode;

  public AppModule(IMode mode) {
    this.mode = mode;
    this.log = LoggerFactory.getLogger(getName());
    this.router = Router.router(vertx);
  }

  /**
   * Access the modules router
   * @return The router
   */
  public Router getRouter() {
    return router;
  }

  /**
   * If your verticle does a simple, synchronous start-up then override this method and put your start-up
   * code in here.
   */
  @Override
  public void start() throws Exception {
    super.start();
    log.info("starting module {}", getName());
  }

  /**
   * If your verticle has simple synchronous clean-up tasks to complete then override this method and put your clean-up
   * code in here.
   */
  @Override
  public void stop() throws Exception {
    super.stop();
    log.info("stopping module {}", getName());
  }

  /**
   * Get the current app mode
   * @return The application mode
   */
  @Override
  public String getMode() {
    return mode.getMode();
  }

  /**
   * Determine if running in a debug mode
   * @return True if in debug mode, false otherwise
   */
  @Override
  public boolean isDebugging() {
    return mode.isDebugging();
  }

  /**
   * Validate the request object class using the specified validator
   * @param validator The validator used to validate the request object
   * @param aClass The type of object to validate
   * @return The validation handler
   * @param <T> The type of object being validated
   */
  protected <T> Handler<RoutingContext> validationHandler(Validator<T> validator, Class<T> aClass) {
    return ctx -> {
      JsonObject result = ctx.get(getName());
      validator.applicative().validate(result == null ? ctx.body().asPojo(aClass) : result.mapTo(aClass)
        , Http.getLocale(ctx)).orElseThrow(ConstraintViolationsException::new);
      ctx.next();
    };
  }

  /**
   * Make sure result is not null and if so return as JSON, otherwise fail 404
   * @param ctx Represents the context for the handling of a request in Vert.x-Web
   * @return The context handler
   * @param <T> The type of result
   */
  protected <T> Handler<T> notNullHandler(RoutingContext ctx) {
    return it -> {
      if (it == null) ctx.fail(HttpStatus.NOT_FOUND.value());
      else ctx.json(it);
    };
  }
}
