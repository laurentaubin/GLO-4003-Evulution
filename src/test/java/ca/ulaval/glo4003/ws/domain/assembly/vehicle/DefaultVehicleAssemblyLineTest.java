package ca.ulaval.glo4003.ws.domain.assembly.vehicle;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.time.AssemblyTime;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.strategy.DefaultVehicleAssemblyLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultVehicleAssemblyLineTest {
  private static final OrderId AN_ID = new OrderId("id");

  private static final AssemblyTime A_NORMAL_ASSEMBLY_TIME = VehicleAssemblyProductionTime.NORMAL.getAssemblyTime();
  private static final AssemblyTime A_DELAYED_ASSEMBLY_TIME =
      VehicleAssemblyProductionTime.DELAYED.getAssemblyTime();

  @Mock private Order order;
  @Mock private Order anotherOrder;
  @Mock private VehicleAssemblyPlanner vehicleAssemblyPlanner;
  @Mock private VehicleAssembledObserver vehicleAssembledObserver;

  private DefaultVehicleAssemblyLine vehicleAssemblyLine;

  @BeforeEach
  void setUp() {
    vehicleAssemblyLine = new DefaultVehicleAssemblyLine(vehicleAssemblyPlanner);
  }

  @Test
  public void givenOrder_whenAssemblingVehicle_thenShouldSetRemainingTimeToOrder() {
    // given
    given(vehicleAssemblyPlanner.getAssemblyTime(order)).willReturn(A_DELAYED_ASSEMBLY_TIME);

    // when
    vehicleAssemblyLine.assembleVehicle(order);

    // then
    verify(order).setRemainingAssemblyTime(A_DELAYED_ASSEMBLY_TIME);
  }

  @Test
  public void givenOrders_whenAdvance_eachOrderShouldAdvance() {
    // given
    setUpOrders();

    // when
    vehicleAssemblyLine.advance();

    // then
    verify(order).advance();
    verify(anotherOrder).advance();
  }

  @Test
  public void givenOrderFinishedAssembling_whenAdvance_thenShouldRemoveOrder() {
    // given
    given(vehicleAssemblyPlanner.getAssemblyTime(order)).willReturn(A_DELAYED_ASSEMBLY_TIME);
    vehicleAssemblyLine.assembleVehicle(order);
    given(order.isOver()).willReturn(true);

    // when
    vehicleAssemblyLine.advance();

    // then
    assertThat(vehicleAssemblyLine.getActiveOrders()).doesNotContain(order);
  }

  @Test
  public void givenOrder_whenComputeRemainingTimeToProduce_thenReturnRemainingTime() {
    // given
    given(vehicleAssemblyPlanner.getAssemblyTime(order)).willReturn(A_DELAYED_ASSEMBLY_TIME);
    given(order.getRemainingAssemblyTime()).willReturn(A_DELAYED_ASSEMBLY_TIME);
    given(order.getId()).willReturn(AN_ID);

    vehicleAssemblyLine.assembleVehicle(order);

    // when
    AssemblyTime actualAssemblyTime = vehicleAssemblyLine.computeRemainingTimeToProduce(AN_ID);

    // then
    assertThat(actualAssemblyTime).isEqualTo(A_DELAYED_ASSEMBLY_TIME);
  }

  @Test
  public void givenOrdersAssembling_whenShutdown_thenRemoveAllOrders() {
    // given
    vehicleAssemblyLine.assembleVehicle(order);

    // when
    vehicleAssemblyLine.shutdown();

    // then
    assertThat(vehicleAssemblyLine.getActiveOrders()).isEmpty();
  }

  @Test
  public void givenVehicleAssembled_whenAdvance_thenNotifyObservers() {
    // given
    given(vehicleAssemblyPlanner.getAssemblyTime(order)).willReturn(A_NORMAL_ASSEMBLY_TIME);
    vehicleAssemblyLine.assembleVehicle(order);
    given(order.isOver()).willReturn(true);
    vehicleAssemblyLine.register(vehicleAssembledObserver);

    // when
    vehicleAssemblyLine.advance();

    // then
    verify(vehicleAssembledObserver).listenToVehicleAssembled(order);
  }

  private void setUpOrders() {
    given(vehicleAssemblyPlanner.getAssemblyTime(order)).willReturn(A_DELAYED_ASSEMBLY_TIME);
    given(vehicleAssemblyPlanner.getAssemblyTime(anotherOrder)).willReturn(A_NORMAL_ASSEMBLY_TIME);

    vehicleAssemblyLine.assembleVehicle(order);
    vehicleAssemblyLine.assembleVehicle(anotherOrder);
  }
}
