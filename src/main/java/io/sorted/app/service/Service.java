package io.sorted.app.service;

import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ServiceProxyBuilder;

public class Service {
  public static <T, I extends T> void register(Vertx vertx, Class<T> tClass, I impl) {
    new ServiceBinder(vertx)
      .setAddress(tClass.getName())
      .register(tClass, impl);
  }

  public static <T> T get(Vertx vertx, Class<T> aClass) {
    return new ServiceProxyBuilder(vertx).setAddress(aClass.getName()).build(aClass);
  }
}
