package io.sorted.product;

import am.ik.yavi.core.Constraint;
import am.ik.yavi.core.Validator;
import io.sorted.app.conf.IMode;
import io.sorted.app.module.RepoModule;
import io.sorted.app.validation.i18nValidatorBuilder;

/**
 * Module to manage products
 */
public class ProductModule extends RepoModule<ProductRepo, Product> {
  public ProductModule(IMode mode, Class<ProductRepo> repoClass, Class<Product> collectionClass) {
    super(mode, repoClass, collectionClass);
  }

  /**
   * Validation used before creating products
   * @return A new validator
   */
  @Override
  protected Validator<Product> saveValidator() {
    return i18nValidatorBuilder.<Product>of()
      .constraint(Product::name, "name", Constraint::notNull)
      .build();
  }

  /**
   * Get the name of the module, which implies the subdomain route
   * @return Module name
   */
  @Override
  public String getName() {
    return "product";
  }
}
