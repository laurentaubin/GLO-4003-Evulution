package ca.ulaval.glo4003.ws.infrastructure.assembly.battery;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.evulution.car_manufacture.BatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.infrastructure.assembly.CommandIdFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatteryAssemblyLineAdapterTest {
  private static final String A_BATTERY_TYPE = "battery type";

  @Mock private OrderId anOrderId;
  @Mock private Order anOrder;
  @Mock private Battery aBattery;
  @Mock private CommandID aCommandId;
  @Mock private BatteryAssemblyLine batteryAssemblyLine;
  @Mock private CommandIdFactory commandIdFactory;

  private BatteryAssemblyLineAdapter batteryAssemblyLineAdapter;

  @BeforeEach
  public void setUp() {
    batteryAssemblyLineAdapter =
        new BatteryAssemblyLineAdapter(batteryAssemblyLine, commandIdFactory);
  }

  @Test
  public void givenAnAssembledOrder_whenGetAssemblyStatus_thenReturnTheOrderAssemblyStatus() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(anOrderId)).willReturn(aCommandId);
    given(batteryAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.ASSEMBLED);

    // when
    AssemblyStatus assemblyStatus = batteryAssemblyLineAdapter.getAssemblyStatus(anOrderId);

    // then
    assertThat(assemblyStatus).isEquivalentAccordingToCompareTo(AssemblyStatus.ASSEMBLED);
  }

  @Test
  public void givenAnInProgressOrder_whenGetAssemblyStatus_thenReturnTheOrderAssemblyStatus() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(anOrderId)).willReturn(aCommandId);
    given(batteryAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.IN_PROGRESS);

    // when
    AssemblyStatus assemblyStatus = batteryAssemblyLineAdapter.getAssemblyStatus(anOrderId);

    // then
    assertThat(assemblyStatus).isEquivalentAccordingToCompareTo(AssemblyStatus.IN_PROGRESS);
  }

  @Test
  public void givenAReceivedOrder_whenGetAssemblyStatus_thenReturnTheOrderAssemblyStatus() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(anOrderId)).willReturn(aCommandId);
    given(batteryAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.RECEIVED);

    // when
    AssemblyStatus assemblyStatus = batteryAssemblyLineAdapter.getAssemblyStatus(anOrderId);

    // then
    assertThat(assemblyStatus).isEquivalentAccordingToCompareTo(AssemblyStatus.RECEIVED);
  }

  @Test
  public void givenAnOrder_whenAddOrder_thenOrderIsSentToBeAssembled() {
    // given
    given(anOrder.getId()).willReturn(anOrderId);
    given(commandIdFactory.getOrCreateFromOrderId(anOrderId)).willReturn(aCommandId);
    given(anOrder.getBattery()).willReturn(aBattery);
    given(aBattery.getType()).willReturn(A_BATTERY_TYPE);

    // when
    batteryAssemblyLineAdapter.addOrder(anOrder);

    // then
    verify(batteryAssemblyLine).newBatteryCommand(aCommandId, A_BATTERY_TYPE);
  }

  @Test
  public void whenAdvance_thenBatteryAssemblyLineIsAdvanced() {
    // when
    batteryAssemblyLineAdapter.advance();

    // then
    verify(batteryAssemblyLine).advance();
  }
}
