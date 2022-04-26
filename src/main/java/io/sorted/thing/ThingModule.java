package io.sorted.thing;

import io.sorted.app.AppModule;
import io.sorted.app.service.Service;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ThingModule extends AppModule {
  private ThingRepo thingRepo;

  /**
   * Get the name of the module, which implies the subdomain route
   * @return Module name
   */
  @Override
  public String getName() {
    return "thing";
  }

  /**
   * Initialise the verticle.<p>
   * This is called by Vert.x when the verticle instance is deployed. Don't call it yourself.
   * @param vertx  the deploying Vert.x instance
   * @param context  the context of the verticle
   */
  @Override
  public void init(Vertx vertx, Context context) {
    thingRepo = Service.get(vertx, ThingRepo.class);;
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
    router.get().handler(this::index);
    router.post().handler(BodyHandler.create()).handler(this::save);
    startPromise.complete();
  }

  /**
   * Stop the verticle.<p>
   * This is called by Vert.x when the verticle instance is un-deployed. Don't call it yourself.<p>
   * If your verticle does things in its shut-down which take some time then you can override this method
   * and call the stopFuture some time later when clean-up is complete.
   * @param stopPromise a promise which should be called when verticle clean-up is complete.
   */
  @Override
  public void stop(Promise<Void> stopPromise) {
    log.info("{} stopping", ThingModule.class.getSimpleName());
    thingRepo.close().onComplete(result -> stopPromise.complete());
  }

  /**
   * list things
   * @param ctx Represents the context for the handling of a request in Vert.x-Web.
   */
  protected void index(RoutingContext ctx) {
    thingRepo.list().onSuccess(ctx::json).onFailure(ctx::fail);
  }

  /**
   * save things
   * @param ctx Represents the context for the handling of a request in Vert.x-Web.
   */
  protected void save(RoutingContext ctx) {
    JsonObject json = ctx.getBodyAsJson();
    thingRepo.save(json).onSuccess(ctx::json).onFailure(ctx::fail);
  }
}
