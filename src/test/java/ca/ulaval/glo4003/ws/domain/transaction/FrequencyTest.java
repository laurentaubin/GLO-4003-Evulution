package ca.ulaval.glo4003.ws.domain.transaction;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.domain.transaction.exception.InvalidFrequencyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class FrequencyTest {

  @Test
  void givenValidFrequencyString_whenFromString_thenReturnCorrectFrequency() {
    // given
    var validFrequencyString = "monthly";

    // when
    var actualFrequency = Frequency.fromString(validFrequencyString);

    // then
    assertThat(actualFrequency.getFrequency()).matches(validFrequencyString);
  }

  @Test
  void givenInvalidFrequencyString_whenFromString_thenThrowInvalidFrequency() {
    // given
    var invalidFrequencyString = "InvalidFrequency";

    // when
    Executable action = () -> Frequency.fromString(invalidFrequencyString);

    // then
    assertThrows(InvalidFrequencyException.class, action);
  }
}
