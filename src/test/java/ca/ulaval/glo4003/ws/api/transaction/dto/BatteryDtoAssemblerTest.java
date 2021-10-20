package ca.ulaval.glo4003.ws.api.transaction.dto;

import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.infrastructure.battery.BatteryDto;
import ca.ulaval.glo4003.ws.infrastructure.battery.BatteryDtoAssembler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@ExtendWith(MockitoExtension.class)
class BatteryDtoAssemblerTest {
  private static final String A_TYPE = "TYPE";
  private static final String A_RANGE = "200";
  private static final Integer A_CAPACITY = 200;
  private static final Integer A_PRICE = 200;
  private static final String A_TIME_TO_PRODUCE = "2";

  private BatteryDtoAssembler batteryDTOAssembler;

  @BeforeEach
  void setUp() {
    batteryDTOAssembler = new BatteryDtoAssembler();
  }

  @Test
  void givenBatteryDtos_whenAssembleBatteries_thenReturnBatteries() {
    // given
    BatteryDto batteryDto = new BatteryDto();
    batteryDto.type = A_TYPE;
    batteryDto.base_NRCAN_range = A_RANGE;
    batteryDto.capacity = A_CAPACITY;
    batteryDto.price = A_PRICE;
    batteryDto.time_to_produce = A_TIME_TO_PRODUCE;

    // when
    List<Battery> batteries =
        batteryDTOAssembler.assembleBatteries(new ArrayList<>(List.of(batteryDto)));

    // then
    Battery assembledBattery = batteries.get(0);
    assertThat(assembledBattery.getType()).isEqualTo(A_TYPE);
    assertThat(assembledBattery.getCapacity()).isEqualTo(A_CAPACITY);
    assertThat(assembledBattery.getBaseNRCANRange()).isEqualTo(Integer.valueOf(A_RANGE));
    assertThat(assembledBattery.getProductionTime())
        .isEqualTo(new ProductionTime(Integer.parseInt(A_TIME_TO_PRODUCE)));
    assertThat(assembledBattery.getPrice()).isEqualTo(A_PRICE);
  }
}
