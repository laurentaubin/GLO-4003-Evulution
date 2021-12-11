package ca.ulaval.glo4003.ws.api.shared;

import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class RequestValidator {

  protected final Validator requestValidator;

  public RequestValidator() {
    this(Validation.buildDefaultValidatorFactory().getValidator());
  }

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

  public void validate(Object requestDto) {
    validateDtoIsNotNull(requestDto);
    validateFields(requestDto);
  }

  private void validateDtoIsNotNull(Object requestDto) {
    if (requestDto == null) {
      throw new InvalidFormatException("The body cannot be empty");
    }
  }
}
