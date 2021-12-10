package ca.ulaval.glo4003.ws.domain.warehouse.vehicle;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.manufacturer.vehicle.VehicleAssemblyProductionTime;
import ca.ulaval.glo4003.ws.domain.manufacturer.vehicle.VehicleManufacturer;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import ca.ulaval.glo4003.ws.domain.warehouse.vehicle.strategy.DefaultVehicleWarehouseStrategy;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultVehicleWarehouseStrategyTest {
  private static final OrderId AN_ID = new OrderId("id");
  private static final AssemblyTime DELAYED =
      VehicleAssemblyProductionTime.DELAYED.getAssemblyTime();

  @Mock private Order anOrder;
  @Mock private VehicleManufacturer vehicleManufacturer;
  @Mock private VehicleInventoryObserver vehicleInventoryObserver;

  private DefaultVehicleWarehouseStrategy vehicleWarehouse;

  @BeforeEach
  void setUp() {
    vehicleWarehouse = new DefaultVehicleWarehouseStrategy(vehicleManufacturer);
    vehicleWarehouse.register(vehicleInventoryObserver);
  }

  @Test
  public void
      givenOrder_whenComputeRemainingTimeToProduce_thenReturnRemainingTimeComputeFromManufacturer() {
    // given
    given(vehicleManufacturer.computeRemainingTimeToProduce(AN_ID)).willReturn(DELAYED);

    // when
    AssemblyTime actualAssemblyTime = vehicleWarehouse.computeRemainingTimeToProduce(AN_ID);

    // then
    assertThat(actualAssemblyTime).isEqualTo(DELAYED);
  }

  @Test
  public void givenOrdersAssembling_whenCancelAllOrders_thenRemoveAllOrders() {
    // given
    vehicleWarehouse.addOrder(anOrder);
    vehicleWarehouse.cancelAllOrders();

    // when
    List<Order> cancelledOrders = vehicleWarehouse.cancelAllOrders();

    // then
    assertThat(cancelledOrders).isEmpty();
  }

  @Test
  public void whenListenToVehicleAssembled_thenNotifyVehicleInStock() {
    // when
    vehicleWarehouse.listenToVehicleAssembled(anOrder);

    // then
    verify(vehicleInventoryObserver).listenToVehicleInStock(anOrder);
  }
}
