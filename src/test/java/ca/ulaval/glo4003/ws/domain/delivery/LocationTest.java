package ca.ulaval.glo4003.ws.domain.delivery;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.domain.delivery.exception.InvalidLocationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class LocationTest {
  @Test
  void givenValidLocation_whenFromString_thenMatchesCorrectLocation() {
    // given
    String validLocation = "Vachon";

    // when
    Location location = Location.fromString(validLocation);

    // then
    assertThat(location.getCampusLocation()).isEqualTo(validLocation);
  }

  @Test
  void givenInvalidLocation_whenFromString_thenThrowInvalidLocationException() {
    // given
    String invalidLocation = "invalid location";

    // when
    Executable convertingFromString = () -> Location.fromString(invalidLocation);

    // then
    assertThrows(InvalidLocationException.class, convertingFromString);
  }
}
