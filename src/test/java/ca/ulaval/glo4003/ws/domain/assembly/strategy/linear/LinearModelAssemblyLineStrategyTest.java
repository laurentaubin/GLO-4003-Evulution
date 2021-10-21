package ca.ulaval.glo4003.ws.domain.assembly.strategy.linear;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssembledObserver;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.notification.ModelAssemblyDelayObserver;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LinearModelAssemblyLineStrategyTest {
  private static final String AN_ID = "fdsnj9203";
  private static final String ANOTHER_ID = "213dmsao4";
  private static final String OTHER_ID = "90dnw8";
  private static final OrderId AN_ORDER_ID = new OrderId(AN_ID);
  private static final OrderId ANOTHER_ORDER_ID = new OrderId(ANOTHER_ID);
  private static final OrderId OTHER_ORDER_ID = new OrderId(OTHER_ID);
  private static final ProductionTime A_REMAINING_PRODUCTION_TIME = new ProductionTime(43);
  private static final ProductionTime ANOTHER_REMAINING_PRODUCTION_TIME = new ProductionTime(763);
  private static final ProductionTime OTHER_REMAINING_PRODUCTION_TIME = new ProductionTime(4322);

  @Mock private Order anOrder;
  @Mock private Order anotherOrder;
  @Mock private Order otherOrder;
  @Mock private Model aModel;
  @Mock private Model anotherModel;
  @Mock private Model otherModel;
  @Mock private AssemblyLineAdapter modelAssemblyLineAdapter;
  @Mock private ModelAssembledObserver modelAssembledObserver;
  @Mock private ModelAssembledObserver anotherModelAssembledObserver;
  @Mock private ModelAssemblyDelayObserver modelAssemblyDelayObserver;

  private LinearModelAssemblyLineStrategy linearModelAssemblyLineStrategy;

  @BeforeEach
  public void setUp() {
    linearModelAssemblyLineStrategy = new LinearModelAssemblyLineStrategy(modelAssemblyLineAdapter);

    linearModelAssemblyLineStrategy.register(modelAssemblyDelayObserver);
  }

  @Test
  public void
      givenAnEmptyQueueAndNoOrderBeingAssembled_whenAddOrder_thenOrderIsSentToBeAssembled() {
    // given
    given(anOrder.getModel()).willReturn(aModel);

    // when
    linearModelAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelAssemblyLineAdapter).addOrder(anOrder);
  }

  @Test
  public void
      givenAnEmptyQueueAndAnOrderDoneBeingAssembled_whenAddOrder_thenNewOrderIsSentToBeAssembled() {
    givenAnOrderDoneBeingAssembled();
    given(anotherOrder.getModel()).willReturn(anotherModel);

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
    given(anOrder.getModel().getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    linearModelAssemblyLineStrategy.addOrder(anOrder);
    when(modelAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .thenReturn(AssemblyStatus.IN_PROGRESS, AssemblyStatus.ASSEMBLED);
    setUpAnotherOrder();
    given(anotherOrder.getModel().getProductionTime())
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
    given(aModel.getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    givenAnOrderBeingAssembled();
    setUpAnotherOrder();
    given(anotherModel.getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
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
    ProductionTime modelTimeToProduce = new ProductionTime(2);
    given(aModel.getProductionTime()).willReturn(modelTimeToProduce);
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
    ProductionTime modelTimeToProduce = new ProductionTime(4);
    ProductionTime expectedRemainingTimeToProduce = new ProductionTime(2);
    given(aModel.getProductionTime()).willReturn(modelTimeToProduce);
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
    given(anOrder.getModel().getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    linearModelAssemblyLineStrategy.addOrder(anOrder);
    given(modelAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.RECEIVED);
    setUpAnotherOrder();
    given(anotherOrder.getModel().getProductionTime())
        .willReturn(ANOTHER_REMAINING_PRODUCTION_TIME);
    linearModelAssemblyLineStrategy.addOrder(anotherOrder);
    setUpOtherOrder();
    given(otherOrder.getModel().getProductionTime()).willReturn(OTHER_REMAINING_PRODUCTION_TIME);
    linearModelAssemblyLineStrategy.addOrder(otherOrder);
    ProductionTime expectedRemainingTimeToProduce =
        new ProductionTime(
            A_REMAINING_PRODUCTION_TIME.inWeeks()
                + ANOTHER_REMAINING_PRODUCTION_TIME.inWeeks()
                + OTHER_REMAINING_PRODUCTION_TIME.inWeeks());

    // when
    ProductionTime remainingTimeToProduce =
        linearModelAssemblyLineStrategy.computeRemainingTimeToProduce(OTHER_ORDER_ID);

    // then
    assertThat(remainingTimeToProduce).isEqualTo(expectedRemainingTimeToProduce);
  }

  @Test
  public void givenNoCurrentOrder_whenAddOrder_thenDoNotNotifyAssemblyDelay() {
    // given
    given(anOrder.getModel()).willReturn(aModel);

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
    given(anOrder.getModel().getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    given(anotherOrder.getModel().getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    given(modelAssemblyLineAdapter.getAssemblyStatus(AN_ORDER_ID))
        .willReturn(AssemblyStatus.IN_PROGRESS);
    linearModelAssemblyLineStrategy.addOrder(anOrder);

    // when
    linearModelAssemblyLineStrategy.addOrder(anotherOrder);

    // then
    verify(modelAssemblyDelayObserver).listenModelAssemblyDelay(anotherOrder);
  }

  private void setUpAnOrder() {
    given(anOrder.getId()).willReturn(AN_ORDER_ID);
    given(anOrder.getModel()).willReturn(aModel);
  }

  private void setUpAnotherOrder() {
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(anotherOrder.getModel()).willReturn(anotherModel);
  }

  private void setUpOtherOrder() {
    given(otherOrder.getId()).willReturn(OTHER_ORDER_ID);
    given(otherOrder.getModel()).willReturn(otherModel);
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
