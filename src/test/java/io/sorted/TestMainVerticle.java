package io.sorted;

import io.sorted.app.MainVerticle;
import io.sorted.app.conf.IMode;
import io.sorted.info.InfoModule;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(VertxUnitRunner.class)
public class TestMainVerticle {
  static final int TEST_PORT = 8889;
  static final JsonObject testConfig = new JsonObject()
    .put(MainVerticle.PORT_PROP, TEST_PORT);
  @Rule
  public RunTestOnContext rule = new RunTestOnContext();

  @Before
  public void deploy_verticle(TestContext testContext) {
    Vertx vertx = rule.vertx();
    MainVerticle mainVerticle = new MainVerticle(){
      @Override
      public String getMode() {
        return Mode.test.name();
      }
      @Override
      public boolean isDebugging() {
        return false;
      }
      @Override
      protected void onConfigured() {
        this.config.mergeIn(testConfig, true);
      }
    };
    vertx.deployVerticle(mainVerticle, testContext.asyncAssertSuccess());
  }

  @Test
  public void verticle_deployed(TestContext testContext) throws Throwable {
    Async async = testContext.async();
    async.complete();
  }

  @Test
  public void checkInfo(TestContext context) throws Throwable {
    HttpClient client = rule.vertx().createHttpClient();

    client.request(HttpMethod.GET, TEST_PORT, "localhost", "/info")
      .compose(req -> req.send().compose(HttpClientResponse::body))
      .onComplete(context.asyncAssertSuccess(buffer -> context.verify(v -> {
        String json = buffer.toString();
        JsonObject response = new JsonObject(json);
        assertEquals(IMode.getJavaVersion(), response.getString(InfoModule.JAVA_VER_PROP));
        assertEquals(IMode.Mode.test.name(), response.getString(InfoModule.MODE_PROP));
        assertFalse(response.getBoolean(InfoModule.DEBUG_PROP));
      })));
  }
}
