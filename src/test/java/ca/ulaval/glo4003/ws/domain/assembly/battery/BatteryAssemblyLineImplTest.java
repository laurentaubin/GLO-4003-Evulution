package ca.ulaval.glo4003.ws.domain.assembly.battery;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatteryAssemblyLineImplTest {
  @Test
  public void whenCreate_thenBatteryAssemblyLineImplIsNotNull() {
    // when
    LinearBatteryAssemblyLine batteryAssemblyLine = new LinearBatteryAssemblyLine();

    // then
    assertThat(batteryAssemblyLine).isNotNull();
  }
}
