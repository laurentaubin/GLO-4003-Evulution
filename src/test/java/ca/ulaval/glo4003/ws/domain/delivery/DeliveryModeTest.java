package ca.ulaval.glo4003.ws.domain.delivery;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.domain.delivery.exception.InvalidDeliveryModeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class DeliveryModeTest {
  @Test
  void givenValidMode_whenFromString_thenMatchesCorrectMode() {
    // given
    String validMode = "At campus";

    // when
    DeliveryMode deliveryMode = DeliveryMode.fromString(validMode);

    // then
    assertThat(deliveryMode.getDeliveryMode()).isEqualTo(validMode);
  }

  @Test
  void givenInvalidMode_whenFromString_thenThrowInvalidDeliveryModeException() {
    // given
    String invalidMode = "invalid mode";

    // when
    Executable exception = () -> DeliveryMode.fromString(invalidMode);

    // then
    assertThrows(InvalidDeliveryModeException.class, exception);
  }
}
