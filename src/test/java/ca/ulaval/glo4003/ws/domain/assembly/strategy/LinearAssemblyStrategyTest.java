package ca.ulaval.glo4003.ws.domain.assembly.strategy;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.assembly.battery.strategy.BatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.model.strategy.ModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderRepository;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.strategy.VehicleAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.fixture.OrderBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LinearAssemblyStrategyTest {
  private static final OrderId AN_ORDER_ID = new OrderId("anOrderId");
  private static final OrderId ANOTHER_ORDER_ID = new OrderId("anotherOrderId");
  private static final OrderId OTHER_ORDER_ID = new OrderId("otherOrderId");
  private static final OrderId YET_ANOTHER_ORDER_ID = new OrderId("yetAnotherOrderId");
  private final Order AN_ORDER = new OrderBuilder().withOrderId(AN_ORDER_ID).build();
  private final Order ANOTHER_ORDER = new OrderBuilder().withOrderId(ANOTHER_ORDER_ID).build();
  private final Order OTHER_ORDER = new OrderBuilder().withOrderId(OTHER_ORDER_ID).build();
  private final Order YET_ANOTHER_ODER =
      new OrderBuilder().withOrderId(YET_ANOTHER_ORDER_ID).build();
  private static final boolean IS_COMPLETED = true;
  private static final boolean IS_NOT_COMPLETED = false;

  @Mock private ModelAssemblyLineStrategy modelAssemblyLineStrategy;
  @Mock private BatteryAssemblyLineStrategy batteryAssemblyLineStrategy;
  @Mock private VehicleAssemblyLineStrategy vehicleAssemblyLineStrategy;
  @Mock private Order order;
  @Mock private Order anotherOrder;
  @Mock private OrderRepository orderRepository;

  private LinearAssemblyStrategy linearAssemblyStrategy;

  @BeforeEach
  public void setUp() {
    linearAssemblyStrategy =
        new LinearAssemblyStrategy(
            modelAssemblyLineStrategy,
            batteryAssemblyLineStrategy,
            vehicleAssemblyLineStrategy,
            orderRepository);
  }

  @Test
  public void givenAnOrder_whenAddOrder_thenOrderIsSentToTheModelAssemblyLine() {
    // when
    linearAssemblyStrategy.addOrder(order);

    // then
    verify(modelAssemblyLineStrategy).addOrder(order);
  }

  @Test
  public void whenListenToModelAssembled_thenOrderedBatteryIsSentToBeAssembled() {
    // when
    linearAssemblyStrategy.listenToModelAssembled(order);

    // then
    verify(batteryAssemblyLineStrategy).addOrder(order);
  }

  @Test
  public void whenAdvance_thenAdvanceAllAssemblyLines() {
    // when
    linearAssemblyStrategy.advance();

    // then
    verify(modelAssemblyLineStrategy).advance();
    verify(batteryAssemblyLineStrategy).advance();
    verify(vehicleAssemblyLineStrategy).advance();
  }

  @Test
  public void givenOrder_whenListenToModelAssembled_thenAssembleBatteryForOrder() {
    // when
    linearAssemblyStrategy.listenToModelAssembled(order);

    // then
    verify(batteryAssemblyLineStrategy).addOrder(order);
  }

  @Test
  public void givenOrder_whenListenToBatteryAssembled_thenAssembleVehicleForOrder() {
    // when
    linearAssemblyStrategy.listenToBatteryAssembled(order);

    // then
    verify(vehicleAssemblyLineStrategy).assembleVehicle(order);
  }

  @Test
  public void whenShutdown_thenBatteryAndModelAssemblyShutdown() {
    // when
    linearAssemblyStrategy.shutdown();

    // then
    verify(batteryAssemblyLineStrategy).shutdown();
    verify(vehicleAssemblyLineStrategy).shutdown();
  }

  @Test
  public void givenAlreadyActivatedProductionLine_whenReactivate_thenDoNotAddStoppedOrders() {
    // given
    linearAssemblyStrategy.reactivate();

    // when
    linearAssemblyStrategy.reactivate();

    // then
    verify(batteryAssemblyLineStrategy, never()).addOrder(order);
  }

  @Test
  public void
      givenOrdersInAssemblyLinesAndCompletedOrders_whenReactivate_thenOrdersAreSentBackInProductionInExpectedOrder() {
    // given
    given(batteryAssemblyLineStrategy.getActiveOrders()).willReturn(List.of(AN_ORDER));
    given(orderRepository.findAllCompletedOrders()).willReturn(List.of(ANOTHER_ORDER));
    given(vehicleAssemblyLineStrategy.getActiveOrders()).willReturn(List.of(OTHER_ORDER));
    linearAssemblyStrategy.shutdown();

    // when
    linearAssemblyStrategy.reactivate();

    // then
    InOrder batteryAssemblyLineCallOrder = inOrder(batteryAssemblyLineStrategy);
    batteryAssemblyLineCallOrder.verify(batteryAssemblyLineStrategy).addOrder(OTHER_ORDER);
    batteryAssemblyLineCallOrder.verify(batteryAssemblyLineStrategy).addOrder(AN_ORDER);
    batteryAssemblyLineCallOrder.verify(batteryAssemblyLineStrategy).addOrder(ANOTHER_ORDER);
  }

  @Test
  public void givenCompletedOrders_whenShutdown_thenCompletedOrdersAreSetAsNotReadyAnymore() {
    // given
    given(orderRepository.findAllCompletedOrders()).willReturn(List.of(order, anotherOrder));

    // when
    linearAssemblyStrategy.shutdown();

    // then
    verify(order).setIsReadyForDelivery(IS_NOT_COMPLETED);
    verify(anotherOrder).setIsReadyForDelivery(IS_NOT_COMPLETED);
  }

  @Test
  public void givenCompletedOrders_whenShutdown_thenUpdatedCompletedOrdersAreSaved() {
    // given
    given(orderRepository.findAllCompletedOrders()).willReturn(List.of(order, anotherOrder));

    // when
    linearAssemblyStrategy.shutdown();

    // then
    verify(orderRepository).save(order);
    verify(orderRepository).save(anotherOrder);
  }

  @Test
  public void givenAnOrder_whenListenToVehicleAssembled_thenOrderIsSetAsCompleted() {
    // when
    linearAssemblyStrategy.listenToModelAssembled(order);

    // then
    order.setIsReadyForDelivery(IS_COMPLETED);
  }

  @Test
  public void givenAnOrder_whenListenToVehicleAssembled_thenOrderIsSaved() {
    // when
    linearAssemblyStrategy.listenToVehicleAssembled(order);

    // then
    verify(orderRepository).save(order);
  }

  @Test
  public void givenOrders_whenGetActivesOrders_thenReturnActiveOrders() {
    // given
    given(batteryAssemblyLineStrategy.getActiveOrders()).willReturn(List.of(AN_ORDER));
    given(orderRepository.findAllCompletedOrders()).willReturn(List.of(ANOTHER_ORDER));
    given(vehicleAssemblyLineStrategy.getActiveOrders()).willReturn(List.of(OTHER_ORDER));
    given(modelAssemblyLineStrategy.getActiveOrders()).willReturn(List.of(YET_ANOTHER_ODER));

    // when
    List<Order> activeOrders = linearAssemblyStrategy.getActiveOrders();

    // then
    assertThat(activeOrders)
        .containsExactlyElementsIn(List.of(AN_ORDER, ANOTHER_ORDER, OTHER_ORDER, YET_ANOTHER_ODER));
  }
}
