package ca.ulaval.glo4003.ws.domain.transaction;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4003.ws.domain.battery.Battery;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleTest {
  private static final Integer A_BATTERY_RANGE = 100;
  private static final int FIFTY_PERCENT = 50;
  private static final int HUNDRED_PERCENT = 100;
  private static final Color A_COLOR = Color.WHITE;

  @Mock private Model aModel;
  @Mock private Battery aBattery;

  private Vehicle vehicle;

  @BeforeEach
  public void setUp() {
    vehicle = new Vehicle(aModel, A_COLOR);
  }

  @Test
  public void givenModelWithFiftyPercentEfficiency_whenComputeRange_thenReturnHalfBatteryRange() {
    // given
    when(aModel.getEfficiency()).thenReturn(BigDecimal.valueOf(FIFTY_PERCENT));
    when(aBattery.getBaseNRCANRange()).thenReturn(A_BATTERY_RANGE);
    vehicle.addBattery(aBattery);
    BigDecimal expectedComputedRange = BigDecimal.valueOf(50);

    // when
    BigDecimal batteryRange = vehicle.computeRange();
    // then
    assertThat(batteryRange).isEqualTo(expectedComputedRange);
  }

  @Test
  public void givenHundredPercentEfficiency_whenComputeRange_thenReturnFullBatteryRange() {
    // given
    when(aModel.getEfficiency()).thenReturn(BigDecimal.valueOf(HUNDRED_PERCENT));
    when(aBattery.getBaseNRCANRange()).thenReturn(A_BATTERY_RANGE);
    vehicle.addBattery(aBattery);
    BigDecimal expected = BigDecimal.valueOf(A_BATTERY_RANGE);

    // when
    BigDecimal batteryRange = vehicle.computeRange();

    // then
    assertThat(batteryRange).isEqualTo(expected);
  }

  @Test
  public void givenANewVehicleWithNoBattery_whenHasBattery_thenReturnFalse() {
    // given
    Vehicle vehicle = new Vehicle(aModel, A_COLOR);

    // when
    boolean hasBattery = vehicle.hasBattery();

    // then
    assertThat(hasBattery).isFalse();
  }

  @Test
  public void givenAVehicleWithBattery_whenHasBattery_thenReturnTrue() {
    // given
    Vehicle vehicle = new Vehicle(aModel, A_COLOR);
    vehicle.addBattery(aBattery);

    // when
    boolean hasBattery = vehicle.hasBattery();

    // then
    assertThat(hasBattery).isTrue();
  }
}
