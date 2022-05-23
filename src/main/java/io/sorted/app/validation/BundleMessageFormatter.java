package io.sorted.app.validation;

import am.ik.yavi.message.MessageFormatter;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Message Formatter implementation that reads messages in messages.properties
 */
public enum BundleMessageFormatter implements MessageFormatter {
  SINGLETON;

  /**
   * Lookup the language specific message from messages
   * @param messageKey The property to lookup
   * @param defaultMessageFormat The default message if key is not found
   * @param args The arguments for the message
   * @param locale Locale of the message
   * @return The message
   */
  @Override
  public String format(String messageKey
    , String defaultMessageFormat
    , Object[] args
    , Locale locale) {
    ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
    String format;
    try {
      format = resourceBundle.getString(messageKey);
    }
    catch (MissingResourceException e) {
      format = defaultMessageFormat;
    }
    try {
      // try to translate the field name, the first argument
      String target = resourceBundle.getString((String) args[0]);
      args[0] = target;
    }
    catch (MissingResourceException ignored) {}
    return new MessageFormat(format, locale).format(args);
  }
}
