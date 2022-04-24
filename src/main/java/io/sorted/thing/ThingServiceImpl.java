package io.sorted.thing;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class ThingServiceImpl implements ThingService {
  private final MongoClient mongo;

  public ThingServiceImpl(MongoClient mongo) {
    this.mongo = mongo;
  }

  @Override
  public Future<JsonArray> list(String collection) {
    return Future.succeededFuture(
      new JsonArray()
        .add(new JsonObject().put("name", "thing1"))
        .add(new JsonObject().put("name", "thing2"))
        .add(new JsonObject().put("name", "thing3"))
    );
  }
}
