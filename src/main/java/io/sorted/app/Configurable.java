package io.sorted.app;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.nio.file.Paths;

public interface Configurable extends IMode {
  String DEFAULT_CONF_EXT = "conf";
  String ENABLED_PROP = "enabled";

  /**
   * Get the Vert.x instance
   * This will be already implemented by any Verticle
   * @return the Vert.x instance
   */
  Vertx getVertx();

  /**
   * Get the configuration for the application
   * @return The final application configuration as a JsonObject
   */
  JsonObject config();

  /**
   * The default config directory for internal config files (within the application jar)
   * @return The relative app path.
   */
  default String getConfPath() {
    return "conf";
  }

  @Override
  default String getMode() {
    return config().getString(MODE_PROP, IMode.super.getMode());
  }

  @Override
  default boolean isDebugging() {
    return config().getBoolean(DEBUG_PROP, IMode.super.isDebugging());
  }

  /**
   * The default file format.
   * @return String indicating the file format.
   */
  default String defaultFormat() {
    return "hocon";
  }

  /**
   * Specifies the config file path
   * @param confPath Path to the config file
   * @param file File name
   * @param confExt File extension
   * @return The config file path
   */
  default String configFilePath(String confPath, String file, String confExt) {
    return Paths.get(confPath, String.format("%s.%s", file, confExt)).toString();
  }

  /**
   * Specifies the config file path
   * @param confPath Path to the config file
   * @param file File name
   * @return The config file path
   */
  default String configFilePath(String confPath, String file) {
    return configFilePath(confPath, file, DEFAULT_CONF_EXT);
  }

  /**
   * Specifies the config file path
   * @param file File name
   * @return The config file path
   */
  default String configFilePath(String file) {
    return configFilePath(getConfPath(), file, DEFAULT_CONF_EXT);
  }

  /**
   * Adds a new config file store
   * @param path Path of the config file
   * @param format Config file format
   * @return ConfigStoreOptions object
   */
  default ConfigStoreOptions addFileStore(String path, String format) {
    return new ConfigStoreOptions()
      .setType("file")
      .setFormat(format)
      .setOptional(true)
      .setConfig(new JsonObject().put("path", path));
  }

  /**
   * Adds a new config file store
   * @return ConfigStoreOptions object
   */
  default ConfigStoreOptions addFileStore(String file) {
    return addFileStore(file, defaultFormat());
  }

  /**
   * Get a new config retriever for system properties
   * @return ConfigRetriever object
   */
  default ConfigRetriever systemRetriever() {
    return ConfigRetriever.create(getVertx()
      , new ConfigRetrieverOptions().addStore(new ConfigStoreOptions().setType("sys")));
  }

  /**
   * Get a new config retriever for environment variables
   * @return ConfigRetriever object
   */
  default ConfigRetriever environmentRetriever() {
    return ConfigRetriever.create(getVertx()
      , new ConfigRetrieverOptions().addStore(new ConfigStoreOptions().setType("env")));
  }

  /**
   * Get the default config retriever
   * @return The default ConfigRetriever
   */
  default ConfigRetriever defaultRetriever() {
    return ConfigRetriever.create(getVertx());
  }

  /**
   * Configure the application using the specified file and listen for changes
   * @param name Config file name
   * @param additionalRetriever Any additional retriever to use
   * @return A future config object
   */
  default Future<JsonObject> configure(String name, ConfigRetriever additionalRetriever) {
    Vertx vertx = getVertx();

    ConfigRetrieverOptions retrieverOptions = new ConfigRetrieverOptions()
      .addStore(addFileStore(configFilePath(name)))
      .addStore(addFileStore(configFilePath(name + "-secrets")));

    ConfigRetriever retriever = ConfigRetriever.create(vertx, retrieverOptions);
    retriever.listen(change -> {
      System.out.println(change.getNewConfiguration().encodePrettily());
      config().mergeIn(change.getNewConfiguration(), true);
      onConfigChange().run();
    });
    additionalRetriever.listen(change -> {
      System.out.println(change.getNewConfiguration().encodePrettily());
      config().mergeIn(change.getNewConfiguration(), true);
      onConfigChange().run();
    });
    return retriever.getConfig()
      .compose(it -> {
        config().mergeIn(it, true);
        return additionalRetriever.getConfig();
      }).compose(it -> Future.succeededFuture(config().mergeIn(it, true)));
  }

  default Future<JsonObject> configure(String name) {
    return configure(name, defaultRetriever());
  }

  default Future<JsonObject> configure() {
    return configure("sorted");
  }

  Runnable onConfigChange();
}
