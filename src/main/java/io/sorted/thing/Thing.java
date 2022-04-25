package io.sorted.thing;

public record Thing(String type, String name, String description, Integer rank, String _id) implements IThing {
}
