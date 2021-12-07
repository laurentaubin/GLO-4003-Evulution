package ca.ulaval.glo4003.ws.api.shared;

import ca.ulaval.glo4003.ws.api.delivery.dto.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import ca.ulaval.glo4003.ws.service.transaction.dto.BatteryRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.PaymentRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.VehicleRequest;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RequestValidatorTest {
  @Mock private Validator fieldValidator;

  private RequestValidator requestValidator;
  private final String ANY_NON_NULL_LOCATION = "any location";
  private final String AN_INVALID_LOCATION = "invalid location";

  @BeforeEach
  void setUp() {
    requestValidator = new RequestValidator(fieldValidator);
  }

  @Test
  void givenDeliveryRequest_whenValidate_thenCallValidateFields() {
    // given
    DeliveryLocationRequest deliveryLocationRequest = new DeliveryLocationRequest();
    deliveryLocationRequest.setLocation(ANY_NON_NULL_LOCATION);
    deliveryLocationRequest.setMode(AN_INVALID_LOCATION);

    // when
    requestValidator.validate(deliveryLocationRequest);

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

    // when
    Executable exception = () -> requestValidator.validate(deliveryLocationRequest);

    // then
    assertThrows(InvalidFormatException.class, exception);
  }

  @Test
  void givenPaymentRequest_whenValidate_thenCallValidateFields() {
    // given
    var paymentRequest = new PaymentRequest();

    // when
    requestValidator.validate(paymentRequest);

    // then
    verify(fieldValidator).validate(paymentRequest);
  }

  @Test
  void givenBatteryRequest_whenValidate_thenCallValidateFields() {
    // given
    var batteryRequest = new BatteryRequest();

    // when
    requestValidator.validate(batteryRequest);

    // then
    verify(fieldValidator).validate(batteryRequest);
  }

  @Test
  void givenVehicleRequest_whenValidate_thenCallValidateFields() {
    // given
    var vehicleRequest = new VehicleRequest();

    // when
    requestValidator.validate(vehicleRequest);

    // then
    verify(fieldValidator).validate(vehicleRequest);
  }
}
