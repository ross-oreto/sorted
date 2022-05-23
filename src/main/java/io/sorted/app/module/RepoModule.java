package io.sorted.app.module;

import am.ik.yavi.core.Validator;
import io.sorted.app.conf.IMode;
import io.sorted.app.http.HttpStatus;
import io.sorted.app.service.Repo;
import io.sorted.app.service.Service;
import io.sorted.app.validation.i18nValidatorBuilder;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Repository module to support basic collection operations such as CRUD
 * @param <R> The type of repository
 * @param <T> The collection type
 */
public abstract class RepoModule<R extends Repo, T> extends AppModule {
  static final String idParam = "id";
  static final String idPath = "/:id";
  static String idPathParam(RoutingContext ctx) {
    return ctx.pathParam(idParam);
  }
  protected R repo;
  protected final Class<R> repoClass;
  protected final Class<T> collectionClass;
  protected Validator<T> saveValidator;
  protected Validator<T> updateValidator;
  protected Validator<T> replaceValidator;
  protected Validator<T> deleteValidator;

  public RepoModule(IMode mode, Class<R> repoClass, Class<T> collectionClass) {
    super(mode);
    this.repoClass = repoClass;
    this.collectionClass = collectionClass;
  }

  /**
   * Validation used before creating documents
   * @return A new validator
   */
  protected Validator<T> saveValidator() {
    return i18nValidatorBuilder.<T>of().build();
  }

  /**
   * Validation used before updating documents
   * @return A new validator
   */
  protected Validator<T> updateValidator() {
    return saveValidator();
  }

  /**
   * Validation used before replacing documents
   * @return A new validator
   */
  protected Validator<T> replaceValidator() {
    return saveValidator();
  }

  /**
   * Validation used before deleting documents
   * @return A new validator
   */
  protected Validator<T> deleteValidator() {
    return i18nValidatorBuilder.<T>of().build();
  }

  /**
   * @return True if module has 'get/list' enabled
   */
  protected boolean getEnabled() {
    return true;
  }

  /**
   * @return True if module has 'create' enabled
   */
  protected boolean createEnabled() {
    return true;
  }

  /**
   * @return True if module has 'update/replace' enabled
   */
  protected boolean updateEnabled() {
    return true;
  }

  /**
   * @return True if module has 'delete' enabled
   */
  protected boolean deleteEnabled() {
    return true;
  }


  /**
   * Initialise the verticle.<p>
   * This is called by Vert.x when the verticle instance is deployed. Don't call it yourself.
   *
   * @param vertx   the deploying Vert.x instance
   * @param context the context of the verticle
   */
  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    repo = Service.get(vertx, repoClass);
    saveValidator = saveValidator();
    updateValidator = updateValidator();
    replaceValidator = replaceValidator();
    deleteValidator = deleteValidator();
    routes();
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
    repo.close().onComplete(result -> stopPromise.complete());
  }

  /**
   * Setup routes for the repository module
   */
  protected void routes() {
    // create
    if (createEnabled()) {
      router.post("/")
        .handler(BodyHandler.create())
        .handler(validationHandler(saveValidator, collectionClass))
        .handler(this::save);
    }

    // retrieval
    if (getEnabled()) {
      router.get("/").handler(this::index);
      router.get(idPath).handler(this::get);
    }

    // updates
    // put does a complete replace
    if (updateEnabled()) {
      router.put(idPath)
        .handler(BodyHandler.create())
        .handler(validationHandler(replaceValidator, collectionClass))
        .handler(this::replace);
      // post will merge fields
      router.post(idPath)
        .handler(BodyHandler.create())
        .handler(this::merge)
        .handler(validationHandler(updateValidator, collectionClass))
        .handler(this::update);
    }

    // delete
    if (deleteEnabled()) {
      router.delete(idPath)
        .handler(validationHandler(deleteValidator, collectionClass))
        .handler(this::delete);
    }
  }

  /**
   * list documents
   * @param ctx Represents the context for the handling of a request in Vert.x-Web.
   */
  protected void index(RoutingContext ctx) {
    repo.list()
      .onSuccess(ctx::json)
      .onFailure(ctx::fail);
  }

  /**
   * save document
   * @param ctx Represents the context for the handling of a request in Vert.x-Web.
   */
  protected void save(RoutingContext ctx) {
    repo.save(ctx.body().asJsonObject())
      .onSuccess(ctx::json)
      .onFailure(ctx::fail);
  }

  /**
   * get document
   * @param ctx Represents the context for the handling of a request in Vert.x-Web.
   */
  protected void get(RoutingContext ctx) {
    repo.get(idPathParam(ctx))
      .onSuccess(notNullHandler(ctx))
      .onFailure(ctx::fail);
  }

  /**
   * Merge the request body with the current document
   * This should happen before validation so that the final object is validated before persisting
   * @param ctx Represents the context for the handling of a request in Vert.x-Web.
   */
  protected void merge(RoutingContext ctx) {
    String id = idPathParam(ctx);
    repo.get(id)
      .onSuccess(it -> {
        if (it == null)
          ctx.fail(HttpStatus.NOT_FOUND.value());
        else {
          ctx.put(getName(), it.mergeIn(ctx.body().asJsonObject(), true)).next();
        }
      }).onFailure(ctx::fail);
  }

  /**
   * Update a document
   * @param ctx Represents the context for the handling of a request in Vert.x-Web.
   */
  protected void update(RoutingContext ctx) {
    repo.updateById(idPathParam(ctx), new JsonObject().put("$set", ctx.body().asJsonObject()))
      .onSuccess(notNullHandler(ctx))
      .onFailure(ctx::fail);
  }

  /**
   * Replace a document
   * @param ctx Represents the context for the handling of a request in Vert.x-Web.
   */
  protected void replace(RoutingContext ctx) {
    repo.replaceById(idPathParam(ctx), ctx.body().asJsonObject())
      .onSuccess(notNullHandler(ctx))
      .onFailure(ctx::fail);
  }

  /**
   * Delete a document
   * @param ctx Represents the context for the handling of a request in Vert.x-Web.
   */
  protected void delete(RoutingContext ctx) {
    repo.deleteById(idPathParam(ctx))
      .onSuccess(notNullHandler(ctx))
      .onFailure(ctx::fail);
  }
}
