package ca.ulaval.glo4003.ws.service.transaction.dto.validators;

import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.service.transaction.dto.VehicleRequest;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleRequestValidatorTest {
  @Mock private Validator fieldValidator;

  private VehicleRequestValidator vehicleRequestValidator;

  @BeforeEach
  void setUp() {
    vehicleRequestValidator = new VehicleRequestValidator(fieldValidator);
  }

  @Test
  void givenVehicleRequest_whenValidate_thenCallValidateFields() {
    // given
    var vehicleRequest = new VehicleRequest();

    // when
    vehicleRequestValidator.validate(vehicleRequest);

    // then
    verify(fieldValidator).validate(vehicleRequest);
  }
}
