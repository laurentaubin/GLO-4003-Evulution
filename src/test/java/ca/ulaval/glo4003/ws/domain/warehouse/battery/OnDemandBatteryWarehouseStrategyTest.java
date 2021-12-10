package ca.ulaval.glo4003.ws.domain.warehouse.battery;

import ca.ulaval.glo4003.ws.domain.manufacturer.battery.BatteryManufacturer;
import ca.ulaval.glo4003.ws.domain.notification.BatteryOrderDelayObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.strategy.OnDemandBatteryWarehouseStrategy;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import ca.ulaval.glo4003.ws.fixture.BatteryOrderBuilder;
import ca.ulaval.glo4003.ws.fixture.OrderBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OnDemandBatteryWarehouseStrategyTest {
  private static final String AN_ID = "fdsnj9203";
  @Mock private BatteryManufacturer batteryManufacturer;
  @Mock private BatteryInventoryObserver batteryAssembledObserver;
  @Mock private BatteryOrderDelayObserver batteryOrderDelayObserver;

  private OnDemandBatteryWarehouseStrategy onDemandBatteryWarehouseStrategy;

  @BeforeEach
  public void setUp() {
    onDemandBatteryWarehouseStrategy = new OnDemandBatteryWarehouseStrategy(batteryManufacturer);
    onDemandBatteryWarehouseStrategy.register(batteryAssembledObserver);
    onDemandBatteryWarehouseStrategy.register(batteryOrderDelayObserver);
  }

  @Test
  public void givenAnOrder_whenAddOrder_thenOrderIsSentToBeAssembled() {
    // given
    Order order = createOrder(new OrderId(AN_ID));

    // when
    onDemandBatteryWarehouseStrategy.addOrder(order);

    // then
    verify(batteryManufacturer).addOrder(order.getBatteryOrder());
  }

  @Test
  public void
      givenAnOrderSentToBeAssembled_whenComputeEstimatedTime_thenReturnTheBatteryRemainingTimeToProduce() {
    // given
    Order order = createOrder(new OrderId(AN_ID));
    onDemandBatteryWarehouseStrategy.addOrder(order);

    // when
    AssemblyTime delay =
        onDemandBatteryWarehouseStrategy.computeRemainingTimeToProduce(order.getId());

    // then
    assertThat(delay.inWeeks()).isEqualTo(0);
  }

  @Test
  public void
      givenBatteryOrderAssembled_whenListenToBatteryAssembled_thenObserversNotifyBatteryInventoryObserver() {
    // given
    BatteryOrder batteryOrder = new BatteryOrderBuilder().build();
    Order order = new OrderBuilder().withBatteryOrder(batteryOrder).build();
    onDemandBatteryWarehouseStrategy.addOrder(order);

    // when
    onDemandBatteryWarehouseStrategy.listenToBatteryAssembled(batteryOrder);

    // then
    verify(batteryAssembledObserver).listenToBatteryInStock(order);
  }

  @Test
  public void givenNoQueuedOrder_whenAddOrder_thenDoNotNotifyAssemblyDelay() {
    // given
    BatteryOrder batteryOrder = new BatteryOrderBuilder().build();
    Order order = new OrderBuilder().withBatteryOrder(batteryOrder).build();

    // when
    onDemandBatteryWarehouseStrategy.addOrder(order);

    // then
    verify(batteryOrderDelayObserver, never()).listenBatteryOrderDelay(any());
  }

  @Test
  public void givenQueuedOrders_whenAddOrder_thenNotifyAssemblyDelayForAddedOrder() {
    // given
    BatteryOrder batteryOrder = new BatteryOrderBuilder().build();
    Order order = new OrderBuilder().withBatteryOrder(batteryOrder).build();
    BatteryOrder anotherBatteryOrder = new BatteryOrderBuilder().build();
    Order anotherOrder = new OrderBuilder().withBatteryOrder(anotherBatteryOrder).build();
    given(
            batteryManufacturer.computeRemainingTimeToProduceNextBatteryType(
                batteryOrder.getBatteryType()))
        .willReturn(batteryOrder.getAssemblyTime());
    onDemandBatteryWarehouseStrategy.addOrder(order);

    // when
    onDemandBatteryWarehouseStrategy.addOrder(anotherOrder);

    // then
    verify(batteryOrderDelayObserver).listenBatteryOrderDelay(anotherOrder);
  }

  @Test
  public void givenQueuedOrder_whenAddOrder_thenComputedDelayIsAddedToQueuedOrder() {
    // given
    OrderId id = new OrderId("id");
    Order firstOrder = createOrder(id);
    OrderId secondId = new OrderId("secondId");
    Order secondOrder = createOrder(secondId);
    OrderId thirdId = new OrderId("thirdId");
    Order thirdOrder = createOrder(thirdId);
    given(
            batteryManufacturer.computeRemainingTimeToProduceNextBatteryType(
                firstOrder.getBatteryOrder().getBatteryType()))
        .willReturn(firstOrder.getBatteryOrder().getAssemblyTime());
    onDemandBatteryWarehouseStrategy.addOrder(firstOrder);
    onDemandBatteryWarehouseStrategy.addOrder(secondOrder);
    onDemandBatteryWarehouseStrategy.addOrder(thirdOrder);
    AssemblyTime expectedDelay =
        firstOrder
            .getBatteryOrder()
            .getAssemblyTime()
            .add(secondOrder.getBatteryOrder().getAssemblyTime());

    // when
    AssemblyTime assemblyTime =
        onDemandBatteryWarehouseStrategy.computeRemainingTimeToProduce(thirdId);

    // then
    assertThat(assemblyTime.inWeeks()).isEqualTo(expectedDelay.inWeeks());
  }

  @Test
  public void givenAnOrder_whenCancelAllOrders_thenReturnAllCanceledOrders() {
    // given
    Order firstOrder = createOrder(new OrderId("id"));
    onDemandBatteryWarehouseStrategy.addOrder(firstOrder);

    // when
    List<Order> cancelledOrders = onDemandBatteryWarehouseStrategy.cancelAllOrders();

    // then
    assertThat(cancelledOrders).contains(firstOrder);
  }

  @Test
  public void givenAnOrder_whenCancelAllOrders_thenNoActiveOrderShouldRemain() {
    // given
    Order firstOrder = createOrder(new OrderId("id"));
    onDemandBatteryWarehouseStrategy.addOrder(firstOrder);
    onDemandBatteryWarehouseStrategy.cancelAllOrders();

    // when
    List<Order> cancelledOrders = onDemandBatteryWarehouseStrategy.cancelAllOrders();

    // then
    assertThat(cancelledOrders).isEmpty();
  }

  private Order createOrder(OrderId id) {
    BatteryOrder batteryOrder = new BatteryOrderBuilder().build();
    return new OrderBuilder().withOrderId(id).withBatteryOrder(batteryOrder).build();
  }
}
