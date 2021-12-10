package ca.ulaval.glo4003.ws.domain.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.google.common.truth.Truth.assertThat;

class BirthDateTest {
  private static final LocalDate A_BIRTHDATE = LocalDate.of(1993, 7, 18);
  private static final LocalDate ANOTHER_BIRTHDATE = LocalDate.of(213132, 7, 18);

  @Test
  public void givenTwoBirthDatesWithSameDate_whenEquals_thenReturnTrue() {
    // given
    BirthDate firstBirthDate = new BirthDate(A_BIRTHDATE);
    BirthDate secondBirthDate = new BirthDate(A_BIRTHDATE);

    // when
    boolean areBirthDatesEqual = firstBirthDate.equals(secondBirthDate);

    // then
    assertThat(areBirthDatesEqual).isTrue();
  }

  @Test
  public void givenTwoBirthDatesWithDifferentDates_whenEquals_thenReturnFalse() {
    // given
    BirthDate firstBirthDate = new BirthDate(A_BIRTHDATE);
    BirthDate secondBirthDate = new BirthDate(ANOTHER_BIRTHDATE);

    // when
    boolean areBirthDatesEqual = firstBirthDate.equals(secondBirthDate);

    // then
    assertThat(areBirthDatesEqual).isFalse();
  }

  @Test
  public void whenHash_thenReturnHash() {
    // given
    BirthDate aBirthDate = new BirthDate(A_BIRTHDATE);

    // when
    int hashedBirthDate = aBirthDate.hashCode();

    // then
    assertThat(hashedBirthDate).isGreaterThan(0);
  }
}
