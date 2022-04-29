package io.sorted.info;

import io.sorted.app.MainVerticle;
import io.sorted.app.conf.IMode;
import io.sorted.app.module.AppModule;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Module that reports basic information about the application
 */
public class InfoModule extends AppModule {
  public static final String JAVA_VER_PROP = "javaVersion";
  public static final String javaVersion = IMode.getJavaVersion();

  public InfoModule(IMode mode) {
    super(mode);
  }

  /**
   * Get the name of the module, which implies the subdomain route
   * @return Module name
   */
  @Override
  public String getName() {
    return "info";
  }

  /**
   * Initialise the verticle.<p>
   * This is called by Vert.x when the verticle instance is deployed. Don't call it yourself.
   * @param vertx   the deploying Vert.x instance
   * @param context the context of the verticle
   */
  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    router.get("/").handler(this::index);
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
