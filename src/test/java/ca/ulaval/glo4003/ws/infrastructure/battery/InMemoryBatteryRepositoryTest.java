package ca.ulaval.glo4003.ws.infrastructure.battery;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.exception.InvalidBatteryException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InMemoryBatteryRepositoryTest {
  private static final String A_BATTERY_NAME = "A BATTERY NAME";
  private static final String AN_INVALID_BATTERY_NAME = "invalid battery name";

  @Mock private BatteryDto batteryDto;
  @Mock private Battery battery;
  @Mock private BatteryAssembler batteryAssembler;

  private InMemoryBatteryRepository repository;

  @BeforeEach
  public void setUpRepository() {
    Map<String, BatteryDto> batteries = new HashMap<>();
    batteries.put(A_BATTERY_NAME, batteryDto);
    repository = new InMemoryBatteryRepository(batteries, batteryAssembler);
  }

  @Test
  public void whenFindByBatteryName_thenReturnBattery() {
    // given
    given(batteryAssembler.assembleBattery(batteryDto)).willReturn(battery);

    // when
    Battery battery = repository.findByType(A_BATTERY_NAME);

    // then
    assertThat(battery).isEqualTo(this.battery);
  }

  @Test
  public void givenANonExistingBattery_whenFindBatteryByName_thenThrowBatteryNotFoundException() {
    // when
    Executable findingBattery = () -> repository.findByType(AN_INVALID_BATTERY_NAME);

    // then
    assertThrows(InvalidBatteryException.class, findingBattery);
  }

  @Test
  public void givenBatteries_whenFindAllBatteries_thenReturnAllBatteries() {
    // given
    Collection<Battery> expectedBatteries = new ArrayList<>();
    expectedBatteries.add(battery);
    given(batteryAssembler.assembleBattery(batteryDto)).willReturn(battery);

    // when
    Collection<Battery> batteries = repository.findAllBatteries();

    // then
    assertThat(batteries).containsExactlyElementsIn(expectedBatteries);
  }
}
