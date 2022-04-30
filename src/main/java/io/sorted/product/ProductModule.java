package io.sorted.product;

import io.sorted.app.conf.IMode;
import io.sorted.app.module.AppModule;
import io.sorted.app.service.Service;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ProductModule extends AppModule {
  private ProductRepo productRepo;

  public ProductModule(IMode mode) {
    super(mode);
  }

  /**
   * Get the name of the module, which implies the subdomain route
   * @return Module name
   */
  @Override
  public String getName() {
    return "product";
  }

  /**
   * Initialise the verticle.<p>
   * This is called by Vert.x when the verticle instance is deployed. Don't call it yourself.
   * @param vertx  the deploying Vert.x instance
   * @param context  the context of the verticle
   */
  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    productRepo = Service.get(vertx, ProductRepo.class);
    router.get("/").handler(this::index);
    router.post("/").handler(BodyHandler.create()).handler(this::save);
  }

  /**
   * Stop the verticle.<p>
   * This is called by Vert.x when the verticle instance is un-deployed. Don't call it yourself.<p>
   * If your verticle does things in its shut-down which take some time then you can override this method
   * and call the stopFuture some time later when clean-up is complete.
   * @param stopPromise a promise which should be called when verticle clean-up is complete.
   */
  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    super.stop();
    productRepo.close().onComplete(result -> stopPromise.complete());
  }

  /**
   * list products
   * @param ctx Represents the context for the handling of a request in Vert.x-Web.
   */
  protected void index(RoutingContext ctx) {
    productRepo.list().onSuccess(ctx::json);
  }

  /**
   * save products
   * @param ctx Represents the context for the handling of a request in Vert.x-Web.
   */
  protected void save(RoutingContext ctx) {
    JsonObject json = ctx.getBodyAsJson();
    productRepo.save(json).onSuccess(ctx::json);
  }
}
