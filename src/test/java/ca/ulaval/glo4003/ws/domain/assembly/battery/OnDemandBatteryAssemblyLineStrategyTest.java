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
import ca.ulaval.glo4003.ws.domain.assembly.time.AssemblyTime;
import ca.ulaval.glo4003.ws.domain.notification.BatteryAssemblyDelayObserver;
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
  private static final AssemblyTime A_REMAINING_ASSEMBLY_TIME = new AssemblyTime(43);
  private static final AssemblyTime ANOTHER_REMAINING_ASSEMBLY_TIME = new AssemblyTime(763);
  private static final AssemblyTime OTHER_REMAINING_ASSEMBLY_TIME = new AssemblyTime(43212);

  @Mock private Order order;
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
    given(order.getBatteryOrder()).willReturn(batteryOrder);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(order);

    // then
    verify(batteryAssemblyLineAdapter).addOrder(order);
  }

  @Test
  public void
      givenAnEmptyQueueAndAnOrderDoneBeingAssembled_whenAddOrder_thenNewOrderIsSentToBeAssembled() {
    setUpAnOrder();
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.ASSEMBLED);
    linearBatteryAssemblyLineStrategy.addOrder(order);
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
    given(order.getBatteryOrder().getAssemblyTime()).willReturn(A_REMAINING_ASSEMBLY_TIME);
    linearBatteryAssemblyLineStrategy.addOrder(order);
    when(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .thenReturn(AssemblyStatus.IN_PROGRESS, AssemblyStatus.ASSEMBLED);
    setUpAnotherOrder();
    given(anotherOrder.getBatteryOrder().getAssemblyTime())
        .willReturn(ANOTHER_REMAINING_ASSEMBLY_TIME);
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
    given(order.getBatteryOrder().getAssemblyTime()).willReturn(A_REMAINING_ASSEMBLY_TIME);
    linearBatteryAssemblyLineStrategy.addOrder(order);
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
    linearBatteryAssemblyLineStrategy.addOrder(order);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.ASSEMBLED);
    linearBatteryAssemblyLineStrategy.register(batteryAssembledObserver);
    linearBatteryAssemblyLineStrategy.register(anotherBatteryAssemblyObserver);

    // when
    linearBatteryAssemblyLineStrategy.advance();

    // then
    verify(batteryAssembledObserver).listenToBatteryAssembled(order);
    verify(anotherBatteryAssemblyObserver).listenToBatteryAssembled(order);
  }

  @Test
  public void
      givenAnOrderSentToBeAssembled_whenComputeEstimatedTime_thenReturnTheBatteryRemainingTimeToProduce() {
    // given
    AssemblyTime batteryTimeToProduce = new AssemblyTime(2);
    given(batteryOrder.getAssemblyTime()).willReturn(batteryTimeToProduce);
    setUpAnOrder();
    linearBatteryAssemblyLineStrategy.addOrder(order);

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
    AssemblyTime batteryTimeToProduce = new AssemblyTime(4);
    AssemblyTime expectedRemainingTimeToProduce = new AssemblyTime(2);
    given(batteryOrder.getAssemblyTime()).willReturn(batteryTimeToProduce);
    setUpAnOrder();
    linearBatteryAssemblyLineStrategy.addOrder(order);
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
    given(order.getBatteryOrder().getAssemblyTime()).willReturn(A_REMAINING_ASSEMBLY_TIME);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.RECEIVED);
    given(order.getBatteryOrder().getAssemblyTime()).willReturn(A_REMAINING_ASSEMBLY_TIME);
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(anotherOrder.getBatteryOrder()).willReturn(anotherBatteryOrder);
    given(anotherOrder.getBatteryOrder().getAssemblyTime())
        .willReturn(ANOTHER_REMAINING_ASSEMBLY_TIME);
    given(otherOrder.getId()).willReturn(OTHER_ORDER_ID);
    given(otherOrder.getBatteryOrder()).willReturn(otherBatteryOrder);
    given(otherOrder.getBatteryOrder().getAssemblyTime()).willReturn(OTHER_REMAINING_ASSEMBLY_TIME);
    linearBatteryAssemblyLineStrategy.addOrder(order);
    linearBatteryAssemblyLineStrategy.addOrder(anotherOrder);
    linearBatteryAssemblyLineStrategy.addOrder(otherOrder);
    AssemblyTime expectedRemainingTimeToProduce =
        new AssemblyTime(
            A_REMAINING_ASSEMBLY_TIME.inWeeks()
                + ANOTHER_REMAINING_ASSEMBLY_TIME.inWeeks()
                + OTHER_REMAINING_ASSEMBLY_TIME.inWeeks());

    // when
    AssemblyTime remainingTimeToProduce =
        linearBatteryAssemblyLineStrategy.computeRemainingTimeToProduce(OTHER_ORDER_ID);

    // then
    assertThat(remainingTimeToProduce).isEqualTo(expectedRemainingTimeToProduce);
  }

  @Test
  public void givenNoCurrentOrder_whenAddOrder_thenDoNotNotifyAssemblyDelay() {
    // given
    given(order.getBatteryOrder()).willReturn(batteryOrder);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(order);

    // then
    verify(batteryAssemblyDelayObserver, never()).listenBatteryAssemblyDelay(any());
  }

  @Test
  public void givenCurrentOrderInQueue_whenAddOrder_thenNotifyAssemblyDelay() {
    // given
    setUpAnOrder();
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(anotherOrder.getBatteryOrder()).willReturn(anotherBatteryOrder);
    given(order.getBatteryOrder().getAssemblyTime()).willReturn(A_REMAINING_ASSEMBLY_TIME);
    given(anotherOrder.getBatteryOrder().getAssemblyTime())
        .willReturn(ANOTHER_REMAINING_ASSEMBLY_TIME);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.IN_PROGRESS);
    linearBatteryAssemblyLineStrategy.addOrder(order);
    linearBatteryAssemblyLineStrategy.register(batteryAssemblyDelayObserver);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(anotherOrder);

    // then
    verify(batteryAssemblyDelayObserver).listenBatteryAssemblyDelay(anotherOrder);
  }

  @Test
  public void givenNoCurrentOrder_whenAddOrder_thenDoNotAddAssemblyDelayToOrder() {
    // given
    given(order.getBatteryOrder()).willReturn(batteryOrder);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(order);

    // then
    verify(order, never()).addAssemblyDelay(any());
  }

  @Test
  public void givenCurrentOrderInQueue_whenAddOrder_thenAddAssemblyDelayToOrder() {
    // given
    setUpAnOrder();
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(anotherOrder.getBatteryOrder()).willReturn(anotherBatteryOrder);
    given(order.getBatteryOrder().getAssemblyTime()).willReturn(A_REMAINING_ASSEMBLY_TIME);
    given(anotherOrder.getBatteryOrder().getAssemblyTime())
        .willReturn(ANOTHER_REMAINING_ASSEMBLY_TIME);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.IN_PROGRESS);
    linearBatteryAssemblyLineStrategy.addOrder(order);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(anotherOrder);

    // then
    verify(anotherOrder).addAssemblyDelay(A_REMAINING_ASSEMBLY_TIME);
  }

  @Test
  public void givenOrders_whenShutdown_thenOrderQueueEmptied() {
    // given
    given(order.getBatteryOrder()).willReturn(batteryOrder);
    linearBatteryAssemblyLineStrategy.addOrder(order);

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
    given(order.getBatteryOrder().getAssemblyTime()).willReturn(A_REMAINING_ASSEMBLY_TIME);
    given(anotherOrder.getBatteryOrder().getAssemblyTime())
        .willReturn(ANOTHER_REMAINING_ASSEMBLY_TIME);
    given(batteryAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.IN_PROGRESS);
    linearBatteryAssemblyLineStrategy.addOrder(order);
    linearBatteryAssemblyLineStrategy.addOrder(anotherOrder);

    // when
    var result = linearBatteryAssemblyLineStrategy.getActiveOrders();

    // then

    assertThat(result).containsExactly(order, anotherOrder);
  }

  private void setUpAnOrder() {
    given(order.getId()).willReturn(AN_ORDER_ID);
    given(order.getBatteryOrder()).willReturn(batteryOrder);
  }

  private void setUpAnotherOrder() {
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(anotherOrder.getBatteryOrder()).willReturn(anotherBatteryOrder);
  }
}
