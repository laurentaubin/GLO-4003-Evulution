package ca.ulaval.glo4003.ws.service.transaction.dto.validators;

import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.service.transaction.dto.PaymentRequest;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentRequestValidatorTest {
  @Mock private Validator fieldValidator;

  private PaymentRequestValidator paymentRequestValidator;

  @BeforeEach
  void setUp() {
    paymentRequestValidator = new PaymentRequestValidator(fieldValidator);
  }

  @Test
  void givenPaymentRequest_whenValidate_thenCallValidateFields() {
    // given
    var paymentRequest = new PaymentRequest();

    // when
    paymentRequestValidator.validate(paymentRequest);

    // then
    verify(fieldValidator).validate(paymentRequest);
  }
}
