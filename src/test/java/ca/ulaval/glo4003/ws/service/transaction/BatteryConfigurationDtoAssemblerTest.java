package ca.ulaval.glo4003.ws.service.transaction;

import ca.ulaval.glo4003.ws.service.transaction.dto.BatteryConfigurationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.google.common.truth.Truth.assertThat;

class BatteryConfigurationDtoAssemblerTest {
  private static final BigDecimal ESTIMATED_RANGE = BigDecimal.valueOf(4324);

  private BatteryConfigurationDtoAssembler batteryConfigurationDtoAssembler;

  @BeforeEach
  public void setUp() {
    batteryConfigurationDtoAssembler = new BatteryConfigurationDtoAssembler();
  }

  @Test
  public void givenAnEstimatedRange_whenAssemble_thenBatteryConfigurationDtoIsCorrectlyAssembled() {
    // when
    BatteryConfigurationDto batteryConfigurationDto =
        batteryConfigurationDtoAssembler.assemble(ESTIMATED_RANGE);

    // then
    assertThat(batteryConfigurationDto.getEstimatedRange()).isEqualTo(ESTIMATED_RANGE);
  }
}
