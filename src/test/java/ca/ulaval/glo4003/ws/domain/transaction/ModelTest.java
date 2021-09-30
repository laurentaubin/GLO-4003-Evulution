package ca.ulaval.glo4003.ws.domain.transaction;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.domain.transaction.exception.InvalidModelException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ModelTest {

  @Test
  void givenValidModelString_whenFromString_thenReturnCorrectModel() {
    // given
    String validModelString = "Vandry";

    // when
    Model actualModel = Model.fromString(validModelString);

    // then
    assertThat(actualModel.getModel()).matches(validModelString);
  }

  @Test
  void givenInvalidModelString_whenFromString_thenThrowInvalidModel() {
    // given
    String invalidModelString = "InvalidModel";

    // when
    Executable action = () -> Model.fromString(invalidModelString);

    // then
    assertThrows(InvalidModelException.class, action);
  }
}
