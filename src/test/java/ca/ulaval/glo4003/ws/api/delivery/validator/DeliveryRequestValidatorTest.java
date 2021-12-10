package ca.ulaval.glo4003.ws.api.delivery.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.api.delivery.dto.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.api.delivery.dto.validator.DeliveryRequestValidator;
import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeliveryRequestValidatorTest {
  private static final String AN_INVALID_LOCATION = "An invalid location";

  @Mock private Validator fieldValidator;

  private DeliveryRequestValidator deliveryRequestValidator;

  @BeforeEach
  void setUp() {
    deliveryRequestValidator = new DeliveryRequestValidator(fieldValidator);
  }

  @Test
  void givenDeliveryRequest_whenValidate_thenCallValidateFields() {
    // given
    DeliveryLocationRequest deliveryLocationRequest = new DeliveryLocationRequest();

    // when
    deliveryRequestValidator.validate(deliveryLocationRequest);

    // then
    verify(fieldValidator).validate(deliveryLocationRequest);
  }

  @Test
  void
      givenFieldValidatorThrowsInvalidFormatException_whenValidate_thenThrowInvalidFormatException() {
    // given
    DeliveryLocationRequest deliveryLocationRequest = new DeliveryLocationRequest();
    deliveryLocationRequest.setLocation(AN_INVALID_LOCATION);
    doThrow(InvalidFormatException.class).when(fieldValidator).validate(deliveryLocationRequest);
    deliveryRequestValidator = new DeliveryRequestValidator(fieldValidator);

    // when
    Executable exception = () -> deliveryRequestValidator.validate(deliveryLocationRequest);

    // then
    assertThrows(InvalidFormatException.class, exception);
  }
}
