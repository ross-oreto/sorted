package io.sorted.command;

import io.vertx.core.spi.launcher.DefaultCommandFactory;

public class AppVersionCommandFactory extends DefaultCommandFactory<AppVersionCommand> {
  public AppVersionCommandFactory() {
    super(AppVersionCommand.class, AppVersionCommand::new);
  }
}
