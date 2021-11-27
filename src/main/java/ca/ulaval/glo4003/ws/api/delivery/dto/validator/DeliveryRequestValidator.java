package ca.ulaval.glo4003.ws.api.delivery.dto.validator;

import ca.ulaval.glo4003.ws.api.shared.RequestValidator;
import jakarta.validation.Validator;

public class DeliveryRequestValidator extends RequestValidator {

  public DeliveryRequestValidator(Validator validator) {
    super(validator);
  }

  @Override
  public void validate(Object deliveryRequest) {
    validateFields(deliveryRequest);
  }
}
