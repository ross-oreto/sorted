package io.sorted.app.service;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Interface to support some standard database operations
 */
@VertxGen(concrete = false)
public interface Repo {
  /**
   * Close the Repo
   * @return a {@code Future} of the asynchronous result
   */
  Future<Void> close();

  /**
   * Save a document in the specified collection
   * This operation might change <i>_id</i> field of <i>document</i> parameter
   * @param jsonObject the document
   * @return A future containing the saved object
   */
  Future<JsonObject> save(JsonObject jsonObject);

  /**
   * Find a single document which matches the id
   * @param id The id to retrieve
   * @return A future containing the retrieved document
   */
  Future<JsonObject> get(String id);

  /**
   * list all documents
   * @return A future containing a list of all documents
   */
  Future<List<JsonObject>> list();

  /**
   * Find matching documents using a query object
   * @param query query used to match documents
   * @return A future containing a list of all matching documents
   */
  Future<List<JsonObject>> find(JsonObject query);

  /**
   * Find a single matching document
   * @param query the query used to match the document
   * @return A future containing a single matching document
   */
  Future<JsonObject> findOne(JsonObject query);

  /**
   * Find a single matching document in the specified collection and update it.
   * @param id the id used to match the document
   * @param update used to describe how the documents will be updated
   * @return A future containing the updated document
   */
  Future<JsonObject> updateById(String id, JsonObject update);

  /**
   * Find a single matching document in the specified collection and update it.
   * @param query the query used to match the document
   * @param update used to describe how the documents will be updated
   * @return A future containing the updated document
   */
  Future<JsonObject> update(JsonObject query, JsonObject update);

  /**
   * Find a single matching document in the specified collection and replace it.
   * @param id the id used to match the document
   * @param update used to describe how the documents will be replaced
   * @return A future containing the replaced document
   */
  Future<JsonObject> replaceById(String id, JsonObject update);

  /**
   * Find a single matching document in the specified collection and replace it.
   * @param query the query used to match the document
   * @param update used to describe how the documents will be replaced
   * @return A future containing the replaced document
   */
  Future<JsonObject> replace(JsonObject query, JsonObject update);

  /**
   * Find a single matching document in the specified collection and delete it.
   * @param id the id used to match the document
   * @return A future containing the deleted document
   */
  Future<JsonObject> deleteById(String id);

  /**
   * Find a single matching document in the specified collection and delete it.
   * @param query the query used to match the document
   * @return A future containing the deleted document
   */
  Future<JsonObject> delete(JsonObject query);

  /**
   * Delete all documents in the collection
   * @return The number of removed documents
   */
  Future<Long> deleteAll();

  /**
   * Drop the collection
   * @return {@code Future} of the asynchronous result
   */
  Future<Void> drop();
}
