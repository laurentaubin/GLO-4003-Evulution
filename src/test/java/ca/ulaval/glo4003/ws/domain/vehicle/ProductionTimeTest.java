package ca.ulaval.glo4003.ws.domain.vehicle;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.domain.vehicle.exception.InvalidOperationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ProductionTimeTest {
  @Test
  void givenAProductionTime_whenInWeeks_thenReturnAmountOfWeeks() {
    // given
    int expectedAmountOfWeeks = 4;
    ProductionTime productionTime = new ProductionTime(expectedAmountOfWeeks);

    // when
    int actualAmountOfWeeks = productionTime.inWeeks();

    // then
    assertThat(expectedAmountOfWeeks).isEqualTo(actualAmountOfWeeks);
  }

  @Test
  void givenProductionTime_whenSubtractWeeks_thenSubtractsWeeks() {
    // given
    int expectedAmountOfWeeks = 10;
    int amountOfWeeksToSubtract = 6;
    ProductionTime expectedAmountOfWeeksAfterSubtraction = new ProductionTime(4);
    ProductionTime productionTime = new ProductionTime(expectedAmountOfWeeks);

    // when
    ProductionTime subtractedProductionTime = productionTime.subtractWeeks(amountOfWeeksToSubtract);

    // then
    assertThat(expectedAmountOfWeeksAfterSubtraction).isEqualTo(subtractedProductionTime);
  }

  @Test
  void givenMoreWeeksThanAvailable_whenSubtractingWeeks_thenThrowsInvalidOperationException() {
    // given
    ProductionTime productionTime = new ProductionTime(5);
    int weeksToSubtract = 10;

    // when
    Executable subtractingWeeks = () -> productionTime.subtractWeeks(weeksToSubtract);

    // then
    assertThrows(InvalidOperationException.class, subtractingWeeks);
  }

  @Test
  public void givenProductionTimeOfZero_whenIsOver_thenReturnTrue() {
    // given
    ProductionTime productionTime = new ProductionTime(0);

    // when
    boolean isOver = productionTime.isOver();

    // then
    assertThat(isOver).isTrue();
  }

  @Test
  public void givenProductionTimeOfMoreThanZero_whenIsOver_thenReturnFalse() {
    // given
    ProductionTime productionTime = new ProductionTime(12);

    // when
    boolean isOver = productionTime.isOver();

    // then
    assertThat(isOver).isFalse();
  }

  @Test
  public void givenTwoProductionTimes_whenSubtract_thenReturnProductionTimeWithSubtractedTime() {
    // given
    ProductionTime aProductionTime = new ProductionTime(10);
    ProductionTime anotherProductionTime = new ProductionTime(2);
    ProductionTime subtractedProductionTime = new ProductionTime(8);

    // when
    ProductionTime actualProductionTime = aProductionTime.subtract(anotherProductionTime);

    // then
    assertThat(actualProductionTime).isEqualTo(subtractedProductionTime);
  }

  @Test
  public void givenTwoProductionTimes_whenSubtractSmallerWithLarger_thenReturnThrowException() {
    // given
    ProductionTime aProductionTime = new ProductionTime(10);
    ProductionTime anotherProductionTime = new ProductionTime(2);

    // when
    Executable subtracting = () -> anotherProductionTime.subtract(aProductionTime);

    // then
    assertThrows(IllegalArgumentException.class, subtracting);
  }

  @Test
  public void givenTwoProductionTimes_whenAdd_thenReturnProductionTimeWithAddedTimes() {
    // given
    ProductionTime aProductionTime = new ProductionTime(10);
    ProductionTime anotherProductionTime = new ProductionTime(2);
    ProductionTime addedProductionTime = new ProductionTime(12);

    // when
    ProductionTime actualProductionTime = aProductionTime.add(anotherProductionTime);

    // then
    assertThat(actualProductionTime).isEqualTo(addedProductionTime);
  }
}
