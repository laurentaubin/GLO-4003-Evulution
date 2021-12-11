package ca.ulaval.glo4003.ws.domain.warehouse.model.strategy;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
class JustInTimeModelWarehouseStrategyTest {
  private static final AssemblyTime ASSEMBLY_TIME_OF_ONE_WEEK = new AssemblyTime(1);
  private static final AssemblyTime AN_ASSEMBLY_TIME = new AssemblyTime(23);

  private static final String FIRST_MODEL_TYPE = "first type";
  private static final ModelOrder FIRST_INITIAL_MODEL_ORDER =
      new ModelOrderBuilder()
          .withModelName(FIRST_MODEL_TYPE)
          .withAssemblyTime(ASSEMBLY_TIME_OF_ONE_WEEK)
          .build();
  private static final String SECOND_MODEL_TYPE = "second type";
  private static final ModelOrder SECOND_INITIAL_MODEL_ORDER =
      new ModelOrderBuilder()
          .withModelName(SECOND_MODEL_TYPE)
          .withAssemblyTime(ASSEMBLY_TIME_OF_ONE_WEEK)
          .build();
  private static final String THIRD_MODEL_TYPE = "third type";
  private static final ModelOrder THIRD_INITIAL_MODEL_ORDER =
      new ModelOrderBuilder()
          .withModelName(THIRD_MODEL_TYPE)
          .withAssemblyTime(ASSEMBLY_TIME_OF_ONE_WEEK)
          .build();
  private static final List<ModelOrder> MODELS_TO_ASSEMBLE =
      List.of(FIRST_INITIAL_MODEL_ORDER, SECOND_INITIAL_MODEL_ORDER, THIRD_INITIAL_MODEL_ORDER);

  @Mock private ModelManufacturer modelManufacturer;
  @Mock private ModelInventory modelInventory;
  @Mock private ModelInventoryObserver modelAssembledObserver;
  @Mock private ModelInventoryObserver anotherModelAssembledObserver;
  @Mock private ModelOrderDelayObserver modelOrderDelayObserver;

  private JustInTimeModelWarehouseStrategy justInTimeModelWarehouseStrategy;

  @BeforeEach
  public void setUp() {
    justInTimeModelWarehouseStrategy =
        new JustInTimeModelWarehouseStrategy(modelManufacturer, modelInventory, MODELS_TO_ASSEMBLE);
    justInTimeModelWarehouseStrategy.register(modelAssembledObserver);
    justInTimeModelWarehouseStrategy.register(anotherModelAssembledObserver);
    justInTimeModelWarehouseStrategy.register(modelOrderDelayObserver);
  }

  @Test
  public void whenAddOrder_thenCheckIfModelIsInStock() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(0));
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);

    // when
    justInTimeModelWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelInventory).isInStock(FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelIsInStock_whenAddOrder_thenModelIsTakenFromInventory() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(true);

    // when
    justInTimeModelWarehouseStrategy.addOrder(anOrder);

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
    justInTimeModelWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelInventory, never()).removeOne(FIRST_MODEL_TYPE);
  }

  @Test
  public void whenAddOrder_thenSendModelOrderForTheSameTypeAsTheOneOrdered() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(0));
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(false);

    // when
    justInTimeModelWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelManufacturer).addOrder(anOrder.getModelOrder());
  }

  @Test
  public void givenModelIsInStock_whenAddOrder_thenNotifyAllModelAssembledObservers() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(true);

    // when
    justInTimeModelWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelAssembledObserver).listenToModelInStock(anOrder);
    verify(anotherModelAssembledObserver).listenToModelInStock(anOrder);
  }

  @Test
  public void givenModelIsNotInStock_whenAddOrder_thenModelAssembledObserversAreNotNotify() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(0));
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(false);

    // when
    justInTimeModelWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelAssembledObserver, never()).listenToModelInStock(anOrder);
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
    justInTimeModelWarehouseStrategy.addOrder(anOrder);

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
    justInTimeModelWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelOrderDelayObserver).listenModelOrderDelay(anOrder);
  }

  @Test
  public void whenCreated_thenSendAllInitialModelOrdersToManufacturerInOrder() {
    // then
    InOrder inOrder = Mockito.inOrder(modelManufacturer);
    inOrder.verify(modelManufacturer).addOrder(FIRST_INITIAL_MODEL_ORDER);
    inOrder.verify(modelManufacturer).addOrder(SECOND_INITIAL_MODEL_ORDER);
    inOrder.verify(modelManufacturer).addOrder(THIRD_INITIAL_MODEL_ORDER);
  }

  @Test
  public void
      givenModelAssembledAndManyOrderWaitingForModel_whenListenModelAssembled_thenOnlyNotifyFirstAddedOrder() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(0));
    ModelOrder modelOrder = new ModelOrderBuilder().withModelName(FIRST_MODEL_TYPE).build();
    Order firstOrder = new OrderBuilder().withModelOrder(modelOrder).build();
    ModelOrder secondModelOrder = new ModelOrderBuilder().withModelName(SECOND_MODEL_TYPE).build();
    Order secondOrder = new OrderBuilder().withModelOrder(secondModelOrder).build();
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(false);
    justInTimeModelWarehouseStrategy.addOrder(firstOrder);
    justInTimeModelWarehouseStrategy.addOrder(secondOrder);

    // when
    justInTimeModelWarehouseStrategy.listenToModelAssembled(modelOrder);

    // then
    verify(modelAssembledObserver).listenToModelInStock(firstOrder);
    verify(modelAssembledObserver, never()).listenToModelInStock(secondOrder);
  }

  @Test
  public void
      givenModelIsAssembledAndNoPendingOrderNeedsIt_whenListenModelAssembled_thenModelIsAddedToInventory() {
    // given
    ModelOrder modelOrder = new ModelOrderBuilder().withModelName(FIRST_MODEL_TYPE).build();

    // when
    justInTimeModelWarehouseStrategy.listenToModelAssembled(modelOrder);

    // then
    verify(modelInventory).addOne(FIRST_MODEL_TYPE);
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
    justInTimeModelWarehouseStrategy.addOrder(anOrder);

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
    justInTimeModelWarehouseStrategy.addOrder(anOrder);
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(1, modelType))
        .willReturn(AN_ASSEMBLY_TIME);

    // when
    justInTimeModelWarehouseStrategy.addOrder(anOrder);

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
    justInTimeModelWarehouseStrategy.addOrder(anOrder);
    Order anotherOrder = new OrderBuilder().withModelType("another type").build();
    String modelType = anotherOrder.getModelOrder().getModelType();
    justInTimeModelWarehouseStrategy.addOrder(anotherOrder);
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(1, modelType))
        .willReturn(AN_ASSEMBLY_TIME);

    // when
    justInTimeModelWarehouseStrategy.addOrder(anotherOrder);

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
    justInTimeModelWarehouseStrategy.addOrder(anOrder);
    Order anotherOrder = new OrderBuilder().withModelType("a type").build();
    String modelType = anotherOrder.getModelOrder().getModelType();
    justInTimeModelWarehouseStrategy.addOrder(anotherOrder);
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(2, modelType))
        .willReturn(AN_ASSEMBLY_TIME);

    // when
    justInTimeModelWarehouseStrategy.addOrder(anotherOrder);

    // then
    assertThat(anotherOrder.getAssemblyDelay()).isEqualTo(AN_ASSEMBLY_TIME);
  }

  @Test
  public void
      givenModelInInventoryAndAnOrderForThisModel_whenAddOrder_thenAddModelOrderToModelManufacturer() {
    // given
    ModelOrder modelOrder = new ModelOrderBuilder().withModelName(FIRST_MODEL_TYPE).build();
    Order anOrder = new OrderBuilder().withModelOrder(modelOrder).build();
    when(modelInventory.isInStock(FIRST_MODEL_TYPE)).thenReturn(true);

    // when
    justInTimeModelWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelInventory).removeOne(anOrder.getModelOrder().getModelType());
    verify(modelManufacturer).addOrder(anOrder.getModelOrder());
  }

  @Test
  public void givenOrders_whenGetActiveOrders_thenReturnOrders() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(0));
    Order order = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    justInTimeModelWarehouseStrategy.addOrder(order);

    // when
    List<Order> activeOrders = justInTimeModelWarehouseStrategy.getActiveOrders();

    // then
    assertThat(activeOrders).containsExactly(order);
  }

  @Test
  public void givenComputeRemainingTimeWithWrongModelQuantity_whenAddOrder_thenDoNotNotifyDelay() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willThrow(new InvalidModelQuantityInQueueException());
    Order anOrder = new OrderBuilder().withModelType("a type").build();

    // when
    justInTimeModelWarehouseStrategy.addOrder(anOrder);

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
    justInTimeModelWarehouseStrategy.addOrder(anOrder);

    // then
    assertThat(anOrder.getAssemblyDelay()).isEqualTo(noDelay);
  }

  private Order createAnOrderWithModelType(String modelType) {
    ModelOrder aModelOrder = new ModelOrderBuilder().withModelName(modelType).build();
    return new OrderBuilder().withModelOrder(aModelOrder).build();
  }
}
