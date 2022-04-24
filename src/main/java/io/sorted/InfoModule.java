package io.sorted;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class InfoModule extends AppModule {
  protected static final String JAVA_VER_PROP = "javaVersion";
  public static final String javaVersion = IMode.getJavaVersion();

  @Override
  public String getName() {
    return "info";
  }

  @Override
  public void start() {
    router.route().handler(this::index);
  }

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
