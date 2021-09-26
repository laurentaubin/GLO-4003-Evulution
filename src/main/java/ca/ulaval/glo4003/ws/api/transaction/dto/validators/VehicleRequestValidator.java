package ca.ulaval.glo4003.ws.api.transaction.dto.validators;

import ca.ulaval.glo4003.ws.api.shared.RequestValidator;
import jakarta.validation.Validator;

public class VehicleRequestValidator extends RequestValidator {

  public VehicleRequestValidator(Validator validator) {
    super(validator);
  }

  public void validate(Object vehicleRequest) {
    validateFields(vehicleRequest);
  }
}
