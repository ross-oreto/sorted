package io.sorted.app;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
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

  public AppModule() {
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
}
