package ca.ulaval.glo4003.ws.domain.assembly.strategy;

import ca.ulaval.glo4003.ws.domain.assembly.battery.strategy.BatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.model.strategy.ModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderRepository;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.strategy.VehicleAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.fixture.OrderBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
  private static final boolean COMPLETED = true;
  private static final boolean NOT_COMPLETED = false;

  @Mock private ModelAssemblyLineStrategy modelAssemblyLineStrategy;
  @Mock private BatteryAssemblyLineStrategy batteryAssemblyLineStrategy;
  @Mock private VehicleAssemblyLineStrategy vehicleAssemblyLineStrategy;
  @Mock private Order anOrder;
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
    linearAssemblyStrategy.addOrder(anOrder);

    // then
    verify(modelAssemblyLineStrategy).addOrder(anOrder);
  }

  @Test
  public void whenListenToModelAssembled_thenOrderedBatteryIsSentToBeAssembled() {
    // when
    linearAssemblyStrategy.listenToModelAssembled(anOrder);

    // then
    verify(batteryAssemblyLineStrategy).addOrder(anOrder);
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
    linearAssemblyStrategy.listenToModelAssembled(anOrder);

    // then
    verify(batteryAssemblyLineStrategy).addOrder(anOrder);
  }

  @Test
  public void givenOrder_whenListenToBatteryAssembled_thenAssembleVehicleForOrder() {
    // when
    linearAssemblyStrategy.listenToBatteryAssembled(anOrder);

    // then
    verify(vehicleAssemblyLineStrategy).assembleVehicle(anOrder);
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
    verify(batteryAssemblyLineStrategy, never()).addOrder(anOrder);
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
    given(orderRepository.findAllCompletedOrders()).willReturn(List.of(anOrder, anotherOrder));

    // when
    linearAssemblyStrategy.shutdown();

    // then
    verify(anOrder).setIsReadyForDelivery(NOT_COMPLETED);
    verify(anotherOrder).setIsReadyForDelivery(NOT_COMPLETED);
  }

  @Test
  public void givenCompletedOrders_whenShutdown_thenUpdatedCompletedOrdersAreSaved() {
    // given
    given(orderRepository.findAllCompletedOrders()).willReturn(List.of(anOrder, anotherOrder));

    // when
    linearAssemblyStrategy.shutdown();

    // then
    verify(orderRepository).save(anOrder);
    verify(orderRepository).save(anotherOrder);
  }

  @Test
  public void givenAnOrder_whenListenToVehicleAssembled_thenOrderIsSetAsCompleted() {
    // when
    linearAssemblyStrategy.listenToModelAssembled(anOrder);

    // then
    anOrder.setIsReadyForDelivery(COMPLETED);
  }

  @Test
  public void givenAnOrder_whenListenToVehicleAssembled_thenOrderIsSaved() {
    // when
    linearAssemblyStrategy.listenToVehicleAssembled(anOrder);

    // then
    verify(orderRepository).save(anOrder);
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
