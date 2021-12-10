package ca.ulaval.glo4003.ws.domain.warehouse.battery;

import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class BatteryOrderFactoryTest {
  private static final String BATTERY_TYPE = "batteryType";
  private static final AssemblyTime ASSEMBLY_TIME = new AssemblyTime(1);

  private BatteryOrderFactory batteryOrderFactory;

  @BeforeEach
  public void setUp() {
    batteryOrderFactory = new BatteryOrderFactory();
  }

  @Test
  public void givenABatteryTypeAndAnAssemblyTime_whenCreate_thenBatteryOrderIsCreatedCorrectly() {
    // given

    // when
    BatteryOrder batteryOrder = batteryOrderFactory.create(BATTERY_TYPE, ASSEMBLY_TIME);

    // then
    assertThat(batteryOrder.getOrderId()).isNotNull();
    assertThat(batteryOrder.getBatteryType()).isEqualTo(BATTERY_TYPE);
    assertThat(batteryOrder.getAssemblyTime()).isEqualTo(ASSEMBLY_TIME);
  }
}
