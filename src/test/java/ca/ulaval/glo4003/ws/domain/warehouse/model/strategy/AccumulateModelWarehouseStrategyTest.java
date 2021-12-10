package ca.ulaval.glo4003.ws.domain.warehouse.model.strategy;

import ca.ulaval.glo4003.ws.domain.manufacturer.model.ModelManufacturer;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelInventory;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelInventoryObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import ca.ulaval.glo4003.ws.fixture.ModelOrderBuilder;
import ca.ulaval.glo4003.ws.fixture.OrderBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccumulateModelWarehouseStrategyTest {
  private static final AssemblyTime ASSEMBLY_TIME_OF_ONE_WEEK = new AssemblyTime(1);
  private static final AssemblyTime ASSEMBLY_TIME_OF_TWO_WEEKS = new AssemblyTime(2);
  private static final String FIRST_MODEL_TYPE = "first type";
  private static final ModelOrder FIRST_MODEL_IN_CYCLE =
      new ModelOrderBuilder()
          .withModelName(FIRST_MODEL_TYPE)
          .withAssemblyTime(ASSEMBLY_TIME_OF_ONE_WEEK)
          .build();
  private static final String SECOND_MODEL_TYPE = "second type";
  private static final ModelOrder SECOND_MODEL_IN_CYCLE =
      new ModelOrderBuilder()
          .withModelName(SECOND_MODEL_TYPE)
          .withAssemblyTime(ASSEMBLY_TIME_OF_ONE_WEEK)
          .build();
  private static final String THIRD_MODEL_TYPE = "third type";
  private static final ModelOrder THIRD_MODEL_IN_CYCLE =
      new ModelOrderBuilder()
          .withModelName(THIRD_MODEL_TYPE)
          .withAssemblyTime(ASSEMBLY_TIME_OF_TWO_WEEKS)
          .build();
  private static final List<ModelOrder> MODEL_ASSEMBLY_ORDER =
      List.of(FIRST_MODEL_IN_CYCLE, SECOND_MODEL_IN_CYCLE, THIRD_MODEL_IN_CYCLE);
  private static final OrderId FIRST_ORDER_ID = new OrderId("firstOrderId");
  private static final ModelOrder FIRST_MODEL_ORDER =
      new ModelOrderBuilder()
          .withOrderId(FIRST_ORDER_ID)
          .withModelName(FIRST_MODEL_TYPE)
          .withAssemblyTime(ASSEMBLY_TIME_OF_ONE_WEEK)
          .build();

  @Mock private ModelManufacturer modelManufacturer;
  @Mock private ModelInventory modelInventory;
  @Mock private ModelInventoryObserver modelInventoryObserver;
  @Mock private ModelInventoryObserver anotherModelAssembledObserver;

  private AccumulateModelWarehouseStrategy accumulateModelWarehouseStrategy;

  @BeforeEach
  public void setUp() {
    accumulateModelWarehouseStrategy =
        new AccumulateModelWarehouseStrategy(
            MODEL_ASSEMBLY_ORDER, modelManufacturer, modelInventory);
    accumulateModelWarehouseStrategy.register(modelInventoryObserver);
    accumulateModelWarehouseStrategy.register(anotherModelAssembledObserver);
  }

  @Test
  public void whenAddOrder_thenCheckIfModelIsInStock() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);

    // when
    accumulateModelWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelInventory).isInStock(FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelIsInStock_whenAddOrder_thenModelIsRemovedFromInventory() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(true);

    // when
    accumulateModelWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelInventory).removeOne(FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelIsNotInStock_whenAddOrder_thenModelIsNotTakenFromInventory() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(false);

    // when
    accumulateModelWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelInventory, never()).removeOne(FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelIsInStock_whenAddOrder_thenNotifyAllModelInventoryObservers() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(true);

    // when
    accumulateModelWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelInventoryObserver).listenToModelInStock(anOrder);
    verify(anotherModelAssembledObserver).listenToModelInStock(anOrder);
  }

  @Test
  public void givenModelIsNotInStock_whenAddOrder_thenModelAssembledObserversAreNotNotify() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(false);
    accumulateModelWarehouseStrategy.register(modelInventoryObserver);
    accumulateModelWarehouseStrategy.register(anotherModelAssembledObserver);

    // when
    accumulateModelWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelInventoryObserver, never()).listenToModelInStock(anOrder);
    verify(anotherModelAssembledObserver, never()).listenToModelInStock(anOrder);
  }

  @Test
  public void whenCreate_thenSendFirstModelOrderInCycleToBeAssembled() {
    // then
    verify(modelManufacturer).addOrder(FIRST_MODEL_IN_CYCLE);
  }

  @Test
  public void whenListenToModelAssembled_thenSendModelOrdersInCycleToBeAssembledInOrderAndInLoop() {
    // when
    accumulateModelWarehouseStrategy.listenToModelAssembled(FIRST_MODEL_IN_CYCLE);
    accumulateModelWarehouseStrategy.listenToModelAssembled(SECOND_MODEL_IN_CYCLE);
    accumulateModelWarehouseStrategy.listenToModelAssembled(THIRD_MODEL_IN_CYCLE);

    // then
    InOrder inOrder = Mockito.inOrder(modelManufacturer);
    inOrder.verify(modelManufacturer).addOrder(FIRST_MODEL_IN_CYCLE);
    inOrder.verify(modelManufacturer).addOrder(SECOND_MODEL_IN_CYCLE);
    inOrder.verify(modelManufacturer).addOrder(THIRD_MODEL_IN_CYCLE);
    inOrder.verify(modelManufacturer).addOrder(FIRST_MODEL_IN_CYCLE);
  }

  @Test
  public void givenNoOrderWaitingForModel_whenListenToModelAssembled_thenModelIsAddedToInventory() {
    // when
    accumulateModelWarehouseStrategy.listenToModelAssembled(FIRST_MODEL_IN_CYCLE);

    // then
    verify(modelInventory).addOne(FIRST_MODEL_IN_CYCLE.getModelType());
  }

  @Test
  public void
      givenAnOrderWaitingForModel__whenListenToModelAssembled_thenModelIsNotAddedToInventory() {
    // given
    ModelOrder modelOrder =
        new ModelOrderBuilder().withModelName(FIRST_MODEL_IN_CYCLE.getModelType()).build();
    Order order = new OrderBuilder().withModelOrder(modelOrder).build();
    accumulateModelWarehouseStrategy.addOrder(order);

    // when
    accumulateModelWarehouseStrategy.listenToModelAssembled(FIRST_MODEL_IN_CYCLE);

    // then
    verify(modelInventory, never()).addOne(FIRST_MODEL_IN_CYCLE.getModelType());
  }

  @Test
  public void
      givenAnOrderWaitingForModel_whenListenToModelAssembled_thenNotifyBatteryOrderIsInStockForOrder() {
    // given
    ModelOrder modelOrder =
        new ModelOrderBuilder().withModelName(FIRST_MODEL_IN_CYCLE.getModelType()).build();
    Order order = new OrderBuilder().withModelOrder(modelOrder).build();
    accumulateModelWarehouseStrategy.addOrder(order);

    // when
    accumulateModelWarehouseStrategy.listenToModelAssembled(FIRST_MODEL_IN_CYCLE);

    // then
    verify(modelInventoryObserver).listenToModelInStock(order);
  }

  @Test
  public void
      givenManyOrdersWaitingForModelAndManyModelsAssembled_whenListenToModelAssembled_thenNotifyOrdersWaitingForBatteryInOrder() {
    // given
    ModelOrder modelOrder =
        new ModelOrderBuilder().withModelName(FIRST_MODEL_IN_CYCLE.getModelType()).build();
    Order order = new OrderBuilder().withModelOrder(modelOrder).build();
    Order anotherOrder = new OrderBuilder().withModelOrder(modelOrder).build();
    accumulateModelWarehouseStrategy.addOrder(order);
    accumulateModelWarehouseStrategy.addOrder(anotherOrder);

    // when
    accumulateModelWarehouseStrategy.listenToModelAssembled(FIRST_MODEL_IN_CYCLE);
    accumulateModelWarehouseStrategy.listenToModelAssembled(FIRST_MODEL_IN_CYCLE);

    // then
    InOrder inOrder = Mockito.inOrder(modelInventoryObserver);
    inOrder.verify(modelInventoryObserver).listenToModelInStock(order);
    inOrder.verify(modelInventoryObserver).listenToModelInStock(anotherOrder);
  }

  @Test
  public void
      givenManyOrdersWaitingForModel_whenListenToModelAssembled_thenOnlyNotifyFirstOrderWaitingForBatteryOrder() {
    // given
    ModelOrder modelOrder =
        new ModelOrderBuilder().withModelName(FIRST_MODEL_IN_CYCLE.getModelType()).build();
    Order order = new OrderBuilder().withModelOrder(modelOrder).build();
    Order anotherOrder = new OrderBuilder().withModelOrder(modelOrder).build();
    accumulateModelWarehouseStrategy.addOrder(order);
    accumulateModelWarehouseStrategy.addOrder(anotherOrder);

    // when
    accumulateModelWarehouseStrategy.listenToModelAssembled(FIRST_MODEL_IN_CYCLE);

    // then
    verify(modelInventoryObserver).listenToModelInStock(order);
    verify(modelInventoryObserver, never()).listenToModelInStock(anotherOrder);
  }

  @Test
  public void
      givenOrderAddedWithModelNotInStock_whenComputeRemainingTimeToProduce_thenReturnModelOrderAssemblyTime() {
    // given
    Order order = createAnOrderWithModel(FIRST_MODEL_ORDER);
    accumulateModelWarehouseStrategy.addOrder(order);
    AssemblyTime expectedAssemblyTime = order.getModelOrder().getAssemblyTime();

    // when
    AssemblyTime actualAssemblyTime =
        accumulateModelWarehouseStrategy.computeRemainingTimeToProduce(order.getId());

    // then
    assertThat(actualAssemblyTime).isEqualTo(expectedAssemblyTime);
  }

  //  @Test
  //  public void givenOrderAndModelIsInStock_whenComputeRemainingTimeToProduce_thenReturnNoDelay()
  // {
  //    // given
  //    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(true);
  //    Order order = createAnOrderWithModel(FIRST_MODEL_ORDER);
  //    accumulateModelWarehouseStrategy.addOrder(order);
  //    AssemblyTime expectedAssemblyTime = new AssemblyTime(0);
  //
  //    // when
  //    AssemblyTime actualAssemblyTime =
  //        accumulateModelWarehouseStrategy.computeRemainingTimeToProduce(order.getId());
  //
  //    // then
  //    assertThat(actualAssemblyTime).isEqualTo(expectedAssemblyTime);
  //  }
  //
  //  @Test
  //  public void
  //
  // givenTwoOrdersAddedWithModelsFollowingCycle_whenComputeRemainingTimeToProduceOfSecondOrder_thenReturnSumOfModelAssemblyTimes() {
  //    // given
  //    Order firstOrder = createAnOrderWithModel(FIRST_MODEL_ORDER);
  //    Order secondOrder = createAnOrderWithModel(SECOND_MODEL_ORDER);
  //    addOrdersToAssemblyLine(List.of(firstOrder, secondOrder));
  //    AssemblyTime expectedAssemblyTime =
  //        FIRST_MODEL_IN_CYCLE.getAssemblyTime().add(SECOND_MODEL_IN_CYCLE.getAssemblyTime());
  //
  //    // when
  //    AssemblyTime actualAssemblyTime =
  //        accumulateModelWarehouseStrategy.computeRemainingTimeToProduce(secondOrder.getId());
  //
  //    // then
  //    assertThat(actualAssemblyTime).isEqualTo(expectedAssemblyTime);
  //  }
  //
  //  @Test
  //  public void
  //
  // givenTwoOrdersAddedWithFirstAndThirdModelsOfCycle_whenComputeRemainingTimeToProduceOfSecondOrder_thenReturnSumOfFirstSecondAndThirdModelsAssemblyTime() {
  //    // given
  //    Order firstOrder = createAnOrderWithModel(FIRST_MODEL_ORDER);
  //    Order secondOrder = createAnOrderWithModel(THIRD_MODEL_ORDER);
  //    addOrdersToAssemblyLine(List.of(firstOrder, secondOrder));
  //    AssemblyTime expectedAssemblyTime =
  //        FIRST_MODEL_IN_CYCLE
  //            .getAssemblyTime()
  //            .add(SECOND_MODEL_IN_CYCLE.getAssemblyTime())
  //            .add(THIRD_MODEL_IN_CYCLE.getAssemblyTime());
  //
  //    // when
  //    AssemblyTime actualAssemblyTime =
  //        accumulateModelWarehouseStrategy.computeRemainingTimeToProduce(secondOrder.getId());
  //
  //    // then
  //    assertThat(actualAssemblyTime).isEqualTo(expectedAssemblyTime);
  //  }
  //
  //  @Test
  //  public void
  //
  // givenTwoOrdersAddedWithFirstModelsOfCycle_whenComputeRemainingTimeToProduceOfSecondOrder_thenReturnSumOfModelAndWholeCycleAssemblyTime() {
  //    // given
  //    Order firstOrder = createAnOrderWithModel(FIRST_MODEL_ORDER);
  //    Order secondOrder = createAnOrderWithModel(FIRST_MODEL_ORDER);
  //    addOrdersToAssemblyLine(List.of(firstOrder, secondOrder));
  //    AssemblyTime expectedAssemblyTime =
  //        FIRST_MODEL_IN_CYCLE
  //            .getAssemblyTime()
  //            .add(SECOND_MODEL_IN_CYCLE.getAssemblyTime())
  //            .add(
  //
  // THIRD_MODEL_IN_CYCLE.getAssemblyTime().add(FIRST_MODEL_IN_CYCLE.getAssemblyTime()));
  //
  //    // when
  //    AssemblyTime actualAssemblyTime =
  //        accumulateModelWarehouseStrategy.computeRemainingTimeToProduce(secondOrder.getId());
  //
  //    // then
  //    assertThat(actualAssemblyTime).isEqualTo(expectedAssemblyTime);
  //  }
  //
  //  @Test
  //  public void givenOrdersInQueue_whenGetActiveOrders_thenReturnOrders() {
  //    // given
  //    Order firstOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
  //    Order secondOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
  //    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(false);
  //    addOrdersToAssemblyLine(List.of(firstOrder, secondOrder));
  //
  //    // when
  //    List<Order> ordersInQueue = accumulateModelWarehouseStrategy.getActiveOrders();
  //
  //    // then
  //    assertThat(ordersInQueue).containsExactly(firstOrder, secondOrder);
  //  }

  private Order createAnOrderWithModelType(String modelType) {
    ModelOrder aModelOrder = new ModelOrderBuilder().withModelName(modelType).build();
    return new OrderBuilder().withModelOrder(aModelOrder).build();
  }

  private Order createAnOrderWithModel(ModelOrder modelOrder) {
    return new OrderBuilder().withModelOrder(modelOrder).build();
  }

  private void addOrdersToAssemblyLine(List<Order> orders) {
    for (Order order : orders) {
      accumulateModelWarehouseStrategy.addOrder(order);
    }
  }
}
