package ca.ulaval.glo4003.ws.domain.vehicle;

import ca.ulaval.glo4003.ws.domain.vehicle.exception.InvalidVehicleColorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ColorTest {
  private static final String A_VALID_COLOR_STRING = "white";
  private static final String AN_INVALID_COLOR_STRING = "not white";

  @Test
  public void givenAValidColorString_whenFromString_thenReturnValidColor() {
    // when
    Color color = Color.fromString(A_VALID_COLOR_STRING);

    // then
    assertThat(color).isEquivalentAccordingToCompareTo(Color.WHITE);
  }

  @Test
  public void givenAnInvalidColorString_whenFromString_thenThrowInvalidVehicleColorException() {
    // when
    Executable creatingColorFromColorString = () -> Color.fromString(AN_INVALID_COLOR_STRING);

    // then
    assertThrows(InvalidVehicleColorException.class, creatingColorFromColorString);
  }
}
