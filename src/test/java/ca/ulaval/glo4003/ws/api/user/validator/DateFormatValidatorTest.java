package ca.ulaval.glo4003.ws.api.user.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.api.user.exception.InvalidFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class DateFormatValidatorTest {
  private static final String DATE_FORMAT = "yyyy-MM-dd";
  private static final String DATE_CORRECTLY_FORMATTED = "2150-01-21";
  private static final String DATE_INCORRECTLY_FORMATTED = "21-01-2150";
  private static final String DATE_IMPOSSIBLE = "2150-99-99";

  private DateFormatValidator validator;

  @BeforeEach
  public void setup() {
    validator = new DateFormatValidator(DATE_FORMAT);
  }

  @Test
  public void givenACorrectlyFormattedDate_whenValidateFormat_thenDoesNotThrow() {
    // when
    Executable validatingRequestFormat = () -> validator.validateFormat(DATE_CORRECTLY_FORMATTED);

    // then
    assertDoesNotThrow(validatingRequestFormat);
  }

  @Test
  public void
      givenADateIncorrectlyFormatted_whenValidateFormat_thenThrowInvalidDateFormatException() {
    // when
    Executable validatingRequestFormat = () -> validator.validateFormat(DATE_INCORRECTLY_FORMATTED);

    // then
    assertThrows(InvalidFormatException.class, validatingRequestFormat);
  }

  @Test
  public void givenAnImpossibleDate_whenValidateFormat_thenThrowInvalidDateFormatException() {
    // when
    Executable validatingRequestFormat = () -> validator.validateFormat(DATE_IMPOSSIBLE);

    // then
    assertThrows(InvalidFormatException.class, validatingRequestFormat);
  }
}
