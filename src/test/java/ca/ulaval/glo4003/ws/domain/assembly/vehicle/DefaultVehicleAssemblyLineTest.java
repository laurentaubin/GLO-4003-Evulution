package ca.ulaval.glo4003.ws.domain.assembly.vehicle;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultVehicleAssemblyLineTest {
  private static final OrderId AN_ID = new OrderId("id");

  private static final VehicleAssemblyProductionTime NORMAL = VehicleAssemblyProductionTime.NORMAL;
  private static final VehicleAssemblyProductionTime DELAYED =
      VehicleAssemblyProductionTime.DELAYED;

  @Mock private Order anOrder;
  @Mock private Order anotherOrder;
  @Mock private VehicleAssemblyPlanner vehicleAssemblyPlanner;

  private DefaultVehicleAssemblyLine vehicleAssemblyLine;

  @BeforeEach
  void setUp() {
    vehicleAssemblyLine = new DefaultVehicleAssemblyLine(vehicleAssemblyPlanner);
  }

  @Test
  public void givenOrder_whenAssemblingVehicle_thenShouldSetRemainingTimeToOrder() {
    // given
    when(vehicleAssemblyPlanner.getProductionTime(anOrder)).thenReturn(DELAYED);

    // when
    vehicleAssemblyLine.assembleVehicle(anOrder);

    // then
    verify(anOrder).setRemainingProductionTime(DELAYED.getProductionTime());
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
    when(vehicleAssemblyPlanner.getProductionTime(anOrder)).thenReturn(DELAYED);
    vehicleAssemblyLine.assembleVehicle(anOrder);
    when(anOrder.getRemainingProductionTime()).thenReturn(0);

    // when
    vehicleAssemblyLine.advance();

    // then
    assertThat(vehicleAssemblyLine.getCurrentOrders()).doesNotContain(anOrder);
  }

  @Test
  public void givenOrder_whenComputeRemainingTimeToProduce_thenReturnRemainingTime() {
    // given
    when(vehicleAssemblyPlanner.getProductionTime(anOrder)).thenReturn(DELAYED);
    when(anOrder.getRemainingProductionTime()).thenReturn(DELAYED.getProductionTime());
    when(anOrder.getId()).thenReturn(AN_ID);

    vehicleAssemblyLine.assembleVehicle(anOrder);

    // when
    var productionTime = vehicleAssemblyLine.computeRemainingTimeToProduce(AN_ID);

    // then
    assertThat(productionTime).isEqualTo(DELAYED.getProductionTime());
  }

  private void setUpOrders() {
    when(vehicleAssemblyPlanner.getProductionTime(anOrder)).thenReturn(DELAYED);
    when(vehicleAssemblyPlanner.getProductionTime(anotherOrder)).thenReturn(NORMAL);

    vehicleAssemblyLine.assembleVehicle(anOrder);
    vehicleAssemblyLine.assembleVehicle(anotherOrder);
  }
}
