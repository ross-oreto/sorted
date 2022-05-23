package io.sorted.app.error;

import am.ik.yavi.core.ConstraintViolation;
import am.ik.yavi.core.ConstraintViolationsException;
import io.sorted.app.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Structure to represent validation errors HTTP 400 responses
 */
public class ValidationErrors {
  private final int code;
  private final Collection<ValidationError> errors;

  public ValidationErrors(ConstraintViolationsException exception) {
    this.code = HttpStatus.BAD_REQUEST.value();
    this.errors = new ArrayList<>();
    for (ConstraintViolation violation : exception.violations()) {
      errors.add(new ValidationError(violation.name()
        , violation.message()
        , violation.messageKey()
        , violation.locale().toString()
        , violation.violatedValue()
        , violation.args()));
    }
  }

  public int getCode() {
    return code;
  }
  public Collection<ValidationError> getErrors() {
    return errors;
  }
}
