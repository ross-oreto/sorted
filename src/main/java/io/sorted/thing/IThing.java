package io.sorted.thing;

public interface IThing {
  String type();
  String name();
  String description();

  Integer rank();

  default boolean unranked() {
    return rank() == null;
  }
}
