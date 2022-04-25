package io.sorted.app;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AppModule extends AbstractVerticle implements IMode {
  public abstract String getName();

  protected final Logger log;
  protected final Router router;

  public AppModule() {
    this.log = LoggerFactory.getLogger(getName());
    this.router = Router.router(vertx);
  }

  public Router getRouter() {
    return router;
  }
}
