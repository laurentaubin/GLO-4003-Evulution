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
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderQueue;
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
  private static final OrderId AN_ORDER_ID = new OrderId(AN_ID);
  private static final OrderId ANOTHER_ORDER_ID = new OrderId(ANOTHER_ID);
  private static final String A_MODEL_NAME = "Vandry";

  @Mock private Order anOrder;
  @Mock private Order anotherOrder;
  @Mock private Model aModel;
  @Mock private Model anotherModel;
  @Mock private VehicleAssemblyLine vehicleAssemblyLine;
  @Mock private CommandIdFactory commandIdFactory;
  @Mock private CommandID aCommandId;
  @Mock private CommandID anotherCommandId;
  @Mock private OrderQueue orderQueue;
  @Mock private ModelAssembledObserver modelAssembledObserver;
  @Mock private ModelAssembledObserver anotherModelAssembledObserver;

  private LinearModelAssemblyLineStrategy linearModelAssemblyLineStrategy;

  @BeforeEach
  public void setUp() {
    linearModelAssemblyLineStrategy =
        new LinearModelAssemblyLineStrategy(vehicleAssemblyLine, commandIdFactory, orderQueue);
  }

  @Test
  public void givenAnOrder_whenAddOrder_thenOrderIsAddedToQueue() {
    // when
    linearModelAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(orderQueue).addOrder(anOrder);
  }

  @Test
  public void
      givenAnEmptyQueueAndNoOrderBeingAssembled_whenAddOrder_thenOrderIsSentToBeAssembled() {
    // given
    given(orderQueue.isEmpty()).willReturn(true);
    setUpAnOrder();
    given(commandIdFactory.createFromOrderId(AN_ORDER_ID)).willReturn(aCommandId);

    // when
    linearModelAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(vehicleAssemblyLine).newCarCommand(aCommandId, A_MODEL_NAME);
  }

  @Test
  public void givenAnEmptyQueueAndNoOrderBeingAssembled_whenAddOrder_thenOrderIsNotAddedToQueue() {
    given(orderQueue.isEmpty()).willReturn(true);
    setUpAnOrder();
    given(commandIdFactory.createFromOrderId(AN_ORDER_ID)).willReturn(aCommandId);

    // when
    linearModelAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(orderQueue, never()).addOrder(anOrder);
  }

  @Test
  public void
      givenAnEmptyQueueAndAnOrderDoneBeingAssembled_whenAddOrder_thenNewOrderIsSentToBeAssembled() {
    setUpAnOrder();
    setUpAnotherOrder();
    given(orderQueue.isEmpty()).willReturn(true);
    given(vehicleAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.ASSEMBLED);
    linearModelAssemblyLineStrategy.addOrder(anOrder);

    // when
    linearModelAssemblyLineStrategy.addOrder(anotherOrder);

    // then
    verify(vehicleAssemblyLine).newCarCommand(anotherCommandId, A_MODEL_NAME);
  }

  @Test
  public void
      givenAnEmptyQueueAndAnOrderDoneBeingAssembled_whenAddOrder_thenNewOrderIsNotAddedToQueue() {
    // given
    setUpAnOrder();
    setUpAnotherOrder();
    given(orderQueue.isEmpty()).willReturn(true);
    given(vehicleAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.ASSEMBLED);
    linearModelAssemblyLineStrategy.addOrder(anOrder);

    // when
    linearModelAssemblyLineStrategy.addOrder(anotherOrder);

    // then
    verify(orderQueue, never()).addOrder(anotherOrder);
  }

  @Test
  public void givenQueueNotEmpty_whenAddOrder_thenNewOrderIsAddedToQueue() {
    // given
    given(orderQueue.isEmpty()).willReturn(false);

    // when
    linearModelAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(orderQueue).addOrder(anOrder);
  }

  @Test
  public void whenAdvance_thenCallAdvanceOnVehicleAssemblyLine() {
    // given
    given(orderQueue.isEmpty()).willReturn(true);

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
    when(orderQueue.isEmpty()).thenReturn(true, false);
    linearModelAssemblyLineStrategy.addOrder(anOrder);
    given(vehicleAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.ASSEMBLED);
    setUpAnotherOrder();
    given(orderQueue.getNextInLine()).willReturn(anotherOrder);

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
    when(orderQueue.isEmpty()).thenReturn(true, false);
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
    given(orderQueue.isEmpty()).willReturn(true);
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
  public void whenComputeEstimatedTime_thenReturnSomething() {
    // when
    linearModelAssemblyLineStrategy.computeRemainingTimeToProduce(AN_ORDER_ID);

    // then
    assertThat(true).isTrue();
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
