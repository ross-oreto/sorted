package io.sorted.app.service;

/**
 * Interface to specify a datasource collection
 */
public interface Collectable {
  /**
   * The datasource collection name to target
   * @return The collection name
   */
  String collectionName();
}
