package ca.ulaval.glo4003.ws.service.transaction.dto.validators;

import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.service.transaction.dto.BatteryRequest;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatteryRequestValidatorTest {
  @Mock private Validator fieldValidator;

  private BatteryRequestValidator batteryRequestValidator;

  @BeforeEach
  void setUp() {
    batteryRequestValidator = new BatteryRequestValidator(fieldValidator);
  }

  @Test
  void givenBatteryRequest_whenValidate_thenCallValidateFields() {
    // given
    var batteryRequest = new BatteryRequest();

    // when
    batteryRequestValidator.validate(batteryRequest);

    // then
    verify(fieldValidator).validate(batteryRequest);
  }
}
