package ca.ulaval.glo4003.ws.api.user.validator;

import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.user.BirthDateValidator;
import ca.ulaval.glo4003.ws.domain.user.exception.BirthDateInTheFutureException;
import ca.ulaval.glo4003.ws.domain.user.exception.InvalidDateFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BirthDateValidatorTest {
  private static final String A_DATE_FORMAT = "yyyy-MM-dd";
  private static final String A_DATE_CORRECTLY_FORMATTED = "1150-01-21";
  private static final String A_DATE_INCORRECTLY_FORMATTED = "21-01-2150";
  private static final String AN_IMPOSSIBLE_DATE = "1999-99-99";
  private static final LocalDate A_PRESENT_DAY_DATE = LocalDate.of(2000, 1, 1);
  private static final String A_DATE_IN_THE_FUTURE = "3150-05-05";

  @Mock private LocalDateProvider localDateProvider;

  private BirthDateValidator validator;

  @BeforeEach
  public void setup() {
    validator = new BirthDateValidator(A_DATE_FORMAT, localDateProvider);
  }

  @Test
  public void givenACorrectlyFormattedDate_whenValidate_thenDoesNotThrow() {
    // given
    given(localDateProvider.today()).willReturn(A_PRESENT_DAY_DATE);

    // when
    Executable validatingBirthDate = () -> validator.validate(A_DATE_CORRECTLY_FORMATTED);

    // then
    assertDoesNotThrow(validatingBirthDate);
  }

  @Test
  public void givenADateIncorrectlyFormatted_whenValidate_thenThrowInvalidDateFormatException() {
    // when
    Executable validatingBirthDate = () -> validator.validate(A_DATE_INCORRECTLY_FORMATTED);

    // then
    assertThrows(InvalidDateFormatException.class, validatingBirthDate);
  }

  @Test
  public void givenAnImpossibleDate_whenValidate_thenThrowInvalidDateFormatException() {
    // when
    Executable validatingBirthDate = () -> validator.validate(AN_IMPOSSIBLE_DATE);

    // then
    assertThrows(InvalidDateFormatException.class, validatingBirthDate);
  }

  @Test
  public void givenDateInTheFuture_whenValidate_thenThrowBirthDateInTheFutureException() {
    // given
    given(localDateProvider.today()).willReturn(A_PRESENT_DAY_DATE);

    // when
    Executable validatingBirthDate = () -> validator.validate(A_DATE_IN_THE_FUTURE);

    // then
    assertThrows(BirthDateInTheFutureException.class, validatingBirthDate);
  }
}
