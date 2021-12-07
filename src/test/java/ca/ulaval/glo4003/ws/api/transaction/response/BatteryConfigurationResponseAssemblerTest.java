package ca.ulaval.glo4003.ws.api.transaction.response;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BatteryConfigurationResponseAssemblerTest {
  private static final BigDecimal AN_ESTIMATED_RANGE = BigDecimal.valueOf(10.999);

  private BatteryConfigurationResponseAssembler batteryConfigurationResponseAssembler;

  @BeforeEach
  void setUp() {
    batteryConfigurationResponseAssembler = new BatteryConfigurationResponseAssembler();
  }

  @Test
  public void
      givenEstimatedRange_whenAssemble_thenReturnBatteryResponseWithRoundedEstimatedRanged() {
    // given
    BigDecimal expectedRange =
        new BigDecimal(String.valueOf(AN_ESTIMATED_RANGE)).setScale(2, RoundingMode.HALF_UP);

    // when
    BatteryConfigurationResponse response =
        batteryConfigurationResponseAssembler.assemble(AN_ESTIMATED_RANGE);

    // then
    assertThat(response.estimatedRange).isEqualTo(expectedRange);
  }
}
