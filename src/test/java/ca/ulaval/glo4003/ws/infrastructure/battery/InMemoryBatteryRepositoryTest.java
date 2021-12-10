package ca.ulaval.glo4003.ws.infrastructure.battery;

import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.exception.InvalidBatteryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class InMemoryBatteryRepositoryTest {
  private static final String A_BATTERY_NAME = "A BATTERY NAME";
  private static final String INVALID_BATTERY_NAME = "invalid battery name";

  @Mock private BatteryDto aBatteryDto;
  @Mock private Battery aBattery;
  @Mock private BatteryAssembler batteryAssembler;

  private InMemoryBatteryRepository repository;

  @BeforeEach
  public void setUpRepository() {
    Map<String, BatteryDto> batteries = new HashMap<>();
    batteries.put(A_BATTERY_NAME, aBatteryDto);
    repository = new InMemoryBatteryRepository(batteries, batteryAssembler);
  }

  @Test
  public void whenFindByBatteryName_thenReturnBattery() {
    // given
    given(batteryAssembler.assembleBattery(aBatteryDto)).willReturn(aBattery);

    // when
    Battery battery = repository.findByType(A_BATTERY_NAME);

    // then
    assertThat(battery).isEqualTo(aBattery);
  }

  @Test
  public void givenANonExistingBattery_whenFindBatteryByName_thenThrowBatteryNotFoundException() {
    // when
    Executable findingBattery = () -> repository.findByType(INVALID_BATTERY_NAME);

    // then
    assertThrows(InvalidBatteryException.class, findingBattery);
  }

  @Test
  public void givenBatteries_whenFindAllBatteries_thenReturnAllBatteries() {
    // given
    Collection<Battery> expectedBatteries = new ArrayList<>();
    expectedBatteries.add(aBattery);
    given(batteryAssembler.assembleBattery(aBatteryDto)).willReturn(aBattery);

    // when
    Collection<Battery> batteries = repository.findAllBatteries();

    // then
    assertThat(batteries).containsExactlyElementsIn(expectedBatteries);
  }
}
