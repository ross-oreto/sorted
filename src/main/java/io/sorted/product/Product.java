package io.sorted.product;

public record Product(String name, String description, Integer rank, String _id) implements IProduct {

}
