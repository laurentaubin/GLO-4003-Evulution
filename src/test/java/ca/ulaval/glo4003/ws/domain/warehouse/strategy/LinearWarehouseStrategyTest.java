package ca.ulaval.glo4003.ws.domain.warehouse.strategy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.notification.OrderDelayObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryWarehouseStrategy;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelWarehouseStrategy;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderRepository;
import ca.ulaval.glo4003.ws.domain.warehouse.vehicle.VehicleWarehouseStrategy;
import ca.ulaval.glo4003.ws.fixture.OrderBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LinearWarehouseStrategyTest {
  private static final OrderId AN_ORDER_ID = new OrderId("anOrderId");
  private static final OrderId ANOTHER_ORDER_ID = new OrderId("anotherOrderId");
  private static final OrderId OTHER_ORDER_ID = new OrderId("otherOrderId");
  private static final OrderId YET_ANOTHER_ORDER_ID = new OrderId("yetAnotherOrderId");
  private final Order AN_ORDER = new OrderBuilder().withOrderId(AN_ORDER_ID).build();
  private final Order ANOTHER_ORDER = new OrderBuilder().withOrderId(ANOTHER_ORDER_ID).build();
  private final Order OTHER_ORDER = new OrderBuilder().withOrderId(OTHER_ORDER_ID).build();
  private final Order YET_ANOTHER_ORDER =
      new OrderBuilder().withOrderId(YET_ANOTHER_ORDER_ID).build();
  private static final boolean COMPLETED = true;

  @Mock private ModelWarehouseStrategy modelWarehouseStrategy;
  @Mock private BatteryWarehouseStrategy batteryWarehouseStrategy;
  @Mock private VehicleWarehouseStrategy vehicleWarehouseStrategy;
  @Mock private Order anOrder;
  @Mock private OrderRepository orderRepository;
  @Mock private OrderDelayObserver orderDelayObserver;

  private LinearWarehouseStrategy linearWarehouseStrategy;

  @BeforeEach
  public void setUp() {
    linearWarehouseStrategy =
        new LinearWarehouseStrategy(
            modelWarehouseStrategy,
            batteryWarehouseStrategy,
            vehicleWarehouseStrategy,
            orderRepository);
    linearWarehouseStrategy.register(orderDelayObserver);
  }

  @Test
  public void givenAnOrder_whenAddOrder_thenOrderIsSentToTheModelAssemblyLine() {
    // when
    linearWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelWarehouseStrategy).addOrder(anOrder);
  }

  @Test
  public void givenOrder_whenListenToModelAssembled_thenAddOrderForBattery() {
    // when
    linearWarehouseStrategy.listenToModelInStock(anOrder);

    // then
    verify(batteryWarehouseStrategy).addOrder(anOrder);
  }

  @Test
  public void givenOrder_whenListenToBatteryInStock_thenAddOrderForVehicle() {
    // when
    linearWarehouseStrategy.listenToBatteryInStock(anOrder);

    // then
    verify(vehicleWarehouseStrategy).addOrder(anOrder);
  }

  @Test
  public void whenListenToAssemblyShutdown_thenCancelAllBatteryWarehouseOrders() {
    // when
    linearWarehouseStrategy.listenToAssemblyShutdown();

    // then
    verify(batteryWarehouseStrategy).cancelAllOrders();
  }

  @Test
  public void whenListenToAssemblyShutdown_thenCancelAllVehicleAssemblyLineOrders() {
    // when
    linearWarehouseStrategy.listenToAssemblyShutdown();

    // then
    verify(vehicleWarehouseStrategy).cancelAllOrders();
  }

  @Test
  public void whenListenToAssemblyShutdown_thenGetModelWarehouseActiveOrders() {
    // when
    linearWarehouseStrategy.listenToAssemblyShutdown();

    // then
    verify(modelWarehouseStrategy).getActiveOrders();
  }

  @Test
  public void whenListenToAssemblyShutdown_thenFindAllCompletedOrders() {
    // when
    linearWarehouseStrategy.listenToAssemblyShutdown();

    // then
    verify(orderRepository).findAllCompletedOrders();
  }

  @Test
  public void whenListenToAssemblyShutdown_thenNotifyOrderDelayForAllOrders() {
    // given
    given(modelWarehouseStrategy.getActiveOrders()).willReturn(List.of(YET_ANOTHER_ORDER));
    given(batteryWarehouseStrategy.cancelAllOrders()).willReturn(List.of(AN_ORDER));
    given(orderRepository.findAllCompletedOrders()).willReturn(List.of(ANOTHER_ORDER));
    given(vehicleWarehouseStrategy.cancelAllOrders()).willReturn(List.of(OTHER_ORDER));

    // when
    linearWarehouseStrategy.listenToAssemblyShutdown();

    // then
    verify(orderDelayObserver).listenToOrderDelay(any());
  }

  @Test
  public void givenAnOrder_whenListenToVehicleAssembled_thenOrderIsSetAsCompleted() {
    // when
    linearWarehouseStrategy.listenToModelInStock(anOrder);

    // then
    anOrder.setIsReadyForDelivery(COMPLETED);
  }

  @Test
  public void givenAnOrder_whenListenToVehicleAssembled_thenOrderIsSaved() {
    // when
    linearWarehouseStrategy.listenToVehicleInStock(anOrder);

    // then
    verify(orderRepository).save(anOrder);
  }

  @Test
  public void whenListToAssemblyReactivation_thenReorderBatteryForAllCancelledOrdersInOrder() {
    // given
    given(batteryWarehouseStrategy.cancelAllOrders()).willReturn(List.of(AN_ORDER));
    given(orderRepository.findAllCompletedOrders()).willReturn(List.of(ANOTHER_ORDER));
    given(vehicleWarehouseStrategy.cancelAllOrders()).willReturn(List.of(OTHER_ORDER));
    linearWarehouseStrategy.listenToAssemblyShutdown();

    // when
    linearWarehouseStrategy.listenToAssemblyReactivation();

    // then
    InOrder inOrder = Mockito.inOrder(batteryWarehouseStrategy);
    inOrder.verify(batteryWarehouseStrategy).addOrder(OTHER_ORDER);
    inOrder.verify(batteryWarehouseStrategy).addOrder(AN_ORDER);
    inOrder.verify(batteryWarehouseStrategy).addOrder(ANOTHER_ORDER);
  }
}
