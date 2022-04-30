package io.sorted.product;

public interface IProduct {
  static String collection() {
    return "products";
  }

  String name();
  String description();
  Integer rank();

  default boolean unranked() {
    return rank() == null;
  }
}
