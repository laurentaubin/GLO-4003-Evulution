package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.transaction.response.BatteryConfigurationResponseAssembler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.google.common.truth.Truth.assertThat;

class BatteryResponseAssemblerTest {
  private static final double A_NUMBER = 10.999;

  private BatteryConfigurationResponseAssembler batteryResponseAssembler;

  @BeforeEach
  void setUp() {
    batteryResponseAssembler = new BatteryConfigurationResponseAssembler();
  }

  @Test
  public void
      givenEstimatedRange_whenAssemble_thenReturnBatteryResponseWithRoundedEstimatedRanged() {
    // given
    var estimatedRange = BigDecimal.valueOf(A_NUMBER);
    var expectedRange = estimatedRange.setScale(2, RoundingMode.HALF_UP);

    // when
    var result = batteryResponseAssembler.assemble(estimatedRange);

    // then
    assertThat(result.estimatedRange).isEqualTo(expectedRange);
  }
}
