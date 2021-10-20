package ca.ulaval.glo4003.ws.infrastructure.assembly.battery;

import ca.ulaval.glo4003.evulution.car_manufacture.BatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssembledObserver;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.notification.BatteryAssemblyDelayObserver;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.infrastructure.assembly.CommandIdFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinearBatteryAssemblyLineStrategyTest {
  private static final String AN_ID = "fdsnj9203";
  private static final String ANOTHER_ID = "213dmsao4";
  private static final String OTHER_ID = "fdsiom234";
  private static final OrderId AN_ORDER_ID = new OrderId(AN_ID);
  private static final OrderId ANOTHER_ORDER_ID = new OrderId(ANOTHER_ID);
  private static final OrderId OTHER_ORDER_ID = new OrderId(OTHER_ID);
  private static final String A_BATTERY_TYPE = "LONG_DISTANCE";
  private static final ProductionTime A_REMAINING_PRODUCTION_TIME = new ProductionTime(43);
  private static final ProductionTime ANOTHER_REMAINING_PRODUCTION_TIME = new ProductionTime(763);
  private static final ProductionTime OTHER_REMAINING_PRODUCTION_TIME = new ProductionTime(43212);

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
  @Mock private BatteryAssemblyDelayObserver batteryAssemblyDelayObserver;

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
    given(anOrder.getBattery().getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);
    given(batteryAssemblyLine.getBuildStatus(aCommandId))
        .willReturn(BuildStatus.IN_PROGRESS, BuildStatus.ASSEMBLED);
    setUpAnotherOrder();
    given(anotherOrder.getBattery().getProductionTime())
        .willReturn(ANOTHER_REMAINING_PRODUCTION_TIME);
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
    given(anOrder.getBattery().getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
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
    ProductionTime batteryTimeToProduce = new ProductionTime(2);
    given(aBattery.getProductionTime()).willReturn(batteryTimeToProduce);
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
    given(aBattery.getProductionTime()).willReturn(batteryTimeToProduce);
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
    given(anOrder.getBattery().getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    given(batteryAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.RECEIVED);
    given(anotherOrder.getId()).willReturn(ANOTHER_ORDER_ID);
    given(anotherOrder.getBattery()).willReturn(anotherBattery);
    given(anotherOrder.getBattery().getProductionTime())
        .willReturn(ANOTHER_REMAINING_PRODUCTION_TIME);
    given(otherOrder.getId()).willReturn(OTHER_ORDER_ID);
    given(otherOrder.getBattery()).willReturn(otherBattery);
    given(otherOrder.getBattery().getProductionTime()).willReturn(OTHER_REMAINING_PRODUCTION_TIME);
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
    setUpAnOrder();

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
    given(anotherOrder.getBattery()).willReturn(anotherBattery);
    given(anOrder.getBattery().getProductionTime()).willReturn(A_REMAINING_PRODUCTION_TIME);
    given(anotherOrder.getBattery().getProductionTime())
        .willReturn(ANOTHER_REMAINING_PRODUCTION_TIME);
    given(batteryAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.IN_PROGRESS);
    linearBatteryAssemblyLineStrategy.addOrder(anOrder);
    linearBatteryAssemblyLineStrategy.register(batteryAssemblyDelayObserver);

    // when
    linearBatteryAssemblyLineStrategy.addOrder(anotherOrder);

    // then
    verify(batteryAssemblyDelayObserver).listenBatteryAssemblyDelay(anotherOrder);
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
