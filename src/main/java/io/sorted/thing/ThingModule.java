package io.sorted.thing;

import io.sorted.AppModule;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

public class ThingModule extends AppModule {
  private ThingService thingService;

  @Override
  public String getName() {
    return "thing";
  }

  @Override
  public void init(Vertx vertx, Context context) {
    thingService = ThingService.create(vertx);
  }

  @Override
  public void start() {
    router.route().handler(this::index);
  }

  protected void index(RoutingContext ctx) {
    thingService.list("things").onSuccess(ctx::json);
  }
}
