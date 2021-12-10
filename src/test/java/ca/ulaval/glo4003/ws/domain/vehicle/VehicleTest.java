package ca.ulaval.glo4003.ws.domain.vehicle;

import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.exception.IncompleteVehicleException;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class VehicleTest {
  private static final Integer A_BATTERY_RANGE = 100;
  private static final int A_FIFTY_PERCENT = 50;
  private static final int A_HUNDRED_PERCENT = 100;
  private static final Color A_COLOR = Color.WHITE;
  private static final Price A_BATTERY_PRICE = new Price(500);
  private static final Price A_MODEL_PRICE = new Price(3000);

  @Mock private Model model;
  @Mock private Battery battery;

  private Vehicle vehicle;

  @BeforeEach
  public void setUp() {
    vehicle = new Vehicle(model, A_COLOR);
  }

  @Test
  public void givenModelWithFiftyPercentEfficiency_whenComputeRange_thenReturnHalfBatteryRange() {
    // given
    given(model.getEfficiency()).willReturn(BigDecimal.valueOf(A_FIFTY_PERCENT));
    given(battery.getBaseNRCANRange()).willReturn(A_BATTERY_RANGE);
    vehicle.addBattery(battery);
    BigDecimal expectedComputedRange = BigDecimal.valueOf(50);

    // when
    BigDecimal batteryRange = vehicle.computeRange();
    // then
    assertThat(batteryRange).isEqualTo(expectedComputedRange);
  }

  @Test
  public void givenHundredPercentEfficiency_whenComputeRange_thenReturnFullBatteryRange() {
    // given
    given(model.getEfficiency()).willReturn(BigDecimal.valueOf(A_HUNDRED_PERCENT));
    given(battery.getBaseNRCANRange()).willReturn(A_BATTERY_RANGE);
    vehicle.addBattery(battery);
    BigDecimal expected = BigDecimal.valueOf(A_BATTERY_RANGE);

    // when
    BigDecimal batteryRange = vehicle.computeRange();

    // then
    assertThat(batteryRange).isEqualTo(expected);
  }

  @Test
  public void givenANewVehicleWithNoBattery_whenHasBattery_thenReturnFalse() {
    // given
    Vehicle vehicle = new Vehicle(model, A_COLOR);

    // when
    boolean hasBattery = vehicle.hasBattery();

    // then
    assertThat(hasBattery).isFalse();
  }

  @Test
  public void givenAVehicleWithBattery_whenHasBattery_thenReturnTrue() {
    // given
    Vehicle vehicle = new Vehicle(model, A_COLOR);
    vehicle.addBattery(battery);

    // when
    boolean hasBattery = vehicle.hasBattery();

    // then
    assertThat(hasBattery).isTrue();
  }

  @Test
  public void givenAValidVehicle_whenCalculatePrice_thenReturnPrice() {
    // given
    given(model.getPrice()).willReturn(A_MODEL_PRICE);
    given(battery.getPrice()).willReturn(A_BATTERY_PRICE);
    Vehicle vehicle = new Vehicle(model, A_COLOR);
    vehicle.addBattery(battery);

    // when
    int actualPrice = vehicle.getVehiclePrice().toInt();

    // then
    assertThat(actualPrice).isEqualTo(model.getPrice().toInt() + battery.getPrice().toInt());
  }

  @Test
  public void givenAnIncompleteVehicle_whenCalculatePrice_thenThrowIncompleteVehicleException() {
    // given
    Vehicle vehicle = new Vehicle(model, A_COLOR);

    // when
    Executable getVehiclePrice = vehicle::getVehiclePrice;

    // then
    assertThrows(IncompleteVehicleException.class, getVehiclePrice);
  }
}
