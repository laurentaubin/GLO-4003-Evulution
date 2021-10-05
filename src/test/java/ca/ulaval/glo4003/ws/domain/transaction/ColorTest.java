package ca.ulaval.glo4003.ws.domain.transaction;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.domain.transaction.exception.InvalidVehicleColorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ColorTest {
  private static final String VALID_COLOR_STRING = "white";
  private static final String INVALID_COLOR_STRING = "not white";

  @Test
  public void givenAValidColorString_whenFromString_thenReturnValidColor() {
    // when
    Color color = Color.fromString(VALID_COLOR_STRING);

    // then
    assertThat(color).isEquivalentAccordingToCompareTo(Color.WHITE);
  }

  @Test
  public void givenAnInvalidColorString_whenFromString_thenThrowInvalidVehicleColorException() {
    // when
    Executable creatingColorFromColorString = () -> Color.fromString(INVALID_COLOR_STRING);

    // then
    assertThrows(InvalidVehicleColorException.class, creatingColorFromColorString);
  }
}
