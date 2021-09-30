package ca.ulaval.glo4003.ws.api.shared;

import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

public abstract class RequestValidator {

  protected Validator requestValidator;

  public RequestValidator(Validator validator) {
    this.requestValidator = validator;
  }

  protected void validateFields(Object request) {
    var constraintViolations = requestValidator.validate(request);

    if (!constraintViolations.isEmpty()) {
      ConstraintViolation<Object> constraint = constraintViolations.iterator().next();
      String description =
          String.format("%s %s", constraint.getPropertyPath(), constraint.getMessage());
      throw new InvalidFormatException(description);
    }
  }

  public abstract void validate(Object request);
}
