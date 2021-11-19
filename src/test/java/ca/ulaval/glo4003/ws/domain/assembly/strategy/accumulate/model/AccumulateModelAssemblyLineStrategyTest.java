package ca.ulaval.glo4003.ws.domain.assembly.strategy.accumulate.model;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssembledObserver;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import ca.ulaval.glo4003.ws.testUtil.ModelBuilder;
import ca.ulaval.glo4003.ws.testUtil.ModelOrderBuilder;
import ca.ulaval.glo4003.ws.testUtil.OrderBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccumulateModelAssemblyLineStrategyTest {
  private static final ProductionTime PRODUCTION_TIME_OF_ONE_WEEK = new ProductionTime(1);
  private static final ProductionTime PRODUCTION_TIME_OF_TWO_WEEKS = new ProductionTime(2);
  private static final String FIRST_MODEL_TYPE = "first type";
  private static final Model FIRST_MODEL =
      new ModelBuilder()
          .withName(FIRST_MODEL_TYPE)
          .withProductionTime(PRODUCTION_TIME_OF_ONE_WEEK)
          .build();
  private static final String SECOND_MODEL_TYPE = "second type";
  private static final Model SECOND_MODEL =
      new ModelBuilder()
          .withName(SECOND_MODEL_TYPE)
          .withProductionTime(PRODUCTION_TIME_OF_ONE_WEEK)
          .build();
  private static final String THIRD_MODEL_TYPE = "third type";
  private static final Model THIRD_MODEL =
      new ModelBuilder()
          .withName(THIRD_MODEL_TYPE)
          .withProductionTime(PRODUCTION_TIME_OF_TWO_WEEKS)
          .build();
  private static final List<Model> MODEL_ASSEMBLY_ORDER =
      List.of(FIRST_MODEL, SECOND_MODEL, THIRD_MODEL);
  private static final OrderId FIRST_ORDER_ID = new OrderId("firstOrderId");
  private static final ModelOrder FIRST_MODEL_ORDER =
      new ModelOrderBuilder().withOrderId(FIRST_ORDER_ID).withModelName(FIRST_MODEL_TYPE).build();
  private static final OrderId SECOND_ORDER_ID = new OrderId("secondOrderId");
  private static final ModelOrder SECOND_MODEL_ORDER =
      new ModelOrderBuilder().withOrderId(SECOND_ORDER_ID).withModelName(SECOND_MODEL_TYPE).build();
  private static final OrderId THIRD_ORDER_ID = new OrderId("thirdOrderId");
  private static final ModelOrder THIRD_MODEL_ORDER =
      new ModelOrderBuilder().withOrderId(THIRD_ORDER_ID).withModelName(THIRD_MODEL_TYPE).build();
  private static final OrderId FOURTH_ORDER_ID = new OrderId("fourthOrderId");
  private static final ModelOrder FOURTH_MODEL_ORDER =
      new ModelOrderBuilder().withOrderId(FOURTH_ORDER_ID).withModelName(FIRST_MODEL_TYPE).build();
  private static final ModelOrder MODEL_ORDER_WITH_SAME_MODEL =
      new ModelOrderBuilder().withOrderId(SECOND_ORDER_ID).withModelName(FIRST_MODEL_TYPE).build();

  @Mock private ModelAssemblyLineAdapter assemblyLineAdapter;
  @Mock private ModelInventory modelInventory;
  @Mock private ModelAssembledObserver modelAssembledObserver;
  @Mock private ModelAssembledObserver anotherModelAssembledObserver;
  @Mock private ModelOrderFactory modelOrderFactory;

  @Test
  public void whenAddOrder_thenCheckIfModelIsInStock() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();

    // when
    accumulateModelAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelInventory).isInStock(FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelIsInStock_whenAddOrder_thenModelIsTakenFromInventory() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(true);
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();

    // when
    accumulateModelAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelInventory).removeOne(FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelIsNotInStock_whenAddOrder_thenModelIsNotTakenFromInventory() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(false);
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();

    // when
    accumulateModelAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelInventory, never()).removeOne(FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelIsInStock_whenAddOrder_thenNotifyAllModelAssembledObservers() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(true);
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();
    accumulateModelAssemblyLineStrategy.register(modelAssembledObserver);
    accumulateModelAssemblyLineStrategy.register(anotherModelAssembledObserver);

    // when
    accumulateModelAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelAssembledObserver).listenToModelAssembled(anOrder);
    verify(anotherModelAssembledObserver).listenToModelAssembled(anOrder);
  }

  @Test
  public void givenModelIsNotInStock_whenAddOrder_thenModelAssembledObserversAreNotNotify() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(false);
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();
    accumulateModelAssemblyLineStrategy.register(modelAssembledObserver);
    accumulateModelAssemblyLineStrategy.register(anotherModelAssembledObserver);

    // when
    accumulateModelAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelAssembledObserver, never()).listenToModelAssembled(anOrder);
    verify(anotherModelAssembledObserver, never()).listenToModelAssembled(anOrder);
  }

  @Test
  public void whenAdvance_thenCallAdvanceOnModelAssemblyLine() {
    // given
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK)).willReturn(FIRST_MODEL_ORDER);
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();

    // when
    accumulateModelAssemblyLineStrategy.advance();

    // then
    verify(assemblyLineAdapter).advance();
  }

  @Test
  public void whenCreate_thenSendFirstModelInModelOrderToBeAssembled() {
    // given
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK)).willReturn(FIRST_MODEL_ORDER);

    // when
    createAccumulateModelAssemblyLineStrategy();

    // then
    verify(assemblyLineAdapter).addOrder(FIRST_MODEL_ORDER);
  }

  @Test
  public void whenAdvance_thenCheckIfCurrentModelBeingAssembledIsDoneBeingAssembled() {
    // given
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK)).willReturn(FIRST_MODEL_ORDER);
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();

    // when
    accumulateModelAssemblyLineStrategy.advance();

    // then
    verify(assemblyLineAdapter).getAssemblyStatus(FIRST_MODEL_ORDER.getOrderId());
  }

  @Test
  public void
      givenCurrentModelOrderStillInProgress_whenAdvance_thenNextModelInModelAssemblyOrderIsNotSentToBeAssembled() {
    // given
    setUpFirstModelOrderStillInProgress();
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();

    // when
    accumulateModelAssemblyLineStrategy.advance();

    // then
    verify(assemblyLineAdapter, never()).addOrder(SECOND_MODEL_ORDER);
  }

  @Test
  public void givenModelAssemblyOrder_whenAdvance_thenModelsAreAssembledInGivenOrderAndInLoop() {
    // given
    when(modelOrderFactory.create(FIRST_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK))
        .thenReturn(FIRST_MODEL_ORDER, FOURTH_MODEL_ORDER);
    given(assemblyLineAdapter.getAssemblyStatus(FIRST_ORDER_ID))
        .willReturn(AssemblyStatus.ASSEMBLED);
    setUpSecondModelOrder();
    setUpThirdModelOrder();

    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();

    // when
    accumulateModelAssemblyLineStrategy.advance();
    accumulateModelAssemblyLineStrategy.advance();
    accumulateModelAssemblyLineStrategy.advance();

    // then
    InOrder assemblyLineAdapterCallOrder = inOrder(assemblyLineAdapter);
    assemblyLineAdapterCallOrder.verify(assemblyLineAdapter).addOrder(FIRST_MODEL_ORDER);
    assemblyLineAdapterCallOrder.verify(assemblyLineAdapter).addOrder(SECOND_MODEL_ORDER);
    assemblyLineAdapterCallOrder.verify(assemblyLineAdapter).addOrder(THIRD_MODEL_ORDER);
    assemblyLineAdapterCallOrder.verify(assemblyLineAdapter).addOrder(FOURTH_MODEL_ORDER);
  }

  @Test
  public void givenModelStillInProgress_whenAdvance_thenModelIsNotAddedToInventory() {
    // given
    setUpFirstModelOrderStillInProgress();
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();

    // when
    accumulateModelAssemblyLineStrategy.advance();

    // then
    verify(modelInventory, never()).addOne(FIRST_MODEL_TYPE);
  }

  @Test
  public void
      givenModelAssembledAndManyOrderWaitingForModel_whenAdvance_thenOnlyNotifyFirstAddedOrder() {
    // given
    Order firstOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    Order secondOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(false);
    setUpFirstModelOrderAssembledWhenAdvance();
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();
    accumulateModelAssemblyLineStrategy.addOrder(firstOrder);
    accumulateModelAssemblyLineStrategy.addOrder(secondOrder);
    accumulateModelAssemblyLineStrategy.register(modelAssembledObserver);

    // when
    accumulateModelAssemblyLineStrategy.advance();

    // then
    verify(modelAssembledObserver).listenToModelAssembled(firstOrder);
    verify(modelAssembledObserver, never()).listenToModelAssembled(secondOrder);
  }

  @Test
  public void
      givenModelIsAssembledAndNoPendingOrderNeedIt_whenAdvance_thenModelIsAddedToInventory() {
    // given
    Order firstOrder = createAnOrderWithModelType(SECOND_MODEL_TYPE);
    Order secondOrder = createAnOrderWithModelType(THIRD_MODEL_TYPE);
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK)).willReturn(FIRST_MODEL_ORDER);
    given(assemblyLineAdapter.getAssemblyStatus(FIRST_ORDER_ID))
        .willReturn(AssemblyStatus.ASSEMBLED);
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();
    accumulateModelAssemblyLineStrategy.addOrder(firstOrder);
    accumulateModelAssemblyLineStrategy.addOrder(secondOrder);

    // when
    accumulateModelAssemblyLineStrategy.advance();

    // then
    verify(modelInventory).addOne(FIRST_MODEL_TYPE);
  }

  @Test
  public void givenManyOrdersWaitingForTheSameModel_whenAdvance_thenNotifyOrdersInOrder() {
    // given
    Order firstOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    Order secondOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    when(modelOrderFactory.create(FIRST_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK))
        .thenReturn(FIRST_MODEL_ORDER, MODEL_ORDER_WITH_SAME_MODEL);
    given(assemblyLineAdapter.getAssemblyStatus(FIRST_MODEL_ORDER.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);
    given(assemblyLineAdapter.getAssemblyStatus(MODEL_ORDER_WITH_SAME_MODEL.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);
    List<Model> modelAssemblyOrder = List.of(FIRST_MODEL);
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy(modelAssemblyOrder);
    accumulateModelAssemblyLineStrategy.addOrder(firstOrder);
    accumulateModelAssemblyLineStrategy.addOrder(secondOrder);
    accumulateModelAssemblyLineStrategy.register(modelAssembledObserver);

    // when
    accumulateModelAssemblyLineStrategy.advance();
    accumulateModelAssemblyLineStrategy.advance();

    // then
    InOrder modelAssembledObserverCallOrder = inOrder(modelAssembledObserver);
    modelAssembledObserverCallOrder
        .verify(modelAssembledObserver)
        .listenToModelAssembled(firstOrder);
    modelAssembledObserverCallOrder
        .verify(modelAssembledObserver)
        .listenToModelAssembled(secondOrder);
    verify(modelAssembledObserver, new Times(1)).listenToModelAssembled(firstOrder);
    verify(modelAssembledObserver, new Times(1)).listenToModelAssembled(secondOrder);
  }

  @Test
  public void
      givenOrderAddedWithModelNotInStock_whenComputeRemainingTimeToProduce_thenReturnModelProductionTime() {
    // given
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, FIRST_MODEL.getProductionTime())).willReturn(FIRST_MODEL_ORDER);
    Order order = createAnOrderWithModel(FIRST_MODEL);
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();
    accumulateModelAssemblyLineStrategy.addOrder(order);
    ProductionTime expectedProductionTime = order.getModel().getProductionTime();

    // when
    ProductionTime actualProductionTime =
        accumulateModelAssemblyLineStrategy.computeRemainingTimeToProduce(order.getId());

    // then
    assertThat(actualProductionTime).isEqualTo(expectedProductionTime);
  }

  @Test
  public void givenOrderAddedWithModelInStock_whenComputeRemainingTimeToProduce_thenReturnZero() {
    // given
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, FIRST_MODEL.getProductionTime())).willReturn(FIRST_MODEL_ORDER);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(true);
    Order order = createAnOrderWithModel(FIRST_MODEL);
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();
    accumulateModelAssemblyLineStrategy.addOrder(order);
    ProductionTime expectedProductionTime = new ProductionTime(0);

    // when
    ProductionTime actualProductionTime =
        accumulateModelAssemblyLineStrategy.computeRemainingTimeToProduce(order.getId());

    // then
    assertThat(actualProductionTime).isEqualTo(expectedProductionTime);
  }

  @Test
  public void
      givenTwoOrdersAddedWithModelsFollowingCycle_whenComputeRemainingTimeToProduceOfSecondOrder_thenReturnSumOfModelProductionTimes() {
    // given
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, FIRST_MODEL.getProductionTime())).willReturn(FIRST_MODEL_ORDER);
    Order firstOrder = createAnOrderWithModel(FIRST_MODEL);
    Order secondOrder = createAnOrderWithModel(SECOND_MODEL);
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();
    accumulateModelAssemblyLineStrategy.addOrder(firstOrder);
    accumulateModelAssemblyLineStrategy.addOrder(secondOrder);
    ProductionTime expectedProductionTime =
        FIRST_MODEL.getProductionTime().add(SECOND_MODEL.getProductionTime());

    // when
    ProductionTime actualProductionTime =
        accumulateModelAssemblyLineStrategy.computeRemainingTimeToProduce(secondOrder.getId());

    // then
    assertThat(actualProductionTime).isEqualTo(expectedProductionTime);
  }

  @Test
  public void
      givenTwoOrdersAddedWithFirstAndThirdModelsOfCycle_whenComputeRemainingTimeToProduceOfSecondOrder_thenReturnSumOfFirstSecondAndThirdModelsProductionTime() {
    // given
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, FIRST_MODEL.getProductionTime())).willReturn(FIRST_MODEL_ORDER);
    Order firstOrder = createAnOrderWithModel(FIRST_MODEL);
    Order secondOrder = createAnOrderWithModel(THIRD_MODEL);
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();
    accumulateModelAssemblyLineStrategy.addOrder(firstOrder);
    accumulateModelAssemblyLineStrategy.addOrder(secondOrder);
    ProductionTime expectedProductionTime =
        FIRST_MODEL
            .getProductionTime()
            .add(SECOND_MODEL.getProductionTime())
            .add(THIRD_MODEL.getProductionTime());

    // when
    ProductionTime actualProductionTime =
        accumulateModelAssemblyLineStrategy.computeRemainingTimeToProduce(secondOrder.getId());

    // then
    assertThat(actualProductionTime).isEqualTo(expectedProductionTime);
  }

  @Test
  public void
      givenTwoOrdersAddedWithFirstModelsOfCycle_whenComputeRemainingTimeToProduceOfSecondOrder_thenReturnSumOfModelAndWholeCycleProductionTime() {
    // given
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, FIRST_MODEL.getProductionTime())).willReturn(FIRST_MODEL_ORDER);
    Order firstOrder = createAnOrderWithModel(FIRST_MODEL);
    Order secondOrder = createAnOrderWithModel(FIRST_MODEL);
    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();
    accumulateModelAssemblyLineStrategy.addOrder(firstOrder);
    accumulateModelAssemblyLineStrategy.addOrder(secondOrder);
    ProductionTime expectedProductionTime =
        FIRST_MODEL
            .getProductionTime()
            .add(SECOND_MODEL.getProductionTime())
            .add(THIRD_MODEL.getProductionTime().add(FIRST_MODEL.getProductionTime()));

    // when
    ProductionTime actualProductionTime =
        accumulateModelAssemblyLineStrategy.computeRemainingTimeToProduce(secondOrder.getId());

    // then
    assertThat(actualProductionTime).isEqualTo(expectedProductionTime);
  }

  @Test
  public void givenOrdersInQueue_whenGetActiveOrders_thenReturnOrders() {
    // given
    Order firstOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    Order secondOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(false);

    AccumulateModelAssemblyLineStrategy accumulateModelAssemblyLineStrategy =
        createAccumulateModelAssemblyLineStrategy();
    accumulateModelAssemblyLineStrategy.addOrder(firstOrder);
    accumulateModelAssemblyLineStrategy.addOrder(secondOrder);

    // when
    List<Order> ordersInQueue = accumulateModelAssemblyLineStrategy.getActiveOrders();

    // then
    assertThat(ordersInQueue).containsExactly(firstOrder, secondOrder);
  }

  private Order createAnOrderWithModelType(String modelType) {
    Model aModel = new ModelBuilder().withName(modelType).build();
    return new OrderBuilder().withModel(aModel).build();
  }

  private Order createAnOrderWithModel(Model model) {
    return new OrderBuilder().withModel(model).build();
  }

  private AccumulateModelAssemblyLineStrategy createAccumulateModelAssemblyLineStrategy() {
    return new AccumulateModelAssemblyLineStrategy(
        MODEL_ASSEMBLY_ORDER, assemblyLineAdapter, modelInventory, modelOrderFactory);
  }

  private AccumulateModelAssemblyLineStrategy createAccumulateModelAssemblyLineStrategy(
      List<Model> modelAssemblyOrder) {
    return new AccumulateModelAssemblyLineStrategy(
        modelAssemblyOrder, assemblyLineAdapter, modelInventory, modelOrderFactory);
  }

  private void setUpFirstModelOrderAssembledWhenAdvance() {
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK)).willReturn(FIRST_MODEL_ORDER);
    given(assemblyLineAdapter.getAssemblyStatus(FIRST_ORDER_ID))
        .willReturn(AssemblyStatus.ASSEMBLED);
  }

  private void setUpFirstModelOrderStillInProgress() {
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK)).willReturn(FIRST_MODEL_ORDER);
    given(assemblyLineAdapter.getAssemblyStatus(FIRST_ORDER_ID))
        .willReturn(AssemblyStatus.IN_PROGRESS);
  }

  private void setUpSecondModelOrder() {
    given(modelOrderFactory.create(SECOND_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK)).willReturn(SECOND_MODEL_ORDER);
    given(assemblyLineAdapter.getAssemblyStatus(SECOND_MODEL_ORDER.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);
  }

  private void setUpThirdModelOrder() {
    given(modelOrderFactory.create(THIRD_MODEL_TYPE, THIRD_MODEL.getProductionTime())).willReturn(THIRD_MODEL_ORDER);
    given(assemblyLineAdapter.getAssemblyStatus(THIRD_MODEL_ORDER.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);
  }
}
