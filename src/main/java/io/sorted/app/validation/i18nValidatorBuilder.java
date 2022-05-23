package io.sorted.app.validation;

import am.ik.yavi.builder.ValidatorBuilder;

/**
 * Small wrapper around ValidatorBuilder which always looks for the "messages" resource bundle
 */
public class i18nValidatorBuilder {
  public static <X> ValidatorBuilder<X> of() {
    return new ValidatorBuilder<X>().messageFormatter(BundleMessageFormatter.SINGLETON);
  }
}
