package ca.ulaval.glo4003.ws.api.user.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import ca.ulaval.glo4003.ws.api.user.exception.BirthDateInTheFutureException;
import ca.ulaval.glo4003.ws.api.util.LocalDateProvider;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BirthDateValidatorTest {
  private static final String DATE_FORMAT = "yyyy-MM-dd";
  private static final String DATE_CORRECTLY_FORMATTED = "1150-01-21";
  private static final String DATE_INCORRECTLY_FORMATTED = "21-01-2150";
  private static final String DATE_IMPOSSIBLE = "1999-99-99";
  private static final LocalDate TODAYS_DATE = LocalDate.of(2000, 1, 1);
  private static final String DATE_IN_THE_FUTURE = "3150-05-05";

  @Mock private LocalDateProvider localDateProvider;

  private BirthDateValidator validator;

  @BeforeEach
  public void setup() {
    validator = new BirthDateValidator(DATE_FORMAT, localDateProvider);
  }

  @Test
  public void givenACorrectlyFormattedDate_whenValidate_thenDoesNotThrow() {
    // given
    given(localDateProvider.today()).willReturn(TODAYS_DATE);

    // when
    Executable validatingBirthDate = () -> validator.validate(DATE_CORRECTLY_FORMATTED);

    // then
    assertDoesNotThrow(validatingBirthDate);
  }

  @Test
  public void givenADateIncorrectlyFormatted_whenValidate_thenThrowInvalidDateFormatException() {
    // when
    Executable validatingBirthDate = () -> validator.validate(DATE_INCORRECTLY_FORMATTED);

    // then
    assertThrows(InvalidFormatException.class, validatingBirthDate);
  }

  @Test
  public void givenAnImpossibleDate_whenValidate_thenThrowInvalidDateFormatException() {
    // when
    Executable validatingBirthDate = () -> validator.validate(DATE_IMPOSSIBLE);

    // then
    assertThrows(InvalidFormatException.class, validatingBirthDate);
  }

  @Test
  public void givenDateInTheFuture_whenValidate_thenThrowBirthDateInTheFutureException() {
    // given
    given(localDateProvider.today()).willReturn(TODAYS_DATE);

    // when
    Executable validatingBirthDate = () -> validator.validate(DATE_IN_THE_FUTURE);

    // then
    assertThrows(BirthDateInTheFutureException.class, validatingBirthDate);
  }
}
