package ca.ulaval.glo4003.ws.domain.delivery;

import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.domain.delivery.exception.InvalidLocationException;
import org.junit.jupiter.api.Assertions;
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
    Assertions.assertEquals(location.getCampusLocation(), validLocation);
  }

  @Test
  void givenInvalidLocation_whenFromString_thenThrowInvalidLocationException() {
    // given
    String invalidLocation = "invalid location";

    // when
    Executable exception = () -> Location.fromString(invalidLocation);

    // then
    assertThrows(InvalidLocationException.class, exception);
  }
}
