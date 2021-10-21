package ca.ulaval.glo4003.ws.api.transaction.dto;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.infrastructure.battery.BatteryAssembler;
import ca.ulaval.glo4003.ws.infrastructure.battery.BatteryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatteryAssemblerTest {
  private static final String A_TYPE = "TYPE";
  private static final String A_RANGE = "200";
  private static final Integer A_CAPACITY = 200;
  private static final Integer A_PRICE = 200;
  private static final String A_TIME_TO_PRODUCE = "2";

  private BatteryAssembler batteryAssembler;

  @BeforeEach
  void setUp() {
    batteryAssembler = new BatteryAssembler();
  }

  @Test
  void givenABatteryDto_whenAssembleBattery_thenReturnBattery() {
    // given
    BatteryDto batteryDto = new BatteryDto();
    batteryDto.type = A_TYPE;
    batteryDto.base_NRCAN_range = A_RANGE;
    batteryDto.capacity = A_CAPACITY;
    batteryDto.price = A_PRICE;
    batteryDto.time_to_produce = A_TIME_TO_PRODUCE;

    // when
    Battery battery = batteryAssembler.assembleBattery(batteryDto);

    // then
    assertThat(battery.getType()).isEqualTo(A_TYPE);
    assertThat(battery.getCapacity()).isEqualTo(A_CAPACITY);
    assertThat(battery.getBaseNRCANRange()).isEqualTo(Integer.valueOf(A_RANGE));
    assertThat(battery.getProductionTime())
        .isEqualTo(new ProductionTime(Integer.parseInt(A_TIME_TO_PRODUCE)));
    assertThat(battery.getPrice()).isEqualTo(A_PRICE);
  }
}
