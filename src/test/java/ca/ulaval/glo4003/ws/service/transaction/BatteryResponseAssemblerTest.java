package ca.ulaval.glo4003.ws.service.transaction;

import static com.google.common.truth.Truth.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BatteryResponseAssemblerTest {
  private static final double A_NUMBER = 10.999;
  private static final double ROUNDED_NUMBER = 11.00;

  private BatteryResponseAssembler batteryResponseAssembler;

  @BeforeEach
  void setUp() {
    batteryResponseAssembler = new BatteryResponseAssembler();
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
