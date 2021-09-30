package ca.ulaval.glo4003.ws.api.transaction.dto;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.infrastructure.battery.BatteryDto;
import ca.ulaval.glo4003.ws.infrastructure.battery.BatteryDtoAssembler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatteryDtoAssemblerTest {
  private static final String A_TYPE = "type";
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
  void givenBatteryDTO_whenAssembleBattery_thenReturnBattery() {
    // given
    BatteryDto batteryDTO = new BatteryDto();

    batteryDTO.type = A_TYPE;
    batteryDTO.base_NRCAN_range = A_RANGE;
    batteryDTO.capacity = A_CAPACITY;
    batteryDTO.price = A_PRICE;
    batteryDTO.time_to_produce = A_TIME_TO_PRODUCE;

    // when
    Battery battery = batteryDTOAssembler.assembleBattery(batteryDTO);

    // then
    assertThat(battery.getType()).matches(A_TYPE);
    assertThat(String.valueOf(battery.getCapacity())).matches(String.valueOf(A_CAPACITY));
    assertThat(String.valueOf(battery.getBaseNRCANRange())).matches(A_RANGE);
    assertThat(String.valueOf(battery.getPrice())).matches(String.valueOf(A_PRICE));
    assertThat(String.valueOf(battery.getTimeToProduce())).matches(A_TIME_TO_PRODUCE);
  }
}
