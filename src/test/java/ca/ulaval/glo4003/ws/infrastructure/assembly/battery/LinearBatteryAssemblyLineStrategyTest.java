package ca.ulaval.glo4003.ws.infrastructure.assembly.battery;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4003.evulution.car_manufacture.BatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssembledObserver;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
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
  private static final String OTHER_ID = "fdsiom234";
  private static final OrderId AN_ORDER_ID = new OrderId(AN_ID);
  private static final OrderId ANOTHER_ORDER_ID = new OrderId(ANOTHER_ID);
  private static final OrderId OTHER_ORDER_ID = new OrderId(OTHER_ID);
  private static final String A_BATTERY_TYPE = "LONG_DISTANCE";
  private static final int A_REMAINING_TIME_TO_PRODUCE = 43;
  private static final int ANOTHER_REMAINING_TIME_TO_PRODUCE = 763;
  private static final int OTHER_REMAINING_TIME_TO_PRODUCE = 43212;

  @Mock private Order anOrder;
  @Mock private Order anotherOrder;
  @Mock private Order otherOrder;
  @Mock private Battery aBattery;
  @Mock private Battery anotherBattery;
  @Mock private Battery otherBattery;
  @Mock private BatteryAssemblyLine batteryAssemblyLine;
  @Mock private CommandIdFactory commandIdFactory;
  @Mock private CommandID aCommandId;
  @Mock private CommandID anotherCommandId;
  @Mock private BatteryAssembledObserver batteryAssembledObserver;
  @Mock private BatteryAssembledObserver anotherBatteryAssemblyObserver;

  private LinearBatteryAssemblyLineStrategy linearBatteryAssemblyLineStrategy;

  @BeforeEach
  public void setUp() {
    linearBatteryAssemblyLineStrategy =
        new LinearBatteryAssemblyLineStrategy(batteryAssemblyLine, commandIdFactory);
  }

  @Test
  public void
      givenAnEmptyQueueAndNoOrderBeingAssembled_whenAddOrder_thenOrderIsSentToBeAssembled() {
    // given
    setUpAnOrder();
    given(commandIdFactory.createFromOrderId(AN_ORDER_ID)).willReturn(aCommandId);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(batteryAssemblyLine).newBatteryCommand(aCommandId, A_BATTERY_TYPE);
  }

  @Test
  public void
      givenAnEmptyQueueAndAnOrderDoneBeingAssembled_whenAddOrder_thenNewOrderIsSentToBeAssembled() {
    setUpAnOrder();
    setUpAnotherOrder();
    given(batteryAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.ASSEMBLED);
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(anotherOrder);

    // then
    verify(batteryAssemblyLine).newBatteryCommand(anotherCommandId, A_BATTERY_TYPE);
  }

  @Test
  public void whenAdvance_thenCallAdvanceOnBatteryAssemblyLine() {
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
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);
    when(batteryAssemblyLine.getBuildStatus(aCommandId))
        .thenReturn(BuildStatus.IN_PROGRESS, BuildStatus.ASSEMBLED);
    setUpAnotherOrder();
    linearBatteryAssemblyLineStrategy.addOrder(anotherOrder);

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
  public void
      givenAnOrderSentToBeAssembled_whenComputeEstimatedTime_thenReturnTheBatteryRemainingTimeToProduce() {
    // given
    int batteryTimeToProduce = 2;
    given(aBattery.getTimeToProduce()).willReturn(batteryTimeToProduce);
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
    int batteryTimeToProduce = 4;
    int expectedRemainingTimeToProduce = 2;
    given(aBattery.getTimeToProduce()).willReturn(batteryTimeToProduce);
    setUpAnOrder();
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);
    given(batteryAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.IN_PROGRESS);
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
    given(anOrder.getBattery().getTimeToProduce()).willReturn(A_REMAINING_TIME_TO_PRODUCE);
    given(batteryAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.RECEIVED);
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(anotherOrder.getBattery()).willReturn(anotherBattery);
    given(anotherOrder.getBattery().getTimeToProduce())
        .willReturn(ANOTHER_REMAINING_TIME_TO_PRODUCE);
    given(otherOrder.getId()).willReturn(OTHER_ORDER_ID);
    given(otherOrder.getBattery()).willReturn(otherBattery);
    given(otherOrder.getBattery().getTimeToProduce()).willReturn(OTHER_REMAINING_TIME_TO_PRODUCE);
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);
    linearBatteryAssemblyLineStrategy.addOrder(anotherOrder);
    linearBatteryAssemblyLineStrategy.addOrder(otherOrder);
    int expectedRemainingTimeToProduce =
        A_REMAINING_TIME_TO_PRODUCE
            + ANOTHER_REMAINING_TIME_TO_PRODUCE
            + OTHER_REMAINING_TIME_TO_PRODUCE;

    // when
    int remainingTimeToProduce =
        linearBatteryAssemblyLineStrategy.computeRemainingTimeToProduce(OTHER_ORDER_ID);

    // then
    assertThat(remainingTimeToProduce).isEqualTo(expectedRemainingTimeToProduce);
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
