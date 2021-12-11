package ca.ulaval.glo4003.ws.domain.warehouse.model.strategy;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.manufacturer.model.ModelManufacturer;
import ca.ulaval.glo4003.ws.domain.notification.ModelOrderDelayObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelInventory;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelInventoryObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import ca.ulaval.glo4003.ws.fixture.ModelOrderBuilder;
import ca.ulaval.glo4003.ws.fixture.OrderBuilder;
import ca.ulaval.glo4003.ws.infrastructure.manufacturer.model.exception.InvalidModelQuantityInQueueException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccumulateModelWarehouseStrategyTest {
  private static final AssemblyTime ASSEMBLY_TIME_OF_ONE_WEEK = new AssemblyTime(1);
  private static final AssemblyTime ASSEMBLY_TIME_OF_TWO_WEEKS = new AssemblyTime(2);
  private static final AssemblyTime AN_ASSEMBLY_TIME = new AssemblyTime(23);
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

  @Mock private ModelManufacturer modelManufacturer;
  @Mock private ModelInventory modelInventory;
  @Mock private ModelInventoryObserver modelInventoryObserver;
  @Mock private ModelInventoryObserver anotherModelAssembledObserver;
  @Mock private ModelOrderDelayObserver modelOrderDelayObserver;

  private AccumulateModelWarehouseStrategy accumulateModelWarehouseStrategy;

  @BeforeEach
  public void setUp() {
    accumulateModelWarehouseStrategy =
        new AccumulateModelWarehouseStrategy(
            MODEL_ASSEMBLY_ORDER, modelManufacturer, modelInventory);
    accumulateModelWarehouseStrategy.register(modelInventoryObserver);
    accumulateModelWarehouseStrategy.register(anotherModelAssembledObserver);
    accumulateModelWarehouseStrategy.register(modelOrderDelayObserver);
  }

  @Test
  public void whenAddOrder_thenCheckIfModelIsInStock() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(0));
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
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(0));
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
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(0));
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
  public void givenModelNotInStock_whenAddOrder_thenOrderRemainingAssemblyTimeIsSet() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(123));
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(false);

    // when
    accumulateModelWarehouseStrategy.addOrder(anOrder);

    // then
    assertThat(anOrder.getAssemblyDelay().inWeeks()).isGreaterThan(0);
  }

  @Test
  public void givenModelNotInStock_whenAddOrder_thenNotifyModelOrderDelay() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(0));
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(false);

    // when
    accumulateModelWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelOrderDelayObserver).listenModelOrderDelay(anOrder);
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
      givenAnOrderWaitingForModel_whenListenToModelAssembled_thenModelIsNotAddedToInventory() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(0));
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
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(0));
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
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(0));
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
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(0));
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
  public void givenNoOrderInQueue_whenAddOrder_thenOrderHasDelayEqualToAssemblyTime() {
    // given
    Order anOrder = new OrderBuilder().build();
    AssemblyTime expectedTimeToProduce = AN_ASSEMBLY_TIME;
    given(
            modelManufacturer.computeTimeToProduceQuantityOfModel(
                1, anOrder.getModelOrder().getModelType()))
        .willReturn(expectedTimeToProduce);

    // when
    accumulateModelWarehouseStrategy.addOrder(anOrder);

    // then
    assertThat(anOrder.getAssemblyDelay()).isEqualTo(expectedTimeToProduce);
  }

  @Test
  public void givenOneOrderInQueue_whenAddNewOrder_thenOrderHasDelayOfTotalAssemblyTime() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(0));
    Order anOrder = new OrderBuilder().build();
    String modelType = anOrder.getModelOrder().getModelType();
    accumulateModelWarehouseStrategy.addOrder(anOrder);
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(1, modelType))
        .willReturn(AN_ASSEMBLY_TIME);

    // when
    accumulateModelWarehouseStrategy.addOrder(anOrder);

    // then
    assertThat(anOrder.getAssemblyDelay()).isEqualTo(AN_ASSEMBLY_TIME);
  }

  @Test
  public void
      givenTwoOrdersOfDifferentTypeInQueue_whenAddNewOrder_thenOrderHasDelayOfTotalAssemblyTime() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(0));
    Order anOrder = new OrderBuilder().withModelType("a type").build();
    accumulateModelWarehouseStrategy.addOrder(anOrder);
    Order anotherOrder = new OrderBuilder().withModelType("another type").build();
    String modelType = anotherOrder.getModelOrder().getModelType();
    accumulateModelWarehouseStrategy.addOrder(anotherOrder);
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(1, modelType))
        .willReturn(AN_ASSEMBLY_TIME);

    // when
    accumulateModelWarehouseStrategy.addOrder(anotherOrder);

    // then
    assertThat(anotherOrder.getAssemblyDelay()).isEqualTo(AN_ASSEMBLY_TIME);
  }

  @Test
  public void
      givenTwoOrdersOfSameTypeInQueue_whenAddNewOrder_thenOrderHasDelayOfTotalAssemblyTime() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(0));
    Order anOrder = new OrderBuilder().withModelType("a type").build();
    accumulateModelWarehouseStrategy.addOrder(anOrder);
    Order anotherOrder = new OrderBuilder().withModelType("a type").build();
    String modelType = anotherOrder.getModelOrder().getModelType();
    accumulateModelWarehouseStrategy.addOrder(anotherOrder);
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(2, modelType))
        .willReturn(AN_ASSEMBLY_TIME);

    // when
    accumulateModelWarehouseStrategy.addOrder(anotherOrder);

    // then
    assertThat(anotherOrder.getAssemblyDelay()).isEqualTo(AN_ASSEMBLY_TIME);
  }

  @Test
  public void givenComputeRemainingTimeWithWrongModelQuantity_whenAddOrder_thenDoNotNotifyDelay() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willThrow(new InvalidModelQuantityInQueueException());
    Order anOrder = new OrderBuilder().withModelType("a type").build();

    // when
    accumulateModelWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelOrderDelayObserver, never()).listenModelOrderDelay(anOrder);
  }

  @Test
  public void
      givenComputeRemainingTimeWithWrongModelQuantity_whenAddOrder_thenDoNotAddDelayToOrder() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willThrow(new InvalidModelQuantityInQueueException());
    Order anOrder = new OrderBuilder().withModelType("a type").build();
    AssemblyTime noDelay = new AssemblyTime(0);

    // when
    accumulateModelWarehouseStrategy.addOrder(anOrder);

    // then
    assertThat(anOrder.getAssemblyDelay()).isEqualTo(noDelay);
  }

  private Order createAnOrderWithModelType(String modelType) {
    ModelOrder aModelOrder = new ModelOrderBuilder().withModelName(modelType).build();
    return new OrderBuilder().withModelOrder(aModelOrder).build();
  }
}
