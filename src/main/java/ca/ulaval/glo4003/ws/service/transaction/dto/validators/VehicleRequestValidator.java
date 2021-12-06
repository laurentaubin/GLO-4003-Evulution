package ca.ulaval.glo4003.ws.service.transaction.dto.validators;

import ca.ulaval.glo4003.ws.api.shared.RequestValidator;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class VehicleRequestValidator extends RequestValidator {

  public VehicleRequestValidator() {
    this(Validation.buildDefaultValidatorFactory().getValidator());
  }

  public VehicleRequestValidator(Validator validator) {
    super(validator);
  }

  public void validate(Object vehicleRequest) {
    validateFields(vehicleRequest);
  }
}
