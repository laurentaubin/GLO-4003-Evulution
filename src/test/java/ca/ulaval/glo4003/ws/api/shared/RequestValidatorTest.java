package ca.ulaval.glo4003.ws.api.shared;

import ca.ulaval.glo4003.ws.api.delivery.dto.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import ca.ulaval.glo4003.ws.api.transaction.request.ConfigureBatteryRequest;
import ca.ulaval.glo4003.ws.api.transaction.request.ConfigurePaymentRequest;
import ca.ulaval.glo4003.ws.api.transaction.request.ConfigureVehicleRequest;
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
  private final String ANY_NON_NULL_LOCATION = "any location";
  private final String AN_INVALID_LOCATION = "invalid location";

  @Mock private Validator fieldValidator;

  private RequestValidator requestValidator;

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
  void givenConfigurePaymentRequest_whenValidate_thenCallValidateFields() {
    // given
    ConfigurePaymentRequest configurePaymentRequest = new ConfigurePaymentRequest();

    // when
    requestValidator.validate(configurePaymentRequest);

    // then
    verify(fieldValidator).validate(configurePaymentRequest);
  }

  @Test
  void givenConfigureBatteryRequest_whenValidate_thenCallValidateFields() {
    // given
    ConfigureBatteryRequest configureBatteryRequest = new ConfigureBatteryRequest();

    // when
    requestValidator.validate(configureBatteryRequest);

    // then
    verify(fieldValidator).validate(configureBatteryRequest);
  }

  @Test
  void givenConfigureVehicleRequest_whenValidate_thenCallValidateFields() {
    // given
    ConfigureVehicleRequest configureVehicleRequest = new ConfigureVehicleRequest();

    // when
    requestValidator.validate(configureVehicleRequest);

    // then
    verify(fieldValidator).validate(configureVehicleRequest);
  }
}
