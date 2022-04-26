package io.sorted.thing;

import io.sorted.app.service.RepoImpl;
import io.vertx.ext.mongo.MongoClient;

public class ThingRepoImpl extends RepoImpl implements ThingRepo {
  public ThingRepoImpl(MongoClient mongo, String collectionName) {
    super(mongo, collectionName);
  }
}
