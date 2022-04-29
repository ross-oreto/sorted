package io.sorted.app.conf;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Defines running modes for the application such as dev,test,prod,debug
 */
public interface IMode {
  String MODE_PROP = "mode";
  String DEBUG_PROP = "debug";

  /**
   * Get the current JDK version running
   * @return The current java version
   */
  static String getJavaVersion() {
    return Runtime.version().version()
      .stream()
      .map(Object::toString)
      .collect(Collectors.joining("."));
  }

  /**
   * Defines modes for the application. For running in different environments such as dev, test, prod.
   */
  enum Mode {
    local, dev, test, uat, prod;

    /**
     * Get a list of all the mode names
     */
    public static final List<String> names = Arrays.stream(values()).map(Enum::toString).collect(Collectors.toList());

    /**
     * Determines if the mode name is a valid mode
     * @param mode The mode to test
     * @return True if the mode name is valid, false otherwise
     */
    public static boolean isValid(String mode) {
      return names.contains(mode);
    }
  }

  /**
   * Get the current app mode
   * @return The application mode
   */
  default String getMode() {
    String mode = System.getenv(MODE_PROP);
    return mode == null ? System.getProperty(MODE_PROP, Mode.dev.name()) : mode;
  }

  /**
   * Determine if running in a debug mode
   * @return True if in debug mode, false otherwise
   */
  default boolean isDebugging() {
    String debug = System.getenv(DEBUG_PROP);
    debug = debug == null ? System.getProperty(DEBUG_PROP, "false") : debug;
    return debug.equals("true");
  }
}
