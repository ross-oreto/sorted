package io.sorted.thing;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

public record ThingServiceImpl(MongoClient mongo, String collectionName) implements ThingService {

  @Override
  public Future<JsonObject> save(JsonObject jsonObject) {
    return mongo.save(collectionName(), jsonObject).compose(this::get);
  }

  public Future<JsonObject> get(String id) {
    return mongo.findOne(collectionName(), new JsonObject().put("_id", id), null);
  }

  @Override
  public Future<List<JsonObject>> list() {
    return mongo.find(collectionName(), new JsonObject());
  }

  @Override
  public Future<Void> close() {
    return mongo.close();
  }
}
