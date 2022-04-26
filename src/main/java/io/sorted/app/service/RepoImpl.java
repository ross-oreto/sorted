package io.sorted.app.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

/**
 * Implementation of standard database operations
 */
public abstract class RepoImpl implements Repo, Collectable {
  /**
   * Create a query object for the id
   * @param id The id value
   * @return The id query object
   */
  public static JsonObject idDocument(String id) {
    return new JsonObject().put("_id", id);
  }

  private final MongoClient mongo;
  private final String collectionName;

  public RepoImpl(MongoClient mongo, String collectionName) {
    this.mongo = mongo;
    this.collectionName = collectionName;
  }

  /**
   * The collection name the repo operates on
   * @return The collection name
   */
  public String collectionName() {
    return collectionName;
  }

  /**
   * Save a document in the specified collection
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   * @param jsonObject the document
   * @return A future containing the saved object
   */
  @Override
  public Future<JsonObject> save(JsonObject jsonObject) {
    return mongo.save(collectionName(), jsonObject).compose(this::get);
  }

  /**
   * list all documents
   * @return A future containing a list of all documents
   */
  @Override
  public Future<JsonObject> get(String id) {
    return mongo.findOne(collectionName(), idDocument(id), null);
  }

  /**
   * list all documents
   * @return A future containing a list of all documents
   */
  @Override
  public Future<List<JsonObject>> list() {
    return mongo.find(collectionName(), new JsonObject());
  }

  /**
   * Find matching documents using a query object
   * @param query query used to match documents
   * @return A future containing a list of all matching documents
   */
  @Override
  public Future<List<JsonObject>> find(JsonObject query) {
    return mongo.find(collectionName(), query);
  }

  /**
   * Find a single matching document
   * @param query the query used to match the document
   * @return A future containing a single matching document
   */
  @Override
  public Future<JsonObject> findOne(JsonObject query) {
    return mongo.findOne(collectionName(), query, null);
  }

  /**
   * Find a single matching document in the specified collection and update it.
   * @param id the id used to match the document
   * @param update used to describe how the documents will be updated
   * @return A future containing the updated document
   */
  @Override
  public Future<JsonObject> updateById(String id, JsonObject update) {
    return mongo.findOneAndUpdate(collectionName, idDocument(id), update);
  }

  /**
   * Find a single matching document in the specified collection and update it.
   * @param query the query used to match the document
   * @param update used to describe how the documents will be updated
   * @return A future containing the updated document
   */
  @Override
  public Future<JsonObject> update(JsonObject query, JsonObject update) {
    return mongo.findOneAndUpdate(collectionName, query, update);
  }

  /**
   * Find a single matching document in the specified collection and delete it.
   * @param id the id used to match the document
   * @return A future containing the deleted document
   */
  @Override
  public Future<JsonObject> deleteById(String id) {
    return mongo.findOneAndDelete(collectionName, new JsonObject().put("_id", id));
  }

  /**
   * Find a single matching document in the specified collection and delete it.
   * @param query the query used to match the document
   * @return A future containing the deleted document
   */
  @Override
  public Future<JsonObject> delete(JsonObject query) {
    return mongo.findOneAndDelete(collectionName, query);
  }

  /**
   * Close the Repo
   * @return a {@code Future} of the asynchronous result
   */
  @Override
  public Future<Void> close() {
    return mongo.close();
  }
}
