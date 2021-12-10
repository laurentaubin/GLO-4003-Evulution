package ca.ulaval.glo4003.ws.domain.manufacturer.vehicle;

import ca.ulaval.glo4003.ws.domain.notification.VehicleOrderDelayObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import ca.ulaval.glo4003.ws.fixture.OrderFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VehicleManufacturerImplTest {
  private static final OrderId ORDER_ID = new OrderId("id");
  private static final Order ORDER = new OrderFixture().withOrderId(ORDER_ID).build();

  @Mock private VehicleAssemblyPlanner vehicleAssemblyPlanner;
  @Mock private VehicleOrderDelayObserver vehicleOrderDelayObserver;
  @Mock private VehicleAssembledObserver vehicleAssembledObserver;
  @Mock private Order order;

  private VehicleManufacturerImpl vehicleManufacturer;

  @BeforeEach
  public void setUp() {
    vehicleManufacturer = new VehicleManufacturerImpl(vehicleAssemblyPlanner);
    vehicleManufacturer.register(vehicleOrderDelayObserver);
    vehicleManufacturer.register(vehicleAssembledObserver);
  }

  @Test
  public void givenADelay_whenAddOrder_thenNotifyVehicleOrderDelay() {
    // given
    given(vehicleAssemblyPlanner.getAssemblyTime(ORDER))
        .willReturn(VehicleAssemblyProductionTime.DELAYED.getAssemblyTime());

    // when
    vehicleManufacturer.addOrder(ORDER);

    // then
    verify(vehicleOrderDelayObserver).listenVehicleOrderDelay(ORDER);
  }

  @Test
  public void givenOrder_whenAddOrder_thenShouldSetRemainingTimeToOrder() {
    // given
    given(vehicleAssemblyPlanner.getAssemblyTime(order))
        .willReturn(VehicleAssemblyProductionTime.NORMAL.getAssemblyTime());

    // when
    vehicleManufacturer.addOrder(order);

    // then
    verify(order).setRemainingAssemblyTime(VehicleAssemblyProductionTime.NORMAL.getAssemblyTime());
  }

  @Test
  public void givenVehicleAssembled_whenAdvanceTime_thenNotifyVehicleOrderIsAssembled() {
    // given
    Order order = new OrderFixture().withOrderId(ORDER_ID).build();
    given(vehicleAssemblyPlanner.getAssemblyTime(order))
        .willReturn(VehicleAssemblyProductionTime.NORMAL.getAssemblyTime());
    vehicleManufacturer.addOrder(order);

    // when
    vehicleManufacturer.advanceTime();

    // then
    verify(vehicleAssembledObserver).listenToVehicleAssembled(order);
  }

  @Test
  public void
      givenVehicleAssembled_whenAdvanceTimeMultipleTimes_thenNotifyVehicleOrderIsAssembledOnlyOnce() {
    // given
    Order order = new OrderFixture().withOrderId(ORDER_ID).build();
    given(vehicleAssemblyPlanner.getAssemblyTime(order))
        .willReturn(VehicleAssemblyProductionTime.NORMAL.getAssemblyTime());
    vehicleManufacturer.addOrder(order);

    // when
    vehicleManufacturer.advanceTime();
    vehicleManufacturer.advanceTime();
    vehicleManufacturer.advanceTime();

    // then
    verify(vehicleAssembledObserver, new Times(1)).listenToVehicleAssembled(order);
  }

  @Test
  public void givenAnOrderThatShouldAssembledNextWeek_whenStop_thenOrderIsNeverAssembled() {
    // given
    Order order = new OrderFixture().withOrderId(ORDER_ID).build();
    given(vehicleAssemblyPlanner.getAssemblyTime(order))
        .willReturn(VehicleAssemblyProductionTime.NORMAL.getAssemblyTime());
    vehicleManufacturer.addOrder(order);

    // when
    vehicleManufacturer.stop();
    vehicleManufacturer.advanceTime();

    // then
    verify(vehicleAssembledObserver, new Times(0)).listenToVehicleAssembled(order);
  }

  @Test
  public void givenAnOrder_whenComputeRemainingTimeToProduce_thenOrderRemainingTimeToProduce() {
    // given
    Order order = new OrderFixture().withOrderId(ORDER_ID).build();
    given(vehicleAssemblyPlanner.getAssemblyTime(order))
        .willReturn(VehicleAssemblyProductionTime.DELAYED.getAssemblyTime());
    vehicleManufacturer.addOrder(order);

    // when
    AssemblyTime assemblyTime = vehicleManufacturer.computeRemainingTimeToProduce(ORDER_ID);

    // then
    assertThat(assemblyTime).isEqualTo(order.getRemainingAssemblyTime());
  }

  @Test
  public void whenNotifyVehicleAssembled_thenNotifyObservers() {
    // when
    vehicleManufacturer.notifyVehicleAssembled(ORDER);

    // then
    verify(vehicleAssembledObserver).listenToVehicleAssembled(ORDER);
  }
}
