package ca.ulaval.glo4003.ws.domain.assembly.time;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.domain.assembly.exception.InvalidAssemblyTimeOperationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class AssemblyTimeTest {

  @Test
  void givenAnAssemblyTime_whenInWeeks_thenReturnAmountOfWeeks() {
    // given
    int expectedAmountOfWeeks = 4;
    AssemblyTime productionTime = new AssemblyTime(expectedAmountOfWeeks);

    // when
    int actualAmountOfWeeks = productionTime.inWeeks();

    // then
    assertThat(expectedAmountOfWeeks).isEqualTo(actualAmountOfWeeks);
  }

  @Test
  void givenAnAssemblyTime_whenSubtractWeeks_thenSubtractsWeeks() {
    // given
    int expectedAmountOfWeeks = 10;
    int amountOfWeeksToSubtract = 6;
    AssemblyTime expectedAmountOfWeeksAfterSubtraction = new AssemblyTime(4);
    AssemblyTime assemblyTime = new AssemblyTime(expectedAmountOfWeeks);

    // when
    AssemblyTime subtractedAssemblyTime = assemblyTime.subtractWeeks(amountOfWeeksToSubtract);

    // then
    assertThat(expectedAmountOfWeeksAfterSubtraction).isEqualTo(subtractedAssemblyTime);
  }

  @Test
  void givenMoreWeeksThanAvailable_whenSubtractingWeeks_thenThrowsInvalidOperationException() {
    // given
    AssemblyTime assemblyTime = new AssemblyTime(5);
    int weeksToSubtract = 10;

    // when
    Executable subtractingWeeks = () -> assemblyTime.subtractWeeks(weeksToSubtract);

    // then
    assertThrows(InvalidAssemblyTimeOperationException.class, subtractingWeeks);
  }

  @Test
  public void givenAssemblyTimeOfZero_whenIsOver_thenReturnTrue() {
    // given
    AssemblyTime assemblyTime = new AssemblyTime(0);

    // when
    boolean isOver = assemblyTime.isOver();

    // then
    assertThat(isOver).isTrue();
  }

  @Test
  public void givenAssemblyTimeOfMoreThanZero_whenIsOver_thenReturnFalse() {
    // given
    AssemblyTime assemblyTime = new AssemblyTime(12);

    // when
    boolean isOver = assemblyTime.isOver();

    // then
    assertThat(isOver).isFalse();
  }

  @Test
  public void givenTwoAssemblyTimes_whenSubtract_thenReturnAssemblyTimeWithSubtractedTime() {
    // given
    AssemblyTime anAssemblyTime = new AssemblyTime(10);
    AssemblyTime anotherAssemblyTime = new AssemblyTime(2);
    AssemblyTime subtractedAssemblyTime = new AssemblyTime(8);

    // when
    AssemblyTime actualAssemblyTime = anAssemblyTime.subtract(anotherAssemblyTime);

    // then
    assertThat(actualAssemblyTime).isEqualTo(subtractedAssemblyTime);
  }

  @Test
  public void givenTwoAssemblyTimes_whenSubtractSmallerWithLarger_thenReturnThrowException() {
    // given
    AssemblyTime anAssemblyTime = new AssemblyTime(10);
    AssemblyTime anotherAssemblyTime = new AssemblyTime(2);

    // when
    Executable subtracting = () -> anotherAssemblyTime.subtract(anAssemblyTime);

    // then
    assertThrows(InvalidAssemblyTimeOperationException.class, subtracting);
  }

  @Test
  public void givenTwoAssemblyTimes_whenAdd_thenReturnAssemblyTimeWithAddedTimes() {
    // given
    AssemblyTime anAssemblyTime = new AssemblyTime(10);
    AssemblyTime anotherAssemblyTime = new AssemblyTime(2);
    AssemblyTime addedAssemblyTime = new AssemblyTime(12);

    // when
    AssemblyTime actualAssemblyTime = anAssemblyTime.add(anotherAssemblyTime);

    // then
    assertThat(actualAssemblyTime).isEqualTo(addedAssemblyTime);
  }
}
