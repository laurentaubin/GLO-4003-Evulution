package ca.ulaval.glo4003.ws.domain.vehicle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductionTimeTest {

  @Test
  void givenAProductionTime_whenInWeeks_thenReturnsAmountOfWeeks() {
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
    productionTime.subtractWeeks(amountOfWeeksToSubtract);

    // then
    assertThat(expectedAmountOfWeeksAfterSubtraction).isEqualTo(productionTime);
  }

  @Test
  void givenMoreWeeksThanAvailable_whenSubtractingWeeks_thenThrowsException() {
    // given
    ProductionTime productionTime = new ProductionTime(5);
    int weeksToSubtract = 10;

    // when
    Executable subtractingWeeks = () -> productionTime.subtractWeeks(weeksToSubtract);

    // then
    assertThrows(IllegalArgumentException.class, subtractingWeeks);
  }
}
