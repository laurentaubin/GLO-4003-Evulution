package ca.ulaval.glo4003.ws.domain.assembly.model.strategy;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelAssembledObserver;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.time.AssemblyTime;
import ca.ulaval.glo4003.ws.domain.notification.ModelAssemblyDelayObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OnDemandModelAssemblyLineStrategyTest {
  private static final String AN_ID = "fdsnj9203";
  private static final String ANOTHER_ID = "213dmsao4";
  private static final String OTHER_ID = "90dnw8";
  private static final OrderId AN_ORDER_ID = new OrderId(AN_ID);
  private static final OrderId ANOTHER_ORDER_ID = new OrderId(ANOTHER_ID);
  private static final OrderId OTHER_ORDER_ID = new OrderId(OTHER_ID);
  private static final AssemblyTime A_REMAINING_PRODUCTION_TIME = new AssemblyTime(43);
  private static final AssemblyTime ANOTHER_REMAINING_PRODUCTION_TIME = new AssemblyTime(763);
  private static final AssemblyTime OTHER_REMAINING_PRODUCTION_TIME = new AssemblyTime(4322);

  @Mock private Order anOrder;
  @Mock private Order anotherOrder;
  @Mock private Order otherOrder;
  @Mock private ModelOrder aModelOrder;
  @Mock private ModelOrder anotherModelOrder;
  @Mock private ModelOrder otherModelOrder;
  @Mock private ModelAssemblyLineAdapter modelAssemblyLineAdapter;
  @Mock private ModelAssembledObserver modelAssembledObserver;
  @Mock private ModelAssembledObserver anotherModelAssembledObserver;
  @Mock private ModelAssemblyDelayObserver modelAssemblyDelayObserver;

  private OnDemandModelAssemblyLineStrategy linearModelAssemblyLineStrategy;

  @BeforeEach
  public void setUp() {
    linearModelAssemblyLineStrategy =
        new OnDemandModelAssemblyLineStrategy(modelAssemblyLineAdapter);

    linearModelAssemblyLineStrategy.register(modelAssemblyDelayObserver);
  }

  @Test
  public void
      givenAnEmptyQueueAndNoOrderBeingAssembled_whenAddOrder_thenOrderIsSentToBeAssembled() {
    // given
    given(anOrder.getModelOrder()).willReturn(aModelOrder);

    // when
    linearModelAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelAssemblyLineAdapter).addOrder(anOrder);
  }

  @Test
  public void
      givenAnEmptyQueueAndAnOrderDoneBeingAssembled_whenAddOrder_thenNewOrderIsSentToBeAssembled() {
    givenAnOrderDoneBeingAssembled();
    given(anotherOrder.getModelOrder()).willReturn(anotherModelOrder);

    // when
    linearModelAssemblyLineStrategy.addOrder(anotherOrder);

    // then
    verify(modelAssemblyLineAdapter).addOrder(anotherOrder);
  }

  @Test
  public void whenAdvance_thenCallAdvanceOnVehicleAssemblyLine() {
    // when
    linearModelAssemblyLineStrategy.advance();

    // then
    verify(modelAssemblyLineAdapter).advance();
  }

  @Test
  public void
      givenAnOrderDoneBeingAssembledAndAnotherOrderInQueue_whenAdvance_thenOrderInQueueIsSentToBeAssembled() {
    // given
    setUpAnOrder();
    given(anOrder.getModelOrder().getAssemblyTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    linearModelAssemblyLineStrategy.addOrder(anOrder);
    when(modelAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .thenReturn(AssemblyStatus.IN_PROGRESS, AssemblyStatus.ASSEMBLED);
    setUpAnotherOrder();
    given(anotherOrder.getModelOrder().getAssemblyTime())
        .willReturn(ANOTHER_REMAINING_PRODUCTION_TIME);
    linearModelAssemblyLineStrategy.addOrder(anotherOrder);

    // when
    linearModelAssemblyLineStrategy.advance();

    // then
    verify(modelAssemblyLineAdapter, times(1)).addOrder(anotherOrder);
  }

  @Test
  public void
      givenAnOrderBeingAssembledAndAnotherOrderInQueue_whenAdvance_thenOrderInQueueIsNotSentToBeAssembled() {
    // given
    given(aModelOrder.getAssemblyTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    givenAnOrderBeingAssembled();
    setUpAnotherOrder();
    given(anotherModelOrder.getAssemblyTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    linearModelAssemblyLineStrategy.addOrder(anotherOrder);

    // when
    linearModelAssemblyLineStrategy.advance();

    // then
    verify(modelAssemblyLineAdapter, never()).addOrder(anotherOrder);
  }

  @Test
  public void givenAnOrderDoneBeingAssembled_whenAdvance_thenNotifyAllModelAssembledObservers() {
    // given
    givenAnOrderDoneBeingAssembled();
    linearModelAssemblyLineStrategy.register(modelAssembledObserver);
    linearModelAssemblyLineStrategy.register(anotherModelAssembledObserver);

    // when
    linearModelAssemblyLineStrategy.advance();

    // then
    verify(modelAssembledObserver).listenToModelAssembled(anOrder);
    verify(anotherModelAssembledObserver).listenToModelAssembled(anOrder);
  }

  @Test
  public void
      givenAnOrderSentToBeAssembled_whenComputeEstimatedTime_thenReturnTheModelRemainingTimeToProduce() {
    // given
    AssemblyTime modelTimeToProduce = new AssemblyTime(2);
    given(aModelOrder.getAssemblyTime()).willReturn(modelTimeToProduce);
    setUpAnOrder();
    linearModelAssemblyLineStrategy.addOrder(anOrder);

    // when
    linearModelAssemblyLineStrategy.computeRemainingTimeToProduce(AN_ORDER_ID);

    // then
    assertThat(linearModelAssemblyLineStrategy.computeRemainingTimeToProduce(AN_ORDER_ID))
        .isEqualTo(modelTimeToProduce);
  }

  @Test
  public void
      givenAnOrderANumberOfWeeksElapsedIntoBeingAssembled_whenComputeRemainingTimeToProduce_thenReturnTheOrderModelRemainingTimeToProduceMinusTheNumberOfWeeksElapsed() {
    // given
    AssemblyTime modelTimeToProduce = new AssemblyTime(4);
    AssemblyTime expectedRemainingTimeToProduce = new AssemblyTime(2);
    given(aModelOrder.getAssemblyTime()).willReturn(modelTimeToProduce);
    givenAnOrderBeingAssembled();
    linearModelAssemblyLineStrategy.advance();
    linearModelAssemblyLineStrategy.advance();

    // when
    linearModelAssemblyLineStrategy.computeRemainingTimeToProduce(AN_ORDER_ID);

    // then
    assertThat(linearModelAssemblyLineStrategy.computeRemainingTimeToProduce(AN_ORDER_ID))
        .isEqualTo(expectedRemainingTimeToProduce);
  }

  @Test
  public void
      givenAQueuedOrder_whenComputeRemainingTimeToProduce_thenTimeIsComputedBasedOnQueuedOrderPosition() {
    // given
    setUpAnOrder();
    given(anOrder.getModelOrder().getAssemblyTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    linearModelAssemblyLineStrategy.addOrder(anOrder);
    given(modelAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.RECEIVED);
    setUpAnotherOrder();
    given(anotherOrder.getModelOrder().getAssemblyTime())
        .willReturn(ANOTHER_REMAINING_PRODUCTION_TIME);
    linearModelAssemblyLineStrategy.addOrder(anotherOrder);
    setUpOtherOrder();
    given(otherOrder.getModelOrder().getAssemblyTime()).willReturn(OTHER_REMAINING_PRODUCTION_TIME);
    linearModelAssemblyLineStrategy.addOrder(otherOrder);
    AssemblyTime expectedRemainingTimeToProduce =
        new AssemblyTime(
            A_REMAINING_PRODUCTION_TIME.inWeeks()
                + ANOTHER_REMAINING_PRODUCTION_TIME.inWeeks()
                + OTHER_REMAINING_PRODUCTION_TIME.inWeeks());

    // when
    AssemblyTime remainingTimeToProduce =
        linearModelAssemblyLineStrategy.computeRemainingTimeToProduce(OTHER_ORDER_ID);

    // then
    assertThat(remainingTimeToProduce).isEqualTo(expectedRemainingTimeToProduce);
  }

  @Test
  public void givenNoCurrentOrder_whenAddOrder_thenDoNotNotifyAssemblyDelay() {
    // given
    given(anOrder.getModelOrder()).willReturn(aModelOrder);

    // when
    linearModelAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelAssemblyDelayObserver, never()).listenModelAssemblyDelay(any());
  }

  @Test
  public void givenCurrentOrderInQueue_whenAddOrder_thenNotifyAssemblyDelay() {
    // given
    setUpAnOrder();
    setUpAnotherOrder();
    given(anOrder.getModelOrder().getAssemblyTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    given(anotherOrder.getModelOrder().getAssemblyTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    given(modelAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.IN_PROGRESS);
    linearModelAssemblyLineStrategy.addOrder(anOrder);

    // when
    linearModelAssemblyLineStrategy.addOrder(anotherOrder);

    // then
    verify(modelAssemblyDelayObserver).listenModelAssemblyDelay(anotherOrder);
  }

  @Test
  public void givenNoCurrentOrder_whenAddOrder_thenDoNotAddAssemblyDelayToOrder() {
    // given
    given(anOrder.getModelOrder()).willReturn(aModelOrder);

    // when
    linearModelAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(anOrder, never()).addAssemblyDelay(any());
  }

  @Test
  public void givenCurrentOrderInQueue_whenAddOrder_thenAddAssemblyDelayToOrder() {
    // given
    setUpAnOrder();
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(anotherOrder.getModelOrder()).willReturn(anotherModelOrder);
    given(anOrder.getModelOrder().getAssemblyTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    given(anotherOrder.getModelOrder().getAssemblyTime())
        .willReturn(ANOTHER_REMAINING_PRODUCTION_TIME);
    given(modelAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.IN_PROGRESS);
    linearModelAssemblyLineStrategy.addOrder(anOrder);

    // when
    linearModelAssemblyLineStrategy.addOrder(anotherOrder);

    // then
    verify(anotherOrder).addAssemblyDelay(A_REMAINING_PRODUCTION_TIME);
  }

  @Test
  public void givenOrdersInQueue_whenGetActiveOrders_thenReturnOrders() {
    // given
    setUpAnOrder();
    setUpAnotherOrder();
    given(anOrder.getModelOrder().getAssemblyTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    given(anotherOrder.getModelOrder().getAssemblyTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    given(modelAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.IN_PROGRESS);
    linearModelAssemblyLineStrategy.addOrder(anOrder);
    linearModelAssemblyLineStrategy.addOrder(anotherOrder);

    // when
    var result = linearModelAssemblyLineStrategy.getActiveOrders();

    // then
    assertThat(result).contains(anOrder);
  }

  private void setUpAnOrder() {
    given(anOrder.getId()).willReturn(AN_ORDER_ID);
    given(anOrder.getModelOrder()).willReturn(aModelOrder);
  }

  private void setUpAnotherOrder() {
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(anotherOrder.getModelOrder()).willReturn(anotherModelOrder);
  }

  private void setUpOtherOrder() {
    given(otherOrder.getId()).willReturn(OTHER_ORDER_ID);
    given(otherOrder.getModelOrder()).willReturn(otherModelOrder);
  }

  private void givenAnOrderBeingAssembled() {
    setUpAnOrder();
    linearModelAssemblyLineStrategy.addOrder(anOrder);
    given(modelAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.IN_PROGRESS);
  }

  private void givenAnOrderDoneBeingAssembled() {
    setUpAnOrder();
    linearModelAssemblyLineStrategy.addOrder(anOrder);
    given(modelAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.ASSEMBLED);
  }
}
