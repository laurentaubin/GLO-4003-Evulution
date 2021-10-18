package ca.ulaval.glo4003.ws.infrastructure.assembly.model;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.evulution.car_manufacture.VehicleAssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssembledObserver;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.Model;
import ca.ulaval.glo4003.ws.infrastructure.assembly.CommandIdFactory;
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
  private static final String A_MODEL_NAME = "Vandry";
  private static final int A_REMAINING_TIME_TO_PRODUCE = 43;
  private static final int ANOTHER_REMAINING_TIME_TO_PRODUCE = 763;
  private static final int OTHER_REMAINING_TIME_TO_PRODUCE = 4322;

  @Mock private Order anOrder;
  @Mock private Order anotherOrder;
  @Mock private Order otherOrder;
  @Mock private Model aModel;
  @Mock private Model anotherModel;
  @Mock private Model otherModel;
  @Mock private VehicleAssemblyLine vehicleAssemblyLine;
  @Mock private CommandIdFactory commandIdFactory;
  @Mock private CommandID aCommandId;
  @Mock private CommandID anotherCommandId;
  @Mock private ModelAssembledObserver modelAssembledObserver;
  @Mock private ModelAssembledObserver anotherModelAssembledObserver;

  private LinearModelAssemblyLineStrategy linearModelAssemblyLineStrategy;

  @BeforeEach
  public void setUp() {
    linearModelAssemblyLineStrategy =
        new LinearModelAssemblyLineStrategy(vehicleAssemblyLine, commandIdFactory);
  }

  @Test
  public void
      givenAnEmptyQueueAndNoOrderBeingAssembled_whenAddOrder_thenOrderIsSentToBeAssembled() {
    // given
    setUpAnOrder();
    given(commandIdFactory.createFromOrderId(AN_ORDER_ID)).willReturn(aCommandId);

    // when
    linearModelAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(vehicleAssemblyLine).newCarCommand(aCommandId, A_MODEL_NAME);
  }

  @Test
  public void
      givenAnEmptyQueueAndAnOrderDoneBeingAssembled_whenAddOrder_thenNewOrderIsSentToBeAssembled() {
    setUpAnOrder();
    setUpAnotherOrder();
    given(vehicleAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.ASSEMBLED);
    linearModelAssemblyLineStrategy.addOrder(anOrder);

    // when
    linearModelAssemblyLineStrategy.addOrder(anotherOrder);

    // then
    verify(vehicleAssemblyLine).newCarCommand(anotherCommandId, A_MODEL_NAME);
  }

  @Test
  public void whenAdvance_thenCallAdvanceOnVehicleAssemblyLine() {
    // when
    linearModelAssemblyLineStrategy.advance();

    // then
    verify(vehicleAssemblyLine).advance();
  }

  @Test
  public void
      givenAnOrderDoneBeingAssembledAndAnotherOrderInQueue_whenAdvance_thenOrderInQueueIsSentToBeAssembled() {
    // given
    setUpAnOrder();
    linearModelAssemblyLineStrategy.addOrder(anOrder);
    when(vehicleAssemblyLine.getBuildStatus(aCommandId))
        .thenReturn(BuildStatus.IN_PROGRESS, BuildStatus.ASSEMBLED);
    setUpAnotherOrder();
    linearModelAssemblyLineStrategy.addOrder(anotherOrder);

    // when
    linearModelAssemblyLineStrategy.advance();

    // then
    verify(vehicleAssemblyLine, times(1)).newCarCommand(anotherCommandId, A_MODEL_NAME);
  }

  @Test
  public void
      givenAnOrderBeingAssembledAndAnotherOrderInQueue_whenAdvance_thenOrderInQueueIsNotSentToBeAssembled() {
    // given
    setUpAnOrder();
    linearModelAssemblyLineStrategy.addOrder(anOrder);
    given(vehicleAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.IN_PROGRESS);

    // when
    linearModelAssemblyLineStrategy.advance();

    // then
    verify(vehicleAssemblyLine, never()).newCarCommand(anotherCommandId, A_MODEL_NAME);
  }

  @Test
  public void givenAnOrderDoneBeingAssembled_whenAdvance_thenNotifyAllModelAssembledObservers() {
    // given
    setUpAnOrder();
    linearModelAssemblyLineStrategy.addOrder(anOrder);
    given(vehicleAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.ASSEMBLED);
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
    int modelTimeToProduce = 2;
    given(aModel.getTimeToProduce()).willReturn(modelTimeToProduce);
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
    int modelTimeToProduce = 4;
    int expectedRemainingTimeToProduce = 2;
    given(aModel.getTimeToProduce()).willReturn(modelTimeToProduce);
    setUpAnOrder();
    linearModelAssemblyLineStrategy.addOrder(anOrder);
    given(vehicleAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.IN_PROGRESS);
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
    given(anOrder.getModel().getTimeToProduce()).willReturn(A_REMAINING_TIME_TO_PRODUCE);
    given(vehicleAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.RECEIVED);
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(anotherOrder.getModel()).willReturn(anotherModel);
    given(anotherOrder.getModel().getTimeToProduce()).willReturn(ANOTHER_REMAINING_TIME_TO_PRODUCE);
    given(otherOrder.getId()).willReturn(OTHER_ORDER_ID);
    given(otherOrder.getModel()).willReturn(otherModel);
    given(otherOrder.getModel().getTimeToProduce()).willReturn(OTHER_REMAINING_TIME_TO_PRODUCE);
    linearModelAssemblyLineStrategy.addOrder(anOrder);
    linearModelAssemblyLineStrategy.addOrder(anotherOrder);
    linearModelAssemblyLineStrategy.addOrder(otherOrder);
    int expectedRemainingTimeToProduce =
        A_REMAINING_TIME_TO_PRODUCE
            + ANOTHER_REMAINING_TIME_TO_PRODUCE
            + OTHER_REMAINING_TIME_TO_PRODUCE;

    // when
    int remainingTimeToProduce =
        linearModelAssemblyLineStrategy.computeRemainingTimeToProduce(OTHER_ORDER_ID);

    // then
    assertThat(remainingTimeToProduce).isEqualTo(expectedRemainingTimeToProduce);
  }

  private void setUpAnOrder() {
    given(anOrder.getId()).willReturn(AN_ORDER_ID);
    given(commandIdFactory.createFromOrderId(AN_ORDER_ID)).willReturn(aCommandId);
    given(aModel.getName()).willReturn(A_MODEL_NAME);
    given(anOrder.getModel()).willReturn(aModel);
  }

  private void setUpAnotherOrder() {
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(commandIdFactory.createFromOrderId(ANOTHER_ORDER_ID)).willReturn(anotherCommandId);
    given(anotherModel.getName()).willReturn(A_MODEL_NAME);
    given(anotherOrder.getModel()).willReturn(anotherModel);
  }
}
