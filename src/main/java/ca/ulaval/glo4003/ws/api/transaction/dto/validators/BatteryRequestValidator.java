package ca.ulaval.glo4003.ws.api.transaction.dto.validators;

import ca.ulaval.glo4003.ws.api.shared.RequestValidator;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class BatteryRequestValidator extends RequestValidator {

  public BatteryRequestValidator() {
    this(Validation.buildDefaultValidatorFactory().getValidator());
  }

  public BatteryRequestValidator(Validator validator) {
    super(validator);
  }

  public void validate(Object batteryRequest) {
    validateFields(batteryRequest);
  }
}
