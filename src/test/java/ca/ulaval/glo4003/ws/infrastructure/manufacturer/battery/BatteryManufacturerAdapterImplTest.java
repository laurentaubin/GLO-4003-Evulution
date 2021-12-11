package ca.ulaval.glo4003.ws.infrastructure.manufacturer.battery;

import ca.ulaval.glo4003.evulution.car_manufacture.BatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.ws.domain.warehouse.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.fixture.BatteryOrderBuilder;
import ca.ulaval.glo4003.ws.infrastructure.warehouse.CommandIdFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BatteryManufacturerAdapterImplTest {
  @Mock private OrderId orderId;
  @Mock private CommandID commandId;
  @Mock private BatteryAssemblyLine batteryAssemblyLine;
  @Mock private CommandIdFactory commandIdFactory;

  private BatteryAssemblyLineAdapterImpl batteryAssemblyLineAdapter;

  @BeforeEach
  public void setUp() {
    batteryAssemblyLineAdapter =
        new BatteryAssemblyLineAdapterImpl(batteryAssemblyLine, commandIdFactory);
  }

  @Test
  public void givenABatteryOrder_whenAddOrder_thenCommandIdCreatedFromOrderId() {
    // given
    BatteryOrder batteryOrder = new BatteryOrderBuilder().build();

    // when
    batteryAssemblyLineAdapter.addOrder(batteryOrder);

    // then
    verify(commandIdFactory).getOrCreateFromOrderId(batteryOrder.getOrderId());
  }

  @Test
  public void givenABatteryOrder_whenAddOrder_thenSendBatteryOrderToAssemblyLine() {
    // given
    BatteryOrder batteryOrder = new BatteryOrderBuilder().build();
    given(commandIdFactory.getOrCreateFromOrderId(any())).willReturn(commandId);

    // when
    batteryAssemblyLineAdapter.addOrder(batteryOrder);

    // then
    verify(batteryAssemblyLine).newBatteryCommand(commandId, batteryOrder.getBatteryType());
  }

  @Test
  public void givenAnAssembledOrder_whenGetAssemblyStatus_thenReturnTheOrderAssemblyStatus() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(orderId)).willReturn(commandId);
    given(batteryAssemblyLine.getBuildStatus(commandId)).willReturn(BuildStatus.ASSEMBLED);

    // when
    AssemblyStatus assemblyStatus = batteryAssemblyLineAdapter.getAssemblyStatus(orderId);

    // then
    assertThat(assemblyStatus).isEquivalentAccordingToCompareTo(AssemblyStatus.ASSEMBLED);
  }

  @Test
  public void givenAnInProgressOrder_whenGetAssemblyStatus_thenReturnTheOrderAssemblyStatus() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(orderId)).willReturn(commandId);
    given(batteryAssemblyLine.getBuildStatus(commandId)).willReturn(BuildStatus.IN_PROGRESS);

    // when
    AssemblyStatus assemblyStatus = batteryAssemblyLineAdapter.getAssemblyStatus(orderId);

    // then
    assertThat(assemblyStatus).isEquivalentAccordingToCompareTo(AssemblyStatus.IN_PROGRESS);
  }

  @Test
  public void givenAReceivedOrder_whenGetAssemblyStatus_thenReturnTheOrderAssemblyStatus() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(orderId)).willReturn(commandId);
    given(batteryAssemblyLine.getBuildStatus(commandId)).willReturn(BuildStatus.RECEIVED);

    // when
    AssemblyStatus assemblyStatus = batteryAssemblyLineAdapter.getAssemblyStatus(orderId);

    // then
    assertThat(assemblyStatus).isEquivalentAccordingToCompareTo(AssemblyStatus.RECEIVED);
  }

  @Test
  public void whenAdvance_thenBatteryAssemblyLineIsAdvanced() {
    // when
    batteryAssemblyLineAdapter.advance();

    // then
    verify(batteryAssemblyLine).advance();
  }

  @Test
  public void givenInvalidOrderId_whenGetAssemblyStatus_thenReturnTheOrderDoesNotExist() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(orderId)).willReturn(commandId);
    doThrow(NullPointerException.class).when(batteryAssemblyLine).getBuildStatus(commandId);

    // when
    AssemblyStatus assemblyStatus = batteryAssemblyLineAdapter.getAssemblyStatus(orderId);

    // then
    assertThat(assemblyStatus).isEquivalentAccordingToCompareTo(AssemblyStatus.DOES_NOT_EXIST);
  }
}
