package io.sorted.app.error;

/**
 *
 * @param name The field name
 * @param message The error message
 * @param messageKey The error message key
 * @param locale The request locale
 * @param violatedValue Invalid value
 * @param args Arguments used in error message
 */
public record ValidationError(String name
  , String message
  , String messageKey
  , String locale
  , Object violatedValue
  , Object[] args) {
}
