package ca.ulaval.glo4003.ws.domain.assembly.battery;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.battery.strategy.OnDemandBatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.notification.BatteryAssemblyDelayObserver;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OnDemandBatteryAssemblyLineStrategyTest {
  private static final String AN_ID = "fdsnj9203";
  private static final String ANOTHER_ID = "213dmsao4";
  private static final String OTHER_ID = "fdsiom234";
  private static final OrderId AN_ORDER_ID = new OrderId(AN_ID);
  private static final OrderId ANOTHER_ORDER_ID = new OrderId(ANOTHER_ID);
  private static final OrderId OTHER_ORDER_ID = new OrderId(OTHER_ID);
  private static final ProductionTime A_REMAINING_PRODUCTION_TIME = new ProductionTime(43);
  private static final ProductionTime ANOTHER_REMAINING_PRODUCTION_TIME = new ProductionTime(763);
  private static final ProductionTime OTHER_REMAINING_PRODUCTION_TIME = new ProductionTime(43212);

  @Mock private Order anOrder;
  @Mock private Order anotherOrder;
  @Mock private Order otherOrder;
  @Mock private BatteryOrder batteryOrder;
  @Mock private BatteryOrder anotherBatteryOrder;
  @Mock private BatteryOrder otherBatteryOrder;
  @Mock private BatteryAssemblyLineAdapter batteryAssemblyLineAdapter;
  @Mock private BatteryAssembledObserver batteryAssembledObserver;
  @Mock private BatteryAssembledObserver anotherBatteryAssemblyObserver;
  @Mock private BatteryAssemblyDelayObserver batteryAssemblyDelayObserver;

  private OnDemandBatteryAssemblyLineStrategy linearBatteryAssemblyLineStrategy;

  @BeforeEach
  public void setUp() {
    linearBatteryAssemblyLineStrategy =
        new OnDemandBatteryAssemblyLineStrategy(batteryAssemblyLineAdapter);
  }

  @Test
  public void
      givenAnEmptyQueueAndNoOrderBeingAssembled_whenAddOrder_thenOrderIsSentToBeAssembled() {
    // given
    given(anOrder.getBatteryOrder()).willReturn(batteryOrder);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(batteryAssemblyLineAdapter).addOrder(anOrder);
  }

  @Test
  public void
      givenAnEmptyQueueAndAnOrderDoneBeingAssembled_whenAddOrder_thenNewOrderIsSentToBeAssembled() {
    setUpAnOrder();
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.ASSEMBLED);
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);
    given(anotherOrder.getBatteryOrder()).willReturn(anotherBatteryOrder);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(anotherOrder);

    // then
    verify(batteryAssemblyLineAdapter).addOrder(anotherOrder);
  }

  @Test
  public void whenAdvance_thenCallAdvanceOnBatteryAssemblyLine() {
    // when
    linearBatteryAssemblyLineStrategy.advance();

    // then
    verify(batteryAssemblyLineAdapter).advance();
  }

  @Test
  public void
      givenAnOrderDoneBeingAssembledAndAnotherOrderInQueue_whenAdvance_thenOrderInQueueIsSentToBeAssembled() {
    // given
    setUpAnOrder();
    given(anOrder.getBatteryOrder().getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);
    when(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .thenReturn(AssemblyStatus.IN_PROGRESS, AssemblyStatus.ASSEMBLED);
    setUpAnotherOrder();
    given(anotherOrder.getBatteryOrder().getProductionTime())
        .willReturn(ANOTHER_REMAINING_PRODUCTION_TIME);
    linearBatteryAssemblyLineStrategy.addOrder(anotherOrder);

    // when
    linearBatteryAssemblyLineStrategy.advance();

    // then
    verify(batteryAssemblyLineAdapter, times(1)).addOrder(anotherOrder);
  }

  @Test
  public void
      givenAnOrderBeingAssembledAndAnotherOrderInQueue_whenAdvance_thenOrderInQueueIsNotSentToBeAssembled() {
    // given
    setUpAnOrder();
    given(anOrder.getBatteryOrder().getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.IN_PROGRESS);

    // when
    linearBatteryAssemblyLineStrategy.advance();

    // then
    verify(batteryAssemblyLineAdapter, never()).addOrder(anotherOrder);
  }

  @Test
  public void givenAnOrderDoneBeingAssembled_whenAdvance_thenNotifyAllModelAssembledObservers() {
    // given
    setUpAnOrder();
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.ASSEMBLED);
    linearBatteryAssemblyLineStrategy.register(batteryAssembledObserver);
    linearBatteryAssemblyLineStrategy.register(anotherBatteryAssemblyObserver);

    // when
    linearBatteryAssemblyLineStrategy.advance();

    // then
    verify(batteryAssembledObserver).listenToBatteryAssembled(anOrder);
    verify(anotherBatteryAssemblyObserver).listenToBatteryAssembled(anOrder);
  }

  @Test
  public void
      givenAnOrderSentToBeAssembled_whenComputeEstimatedTime_thenReturnTheBatteryRemainingTimeToProduce() {
    // given
    ProductionTime batteryTimeToProduce = new ProductionTime(2);
    given(batteryOrder.getProductionTime()).willReturn(batteryTimeToProduce);
    setUpAnOrder();
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);

    // when
    linearBatteryAssemblyLineStrategy.computeRemainingTimeToProduce(AN_ORDER_ID);

    // then
    assertThat(linearBatteryAssemblyLineStrategy.computeRemainingTimeToProduce(AN_ORDER_ID))
        .isEqualTo(batteryTimeToProduce);
  }

  @Test
  public void
      givenAnOrderANumberOfWeeksElapsedIntoBeingAssembled_whenComputeRemainingTimeToProduce_thenReturnTheOrderBatteryRemainingTimeToProduceMinusTheNumberOfWeeksElapsed() {
    // given
    ProductionTime batteryTimeToProduce = new ProductionTime(4);
    ProductionTime expectedRemainingTimeToProduce = new ProductionTime(2);
    given(batteryOrder.getProductionTime()).willReturn(batteryTimeToProduce);
    setUpAnOrder();
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.IN_PROGRESS);
    linearBatteryAssemblyLineStrategy.advance();
    linearBatteryAssemblyLineStrategy.advance();

    // when
    linearBatteryAssemblyLineStrategy.computeRemainingTimeToProduce(AN_ORDER_ID);

    // then
    assertThat(linearBatteryAssemblyLineStrategy.computeRemainingTimeToProduce(AN_ORDER_ID))
        .isEqualTo(expectedRemainingTimeToProduce);
  }

  @Test
  public void
      givenAQueuedOrder_whenComputeRemainingTimeToProduce_thenTimeIsComputedBasedOnTheQueuedOrderPosition() {
    // given
    setUpAnOrder();
    given(anOrder.getBatteryOrder().getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.RECEIVED);
    given(anOrder.getBatteryOrder().getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(anotherOrder.getBatteryOrder()).willReturn(anotherBatteryOrder);
    given(anotherOrder.getBatteryOrder().getProductionTime())
        .willReturn(ANOTHER_REMAINING_PRODUCTION_TIME);
    given(otherOrder.getId()).willReturn(OTHER_ORDER_ID);
    given(otherOrder.getBatteryOrder()).willReturn(otherBatteryOrder);
    given(otherOrder.getBatteryOrder().getProductionTime())
        .willReturn(OTHER_REMAINING_PRODUCTION_TIME);
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);
    linearBatteryAssemblyLineStrategy.addOrder(anotherOrder);
    linearBatteryAssemblyLineStrategy.addOrder(otherOrder);
    ProductionTime expectedRemainingTimeToProduce =
        new ProductionTime(
            A_REMAINING_PRODUCTION_TIME.inWeeks()
                + ANOTHER_REMAINING_PRODUCTION_TIME.inWeeks()
                + OTHER_REMAINING_PRODUCTION_TIME.inWeeks());

    // when
    ProductionTime remainingTimeToProduce =
        linearBatteryAssemblyLineStrategy.computeRemainingTimeToProduce(OTHER_ORDER_ID);

    // then
    assertThat(remainingTimeToProduce).isEqualTo(expectedRemainingTimeToProduce);
  }

  @Test
  public void givenNoCurrentOrder_whenAddOrder_thenDoNotNotifyAssemblyDelay() {
    // given
    given(anOrder.getBatteryOrder()).willReturn(batteryOrder);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(batteryAssemblyDelayObserver, never()).listenBatteryAssemblyDelay(any());
  }

  @Test
  public void givenCurrentOrderInQueue_whenAddOrder_thenNotifyAssemblyDelay() {
    // given
    setUpAnOrder();
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(anotherOrder.getBatteryOrder()).willReturn(anotherBatteryOrder);
    given(anOrder.getBatteryOrder().getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    given(anotherOrder.getBatteryOrder().getProductionTime())
        .willReturn(ANOTHER_REMAINING_PRODUCTION_TIME);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.IN_PROGRESS);
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);
    linearBatteryAssemblyLineStrategy.register(batteryAssemblyDelayObserver);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(anotherOrder);

    // then
    verify(batteryAssemblyDelayObserver).listenBatteryAssemblyDelay(anotherOrder);
  }

  @Test
  public void givenNoCurrentOrder_whenAddOrder_thenDoNotAddAssemblyDelayToOrder() {
    // given
    given(anOrder.getBatteryOrder()).willReturn(batteryOrder);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(anOrder, never()).addAssemblyDelay(any());
  }

  @Test
  public void givenCurrentOrderInQueue_whenAddOrder_thenAddAssemblyDelayToOrder() {
    // given
    setUpAnOrder();
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(anotherOrder.getBatteryOrder()).willReturn(anotherBatteryOrder);
    given(anOrder.getBatteryOrder().getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    given(anotherOrder.getBatteryOrder().getProductionTime())
        .willReturn(ANOTHER_REMAINING_PRODUCTION_TIME);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.IN_PROGRESS);
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(anotherOrder);

    // then
    verify(anotherOrder).addAssemblyDelay(A_REMAINING_PRODUCTION_TIME);
  }

  @Test
  public void givenOrders_whenShutdown_thenOrderQueueEmptied() {
    // given
    given(anOrder.getBatteryOrder()).willReturn(batteryOrder);
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);

    // when
    linearBatteryAssemblyLineStrategy.shutdown();

    // then
    assertThat(linearBatteryAssemblyLineStrategy.getActiveOrders()).isEmpty();
  }

  @Test
  public void givenActiveOrderAndOrdersInQueue_whenGetActiveOrders_thenReturnAllOrders() {
    // given
    setUpAnOrder();
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(anotherOrder.getBatteryOrder()).willReturn(anotherBatteryOrder);
    given(anOrder.getBatteryOrder().getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    given(anotherOrder.getBatteryOrder().getProductionTime())
        .willReturn(ANOTHER_REMAINING_PRODUCTION_TIME);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.IN_PROGRESS);
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);
    linearBatteryAssemblyLineStrategy.addOrder(anotherOrder);

    // when
    var result = linearBatteryAssemblyLineStrategy.getActiveOrders();

    // then

    assertThat(result).containsExactly(anOrder, anotherOrder);
  }

  private void setUpAnOrder() {
    given(anOrder.getId()).willReturn(AN_ORDER_ID);
    given(anOrder.getBatteryOrder()).willReturn(batteryOrder);
  }

  private void setUpAnotherOrder() {
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(anotherOrder.getBatteryOrder()).willReturn(anotherBatteryOrder);
  }
}
