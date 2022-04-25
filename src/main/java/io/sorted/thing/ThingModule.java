package io.sorted.thing;

import io.sorted.app.AppModule;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

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
    router.get().handler(this::index);
    router.post().handler(BodyHandler.create()).handler(this::save);
  }

  @Override
  public void stop() throws Exception {
    log.info("{} stopped", ThingModule.class.getSimpleName());
    thingService.close();
  }

  protected void index(RoutingContext ctx) {
    thingService.list().onSuccess(ctx::json);
  }

  protected void save(RoutingContext ctx) {
    JsonObject json = ctx.getBodyAsJson();
    thingService.save(json).onSuccess(ctx::json).onFailure(ctx::fail);
  }
}
