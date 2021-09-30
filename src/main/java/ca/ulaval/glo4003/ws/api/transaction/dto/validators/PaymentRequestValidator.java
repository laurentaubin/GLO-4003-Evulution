package ca.ulaval.glo4003.ws.api.transaction.dto.validators;

import ca.ulaval.glo4003.ws.api.shared.RequestValidator;
import jakarta.validation.Validator;

public class PaymentRequestValidator extends RequestValidator {

  public PaymentRequestValidator(Validator validator) {
    super(validator);
  }

  public void validate(Object paymentRequest) {
    validateFields(paymentRequest);
  }
}