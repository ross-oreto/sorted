package io.sorted.product;

import io.sorted.app.service.RepoImpl;
import io.vertx.ext.mongo.MongoClient;

public class ProductRepoImpl extends RepoImpl implements ProductRepo {
  public ProductRepoImpl(MongoClient mongo, String collectionName) {
    super(mongo, collectionName);
  }
}
