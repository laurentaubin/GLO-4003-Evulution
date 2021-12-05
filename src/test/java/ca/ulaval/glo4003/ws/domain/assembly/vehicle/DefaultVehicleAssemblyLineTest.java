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

  private static final AssemblyTime NORMAL = VehicleAssemblyProductionTime.NORMAL.getAssemblyTime();
  private static final AssemblyTime DELAYED =
      VehicleAssemblyProductionTime.DELAYED.getAssemblyTime();

  @Mock private Order anOrder;
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
    given(vehicleAssemblyPlanner.getAssemblyTime(anOrder)).willReturn(DELAYED);

    // when
    vehicleAssemblyLine.assembleVehicle(anOrder);

    // then
    verify(anOrder).setRemainingAssemblyTime(DELAYED);
  }

  @Test
  public void givenOrders_whenAdvance_eachOrderShouldAdvance() {
    // given
    setUpOrders();

    // when
    vehicleAssemblyLine.advance();

    // then
    verify(anOrder).advance();
    verify(anotherOrder).advance();
  }

  @Test
  public void givenOrderFinishedAssembling_whenAdvance_thenShouldRemoveOrder() {
    // given
    given(vehicleAssemblyPlanner.getAssemblyTime(anOrder)).willReturn(DELAYED);
    vehicleAssemblyLine.assembleVehicle(anOrder);
    given(anOrder.isOver()).willReturn(true);

    // when
    vehicleAssemblyLine.advance();

    // then
    assertThat(vehicleAssemblyLine.getActiveOrders()).doesNotContain(anOrder);
  }

  @Test
  public void givenOrder_whenComputeRemainingTimeToProduce_thenReturnRemainingTime() {
    // given
    given(vehicleAssemblyPlanner.getAssemblyTime(anOrder)).willReturn(DELAYED);
    given(anOrder.getRemainingAssemblyTime()).willReturn(DELAYED);
    given(anOrder.getId()).willReturn(AN_ID);

    vehicleAssemblyLine.assembleVehicle(anOrder);

    // when
    AssemblyTime actualAssemblyTime = vehicleAssemblyLine.computeRemainingTimeToProduce(AN_ID);

    // then
    assertThat(actualAssemblyTime).isEqualTo(DELAYED);
  }

  @Test
  public void givenOrdersAssembling_whenShutdown_thenRemoveAllOrders() {
    // given
    vehicleAssemblyLine.assembleVehicle(anOrder);

    // when
    vehicleAssemblyLine.shutdown();

    // then
    assertThat(vehicleAssemblyLine.getActiveOrders()).isEmpty();
  }

  @Test
  public void givenVehicleAssembled_whenAdvance_thenNotifyObservers() {
    // given
    given(vehicleAssemblyPlanner.getAssemblyTime(anOrder)).willReturn(NORMAL);
    vehicleAssemblyLine.assembleVehicle(anOrder);
    given(anOrder.isOver()).willReturn(true);
    vehicleAssemblyLine.register(vehicleAssembledObserver);

    // when
    vehicleAssemblyLine.advance();

    // then
    verify(vehicleAssembledObserver).listenToVehicleAssembled(anOrder);
  }

  private void setUpOrders() {
    given(vehicleAssemblyPlanner.getAssemblyTime(anOrder)).willReturn(DELAYED);
    given(vehicleAssemblyPlanner.getAssemblyTime(anotherOrder)).willReturn(NORMAL);

    vehicleAssemblyLine.assembleVehicle(anOrder);
    vehicleAssemblyLine.assembleVehicle(anotherOrder);
  }
}
