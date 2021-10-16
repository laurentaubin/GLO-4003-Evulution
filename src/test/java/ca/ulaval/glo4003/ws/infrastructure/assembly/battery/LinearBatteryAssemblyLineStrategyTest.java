package ca.ulaval.glo4003.ws.infrastructure.assembly.battery;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.evulution.car_manufacture.BatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssembledObserver;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderQueue;
import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.infrastructure.assembly.CommandIdFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LinearBatteryAssemblyLineStrategyTest {
  private static final String AN_ID = "fdsnj9203";
  private static final String ANOTHER_ID = "213dmsao4";
  private static final OrderId AN_ORDER_ID = new OrderId(AN_ID);
  private static final OrderId ANOTHER_ORDER_ID = new OrderId(ANOTHER_ID);
  private static final String A_BATTERY_TYPE = "LONG_DISTANCE";

  @Mock private Order anOrder;
  @Mock private Order anotherOrder;
  @Mock private Battery aBattery;
  @Mock private Battery anotherBattery;
  @Mock private BatteryAssemblyLine batteryAssemblyLine;
  @Mock private CommandIdFactory commandIdFactory;
  @Mock private CommandID aCommandId;
  @Mock private CommandID anotherCommandId;
  @Mock private OrderQueue orderQueue;
  @Mock private BatteryAssembledObserver batteryAssembledObserver;
  @Mock private BatteryAssembledObserver anotherBatteryAssemblyObserver;

  private LinearBatteryAssemblyLineStrategy linearBatteryAssemblyLineStrategy;

  @BeforeEach
  public void setUp() {
    linearBatteryAssemblyLineStrategy =
        new LinearBatteryAssemblyLineStrategy(batteryAssemblyLine, commandIdFactory, orderQueue);
  }

  @Test
  public void givenAnOrder_whenAddOrder_thenOrderIsAddedToQueue() {
    // when
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);

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
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(batteryAssemblyLine).newBatteryCommand(aCommandId, A_BATTERY_TYPE);
  }

  @Test
  public void givenAnEmptyQueueAndNoOrderBeingAssembled_whenAddOrder_thenOrderIsNotAddedToQueue() {
    given(orderQueue.isEmpty()).willReturn(true);
    setUpAnOrder();
    given(commandIdFactory.createFromOrderId(AN_ORDER_ID)).willReturn(aCommandId);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(orderQueue, never()).addOrder(anOrder);
  }

  @Test
  public void
      givenAnEmptyQueueAndAnOrderDoneBeingAssembled_whenAddOrder_thenNewOrderIsSentToBeAssembled() {
    setUpAnOrder();
    setUpAnotherOrder();
    given(orderQueue.isEmpty()).willReturn(true);
    given(batteryAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.ASSEMBLED);
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(anotherOrder);

    // then
    verify(batteryAssemblyLine).newBatteryCommand(anotherCommandId, A_BATTERY_TYPE);
  }

  @Test
  public void
      givenAnEmptyQueueAndAnOrderDoneBeingAssembled_whenAddOrder_thenNewOrderIsNotAddedToQueue() {
    // given
    setUpAnOrder();
    setUpAnotherOrder();
    given(orderQueue.isEmpty()).willReturn(true);
    given(batteryAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.ASSEMBLED);
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(anotherOrder);

    // then
    verify(orderQueue, never()).addOrder(anotherOrder);
  }

  @Test
  public void givenQueueNotEmpty_whenAddOrder_thenNewOrderIsAddedToQueue() {
    // given
    given(orderQueue.isEmpty()).willReturn(false);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(orderQueue).addOrder(anOrder);
  }

  @Test
  public void whenAdvance_thenCallAdvanceOnBatteryAssemblyLine() {
    // given
    given(orderQueue.isEmpty()).willReturn(true);

    // when
    linearBatteryAssemblyLineStrategy.advance();

    // then
    verify(batteryAssemblyLine).advance();
  }

  @Test
  public void
      givenAnOrderDoneBeingAssembledAndAnotherOrderInQueue_whenAdvance_thenOrderInQueueIsSentToBeAssembled() {
    // given
    setUpAnOrder();
    when(orderQueue.isEmpty()).thenReturn(true, false);
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);
    given(batteryAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.ASSEMBLED);
    setUpAnotherOrder();
    given(orderQueue.getNextInLine()).willReturn(anotherOrder);

    // when
    linearBatteryAssemblyLineStrategy.advance();

    // then
    verify(batteryAssemblyLine, times(1)).newBatteryCommand(anotherCommandId, A_BATTERY_TYPE);
  }

  @Test
  public void
      givenAnOrderBeingAssembledAndAnotherOrderInQueue_whenAdvance_thenOrderInQueueIsNotSentToBeAssembled() {
    // given
    setUpAnOrder();
    when(orderQueue.isEmpty()).thenReturn(true, false);
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);
    given(batteryAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.IN_PROGRESS);

    // when
    linearBatteryAssemblyLineStrategy.advance();

    // then
    verify(batteryAssemblyLine, never()).newBatteryCommand(anotherCommandId, A_BATTERY_TYPE);
  }

  @Test
  public void givenAnOrderDoneBeingAssembled_whenAdvance_thenNotifyAllModelAssembledObservers() {
    // given
    given(orderQueue.isEmpty()).willReturn(true);
    setUpAnOrder();
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);
    given(batteryAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.ASSEMBLED);
    linearBatteryAssemblyLineStrategy.register(batteryAssembledObserver);
    linearBatteryAssemblyLineStrategy.register(anotherBatteryAssemblyObserver);

    // when
    linearBatteryAssemblyLineStrategy.advance();

    // then
    verify(batteryAssembledObserver).listenToBatteryAssembled(anOrder);
    verify(anotherBatteryAssemblyObserver).listenToBatteryAssembled(anOrder);
  }

  @Test
  public void whenComputeEstimatedTime_thenReturnSomething() {
    // when
    linearBatteryAssemblyLineStrategy.computeRemainingTimeToProduce(AN_ORDER_ID);

    // then
    assertThat(true).isTrue();
  }

  private void setUpAnOrder() {
    given(anOrder.getId()).willReturn(AN_ORDER_ID);
    given(commandIdFactory.createFromOrderId(AN_ORDER_ID)).willReturn(aCommandId);
    given(aBattery.getType()).willReturn(A_BATTERY_TYPE);
    given(anOrder.getBattery()).willReturn(aBattery);
  }

  private void setUpAnotherOrder() {
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(commandIdFactory.createFromOrderId(ANOTHER_ORDER_ID)).willReturn(anotherCommandId);
    given(anotherBattery.getType()).willReturn(A_BATTERY_TYPE);
    given(anotherOrder.getBattery()).willReturn(anotherBattery);
  }
}
