package io.sorted.info;

import io.sorted.app.AppModule;
import io.sorted.app.IMode;
import io.sorted.app.MainVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Module that reports basic information about the application
 */
public class InfoModule extends AppModule {
  protected static final String JAVA_VER_PROP = "javaVersion";
  public static final String javaVersion = IMode.getJavaVersion();

  /**
   * Get the name of the module, which implies the subdomain route
   * @return Module name
   */
  @Override
  public String getName() {
    return "info";
  }

  /**
   * Start the verticle.<p>
   * This is called by Vert.x when the verticle instance is deployed. Don't call it yourself.<p>
   * If your verticle does things in its startup which take some time then you can override this method
   * and call the startFuture some time later when start up is complete.
   * @param startPromise  a promise which should be called when verticle start-up is complete.
   */
  @Override
  public void start(Promise<Void> startPromise) {
    router.route().handler(this::index);
    startPromise.complete();
  }

  /**
   * Stop the verticle.<p>
   * This is called by Vert.x when the verticle instance is un-deployed. Don't call it yourself.<p>
   * If your verticle does things in its shut-down which take some time then you can override this method
   * and call the stopFuture some time later when clean-up is complete.
   * @param stopPromise  a promise which should be called when verticle clean-up is complete.
   */
  @Override
  public void stop(Promise<Void> stopPromise) {
    log.info("{} stopping", InfoModule.class.getSimpleName());
    stopPromise.complete();
  }

  /**
   * app info response
   * @param ctx Represents the context for the handling of a request in Vert.x-Web.
   */
  protected void index(RoutingContext ctx) {
    ctx.json(
      new JsonObject()
        .put("version", MainVerticle.getVersion())
        .put(JAVA_VER_PROP, javaVersion)
        .put("vertx", Vertx.class.getPackage().getImplementationVersion())
        .put(MODE_PROP, getMode())
        .put(DEBUG_PROP, isDebugging())
    );
  }
}
