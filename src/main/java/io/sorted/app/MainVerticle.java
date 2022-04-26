package io.sorted.app;

import io.sorted.app.command.AppVersionCommand;
import io.sorted.app.service.Service;
import io.sorted.info.InfoModule;
import io.sorted.thing.ThingModule;
import io.sorted.thing.ThingRepo;
import io.sorted.thing.ThingRepoImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point of the application
 * Configured as follows:
 * The Vert.x verticle config()
 * The system properties
 * The environment variables
 * conf/config.json file. This path can be overridden using the vertx-config-path system property or VERTX_CONFIG_PATH environment variable.
 * conf/sorted.conf (uses hocon format <a href="https://github.com/lightbend/config">https://github.com/lightbend/config</a>)
 * conf/sorted-secrets.conf (also hocon and is ignored by VCS)
 * If any config changes are detected, the server will restart to load the config changes
 */
public class MainVerticle extends AbstractVerticle implements Configurable {
  protected static final String PORT_PROP = "port";
  protected static final int DEFAULT_PORT = 8888;

  private static String version;

  /**
   * Get the application version
   * @return Version as a string
   */
  public static String getVersion() {
    if (version == null)
      version = AppVersionCommand.getVersion().orElse("_");
    return version;
  }

  protected Logger log;
  protected final JsonObject config = new JsonObject();
  private HttpServer server;
  private Router router;

  /**
   * Get the configuration of the verticle
   * @return the configuration
   */
  @Override
  public final JsonObject config() {
    return this.config;
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
    // get any configuration from the context
    config.mergeIn(context.config(), true);
    log = LoggerFactory.getLogger(MainVerticle.class.getSimpleName());

    // configure app then start the server
    configureApp().onComplete(conf -> {
      registerServices();
      startServer(startPromise);
    });
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
    log.info("{} stopping", MainVerticle.class.getSimpleName());
    stopPromise.complete();
  }

    /**
     * load configuration for the application
     * @return Future json config
     */
  protected Future<JsonObject> configureApp() {
    return configure().onComplete(conf -> {
      String mode = getMode();
      log.info("running in {} mode{}", mode, isDebugging() ? " and debugging" : "");
      if (conf.failed()) {
        log.error("error loading config: {}", conf.cause().getMessage());
      } else {
        config().mergeIn(conf.result(), true)
          .mergeIn(config().getJsonObject(getMode(), config()), true);
      }
    });
  }

  /**
   * start up the http server
   * @param startPromise a promise which should be called when verticle start-up is complete.
   */
  protected void startServer(Promise<Void> startPromise) {
    int port = config().getInteger(PORT_PROP, DEFAULT_PORT);
    this.server = vertx.createHttpServer();
    this.router = router == null ? Router.router(getVertx()) : router.clear();
    deployModules();
    server.requestHandler(router);

    server.listen(port, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        log.info("HTTP server started on port {}", port);
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  /**
   * Register any needed services on the event bus
   */
  protected void registerServices() {
    registerService(ThingRepo.class
      , new ThingRepoImpl(MongoClient.createShared(vertx, config.getJsonObject("mongo")), "things"));
  }

  /**
   * Registers a service on the event bus.
   * @param tClass  the service class (interface)
   * @param impl the service implementation object
   * @param <T> the type of the service interface
   */
  final <T, I extends T> void registerService(Class<T> tClass, I impl) {
    Service.register(vertx, tClass, impl);
  }

  /**
   * Deploy any additional verticles and mount them as sub routes
   */
  protected void deployModules() {
    for (AppModule app : getModules()) {
      vertx.deployVerticle(app).onSuccess(
        id -> router.mountSubRouter(String.format("/%s", app.getName()), app.getRouter())
      ).onFailure(t -> log.error("error deploying module {}: {}", app.getName(), t.getMessage()));
    }
  }

  /**
   * Defines all the verticles which should be deployed
   * @return An array of app verticles
   */
  protected AppModule[] getModules() {
    return new AppModule[] {
      new InfoModule()
      , new ThingModule()
    };
  }

  /**
   * When the app config changes do something
   * @return a Runnable
   */
  @Override
  public Runnable onConfigChange() {
    return this::bounceServer;
  }

  /**
   * Restart (bounce) the http server
   */
  protected final void bounceServer() {
    server.close(stop -> {
      if (stop.succeeded()) {
        log.info("restarting http server...");
        int port = config().getInteger(PORT_PROP, DEFAULT_PORT);
        router.clear();
        registerServices();
        deployModules();
        server.requestHandler(router);

        server.listen(port, http -> {
          if (http.succeeded()) {
            log.info("HTTP server started on port {}", port);
          } else {
            log.error("error starting http server: {}", http.cause().getMessage());
          }
        });
      } else {
        log.error("error stopping http server: {}", stop.cause().getMessage());
      }
    });
  }
}
