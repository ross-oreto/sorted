package io.sorted.thing;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceProxyBuilder;

import java.util.List;

@ProxyGen
@VertxGen
public interface ThingService {
  static ThingService create(Vertx vertx) {
    return new ServiceProxyBuilder(vertx).setAddress(ThingService.class.getName()).build(ThingService.class);
  }

  Future<Void> close();

  Future<JsonObject> save(JsonObject jsonObject);

  Future<List<JsonObject>> list();
}
