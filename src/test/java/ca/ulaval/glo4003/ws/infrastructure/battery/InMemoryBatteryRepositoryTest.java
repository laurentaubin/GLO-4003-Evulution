package ca.ulaval.glo4003.ws.infrastructure.battery;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.domain.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.battery.InvalidBatteryException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryBatteryRepositoryTest {
  private static final String EXISTING_BATTERY_TYPE = "STANDARD";
  private static final Integer ANY_NRCAN_BATTERY_RANGE = Integer.valueOf(400);
  private static final Integer ANY_CAPACITY = Integer.valueOf(60);
  private static final Integer ANY_PRICE = Integer.valueOf(15000);
  private static final Integer ANY_TIME_TO_PRODUCE = Integer.valueOf(3);

  private static final Battery NON_EXISTING_BATTERY =
      new Battery(
          "non existing type",
          ANY_NRCAN_BATTERY_RANGE,
          ANY_CAPACITY,
          ANY_PRICE,
          ANY_TIME_TO_PRODUCE);

  private static final Battery EXISTING_BATTERY =
      new Battery(
          EXISTING_BATTERY_TYPE,
          ANY_NRCAN_BATTERY_RANGE,
          ANY_CAPACITY,
          ANY_PRICE,
          ANY_TIME_TO_PRODUCE);

  private static final Map<String, Battery> EXISTING_BATTERIES =
      Map.of(EXISTING_BATTERY.getType(), EXISTING_BATTERY);

  private BatteryRepository batteryRepository;

  @BeforeEach
  void setUp() {
    batteryRepository = new InMemoryBatteryRepository();
  }

  @Test
  void givenExistingBattery_whenFindByType_thenValidationPassesSuccessfully() {
    // given
    batteryRepository.save(EXISTING_BATTERIES);

    // when/then
    assertDoesNotThrow(() -> batteryRepository.findByType(EXISTING_BATTERY.getType()));
  }

  @Test
  void givenExistingBattery_whenSave_thenValidationFails() {
    // given
    batteryRepository.save(EXISTING_BATTERIES);

    // when/then
    assertThrows(
        InvalidBatteryException.class,
        () -> batteryRepository.findByType(NON_EXISTING_BATTERY.getType()));
  }
}
