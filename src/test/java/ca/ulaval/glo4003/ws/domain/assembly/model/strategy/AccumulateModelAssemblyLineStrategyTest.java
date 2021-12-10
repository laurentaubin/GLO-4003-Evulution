package ca.ulaval.glo4003.ws.domain.assembly.model.strategy;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelAssembledObserver;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelInventory;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.time.AssemblyTime;
import ca.ulaval.glo4003.ws.fixture.ModelOrderBuilder;
import ca.ulaval.glo4003.ws.fixture.OrderBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccumulateModelAssemblyLineStrategyTest {
  private static final AssemblyTime AN_ASSEMBLY_TIME_OF_ONE_WEEK = new AssemblyTime(1);
  private static final AssemblyTime AN_ASSEMBLY_TIME_OF_TWO_WEEKS = new AssemblyTime(2);
  private static final String A_FIRST_MODEL_TYPE = "first type";
  private static final ModelOrder A_FIRST_MODEL_IN_CYCLE =
      new ModelOrderBuilder()
          .withModelName(A_FIRST_MODEL_TYPE)
          .withAssemblyTime(AN_ASSEMBLY_TIME_OF_ONE_WEEK)
          .build();
  private static final String A_SECOND_MODEL_TYPE = "second type";
  private static final ModelOrder A_SECOND_MODEL_IN_CYCLE =
      new ModelOrderBuilder()
          .withModelName(A_SECOND_MODEL_TYPE)
          .withAssemblyTime(AN_ASSEMBLY_TIME_OF_ONE_WEEK)
          .build();
  private static final String A_THIRD_MODEL_TYPE = "third type";
  private static final ModelOrder A_THIRD_MODEL_IN_CYCLE =
      new ModelOrderBuilder()
          .withModelName(A_THIRD_MODEL_TYPE)
          .withAssemblyTime(AN_ASSEMBLY_TIME_OF_TWO_WEEKS)
          .build();
  private static final List<ModelOrder> A_MODEL_ASSEMBLY_ORDER =
      List.of(A_FIRST_MODEL_IN_CYCLE, A_SECOND_MODEL_IN_CYCLE, A_THIRD_MODEL_IN_CYCLE);
  private static final OrderId A_FIRST_ORDER_ID = new OrderId("firstOrderId");
  private static final ModelOrder A_FIRST_MODEL_ORDER =
      new ModelOrderBuilder()
          .withOrderId(A_FIRST_ORDER_ID)
          .withModelName(A_FIRST_MODEL_TYPE)
          .withAssemblyTime(AN_ASSEMBLY_TIME_OF_ONE_WEEK)
          .build();
  private static final OrderId A_SECOND_ORDER_ID = new OrderId("secondOrderId");
  private static final ModelOrder A_SECOND_MODEL_ORDER =
      new ModelOrderBuilder()
          .withOrderId(A_SECOND_ORDER_ID)
          .withModelName(A_SECOND_MODEL_TYPE)
          .withAssemblyTime(AN_ASSEMBLY_TIME_OF_ONE_WEEK)
          .build();
  private static final OrderId A_THIRD_ORDER_ID = new OrderId("thirdOrderId");
  private static final ModelOrder A_THIRD_MODEL_ORDER =
      new ModelOrderBuilder()
          .withOrderId(A_THIRD_ORDER_ID)
          .withModelName(A_THIRD_MODEL_TYPE)
          .withAssemblyTime(AN_ASSEMBLY_TIME_OF_TWO_WEEKS)
          .build();

  @Mock private ModelAssemblyLineAdapter assemblyLineAdapter;
  @Mock private ModelInventory modelInventory;
  @Mock private ModelAssembledObserver modelAssembledObserver;
  @Mock private ModelAssembledObserver anotherModelAssembledObserver;

  private AccumulateModelAssemblyLineStrategy assemblyLineStrategy;

  @BeforeEach
  public void setUp() {
    assemblyLineStrategy =
        new AccumulateModelAssemblyLineStrategy(
                A_MODEL_ASSEMBLY_ORDER, assemblyLineAdapter, modelInventory);
  }

  @Test
  public void whenAddOrder_thenCheckIfModelIsInStock() {
    // given
    Order anOrder = createAnOrderWithModelType(A_FIRST_MODEL_TYPE);

    // when
    assemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelInventory).isInStock(A_FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelIsInStock_whenAddOrder_thenModelIsTakenFromInventory() {
    // given
    Order anOrder = createAnOrderWithModelType(A_FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(A_FIRST_MODEL_TYPE)).willReturn(true);

    // when
    assemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelInventory).removeOne(A_FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelIsNotInStock_whenAddOrder_thenModelIsNotTakenFromInventory() {
    // given
    Order anOrder = createAnOrderWithModelType(A_FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(A_FIRST_MODEL_TYPE)).willReturn(false);

    // when
    assemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelInventory, never()).removeOne(A_FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelIsInStock_whenAddOrder_thenNotifyAllModelAssembledObservers() {
    // given
    Order anOrder = createAnOrderWithModelType(A_FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(A_FIRST_MODEL_TYPE)).willReturn(true);
    assemblyLineStrategy.register(modelAssembledObserver);
    assemblyLineStrategy.register(anotherModelAssembledObserver);

    // when
    assemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelAssembledObserver).listenToModelAssembled(anOrder);
    verify(anotherModelAssembledObserver).listenToModelAssembled(anOrder);
  }

  @Test
  public void givenModelIsNotInStock_whenAddOrder_thenModelAssembledObserversAreNotNotify() {
    // given
    Order anOrder = createAnOrderWithModelType(A_FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(A_FIRST_MODEL_TYPE)).willReturn(false);
    assemblyLineStrategy.register(modelAssembledObserver);
    assemblyLineStrategy.register(anotherModelAssembledObserver);

    // when
    assemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelAssembledObserver, never()).listenToModelAssembled(anOrder);
    verify(anotherModelAssembledObserver, never()).listenToModelAssembled(anOrder);
  }

  @Test
  public void whenAdvance_thenCallAdvanceOnModelAssemblyLine() {
    // when
    assemblyLineStrategy.advance();

    // then
    verify(assemblyLineAdapter).advance();
  }

  @Test
  public void whenCreate_thenSendFirstModelInModelOrderToBeAssembled() {
    // then
    verify(assemblyLineAdapter).addOrder(A_FIRST_MODEL_IN_CYCLE);
  }

  @Test
  public void whenAdvance_thenCheckIfCurrentModelBeingAssembledIsDoneBeingAssembled() {
    // when
    assemblyLineStrategy.advance();

    // then
    verify(assemblyLineAdapter).getAssemblyStatus(A_FIRST_MODEL_IN_CYCLE.getOrderId());
  }

  @Test
  public void
      givenCurrentModelOrderStillInProgress_whenAdvance_thenNextModelInModelAssemblyOrderIsNotSentToBeAssembled() {
    // given
    setUpFirstModelOrderStillInProgress();

    // when
    assemblyLineStrategy.advance();

    // then
    verify(assemblyLineAdapter, never()).addOrder(A_SECOND_MODEL_IN_CYCLE);
  }

  @Test
  public void givenModelAssemblyOrder_whenAdvance_thenModelsAreAssembledInGivenOrderAndInLoop() {
    // given
    given(assemblyLineAdapter.getAssemblyStatus(A_FIRST_MODEL_IN_CYCLE.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);
    given(assemblyLineAdapter.getAssemblyStatus(A_SECOND_MODEL_IN_CYCLE.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);
    given(assemblyLineAdapter.getAssemblyStatus(A_THIRD_MODEL_IN_CYCLE.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);

    // when
    assemblyLineStrategy.advance();
    assemblyLineStrategy.advance();
    assemblyLineStrategy.advance();

    // then
    InOrder assemblyLineAdapterCallOrder = inOrder(assemblyLineAdapter);
    assemblyLineAdapterCallOrder.verify(assemblyLineAdapter).addOrder(A_FIRST_MODEL_IN_CYCLE);
    assemblyLineAdapterCallOrder.verify(assemblyLineAdapter).addOrder(A_SECOND_MODEL_IN_CYCLE);
    assemblyLineAdapterCallOrder.verify(assemblyLineAdapter).addOrder(A_THIRD_MODEL_IN_CYCLE);
    assemblyLineAdapterCallOrder.verify(assemblyLineAdapter).addOrder(A_FIRST_MODEL_IN_CYCLE);
  }

  @Test
  public void givenModelStillInProgress_whenAdvance_thenModelIsNotAddedToInventory() {
    // given
    setUpFirstModelOrderStillInProgress();

    // when
    assemblyLineStrategy.advance();

    // then
    verify(modelInventory, never()).addOne(A_FIRST_MODEL_TYPE);
  }

  @Test
  public void
      givenModelAssembledAndManyOrderWaitingForModel_whenAdvance_thenOnlyNotifyFirstAddedOrder() {
    // given
    Order firstOrder = createAnOrderWithModelType(A_FIRST_MODEL_TYPE);
    Order secondOrder = createAnOrderWithModelType(A_FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(A_FIRST_MODEL_TYPE)).willReturn(false);
    given(assemblyLineAdapter.getAssemblyStatus(A_FIRST_MODEL_IN_CYCLE.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);
    addOrdersToAssemblyLine(List.of(firstOrder, secondOrder));
    assemblyLineStrategy.register(modelAssembledObserver);

    // when
    assemblyLineStrategy.advance();

    // then
    verify(modelAssembledObserver).listenToModelAssembled(firstOrder);
    verify(modelAssembledObserver, never()).listenToModelAssembled(secondOrder);
  }

  @Test
  public void
      givenModelIsAssembledAndNoPendingOrderNeedIt_whenAdvance_thenModelIsAddedToInventory() {
    // given
    Order firstOrder = createAnOrderWithModelType(A_SECOND_MODEL_TYPE);
    Order secondOrder = createAnOrderWithModelType(A_THIRD_MODEL_TYPE);
    given(assemblyLineAdapter.getAssemblyStatus(A_FIRST_MODEL_IN_CYCLE.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);
    addOrdersToAssemblyLine(List.of(firstOrder, secondOrder));

    // when
    assemblyLineStrategy.advance();

    // then
    verify(modelInventory).addOne(A_FIRST_MODEL_TYPE);
  }

  @Test
  public void
      givenManyOrdersWaitingForTheSameModelAndModelIsAssembled_whenAdvance_thenNotifyOrdersInOrder() {
    // given
    Order firstOrder = createAnOrderWithModelType(A_FIRST_MODEL_TYPE);
    Order secondOrder = createAnOrderWithModelType(A_FIRST_MODEL_TYPE);
    given(assemblyLineAdapter.getAssemblyStatus(A_FIRST_MODEL_IN_CYCLE.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);
    List<ModelOrder> modelAssemblyOrder = List.of(A_FIRST_MODEL_IN_CYCLE);
    assemblyLineStrategy = createAccumulateModelAssemblyLineStrategy(modelAssemblyOrder);
    addOrdersToAssemblyLine(List.of(firstOrder, secondOrder));
    assemblyLineStrategy.register(modelAssembledObserver);

    // when
    assemblyLineStrategy.advance();
    assemblyLineStrategy.advance();

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
      givenOrderAddedWithModelNotInStock_whenComputeRemainingTimeToProduce_thenReturnModelOrderAssemblyTime() {
    // given
    Order order = createAnOrderWithModel(A_FIRST_MODEL_ORDER);
    assemblyLineStrategy.addOrder(order);
    AssemblyTime expectedAssemblyTime = order.getModelOrder().getAssemblyTime();

    // when
    AssemblyTime actualAssemblyTime =
        assemblyLineStrategy.computeRemainingTimeToProduce(order.getId());

    // then
    assertThat(actualAssemblyTime).isEqualTo(expectedAssemblyTime);
  }

  @Test
  public void givenOrderAddedWithModelInStock_whenComputeRemainingTimeToProduce_thenReturnZero() {
    // given
    given(modelInventory.isInStock(A_FIRST_MODEL_TYPE)).willReturn(true);
    Order order = createAnOrderWithModel(A_FIRST_MODEL_ORDER);
    assemblyLineStrategy.addOrder(order);
    AssemblyTime expectedAssemblyTime = new AssemblyTime(0);

    // when
    AssemblyTime actualAssemblyTime =
        assemblyLineStrategy.computeRemainingTimeToProduce(order.getId());

    // then
    assertThat(actualAssemblyTime).isEqualTo(expectedAssemblyTime);
  }

  @Test
  public void
      givenTwoOrdersAddedWithModelsFollowingCycle_whenComputeRemainingTimeToProduceOfSecondOrder_thenReturnSumOfModelAssemblyTimes() {
    // given
    Order firstOrder = createAnOrderWithModel(A_FIRST_MODEL_ORDER);
    Order secondOrder = createAnOrderWithModel(A_SECOND_MODEL_ORDER);
    addOrdersToAssemblyLine(List.of(firstOrder, secondOrder));
    AssemblyTime expectedAssemblyTime =
        A_FIRST_MODEL_IN_CYCLE.getAssemblyTime().add(A_SECOND_MODEL_IN_CYCLE.getAssemblyTime());

    // when
    AssemblyTime actualAssemblyTime =
        assemblyLineStrategy.computeRemainingTimeToProduce(secondOrder.getId());

    // then
    assertThat(actualAssemblyTime).isEqualTo(expectedAssemblyTime);
  }

  @Test
  public void
      givenTwoOrdersAddedWithFirstAndThirdModelsOfCycle_whenComputeRemainingTimeToProduceOfSecondOrder_thenReturnSumOfFirstSecondAndThirdModelsAssemblyTime() {
    // given
    Order firstOrder = createAnOrderWithModel(A_FIRST_MODEL_ORDER);
    Order secondOrder = createAnOrderWithModel(A_THIRD_MODEL_ORDER);
    addOrdersToAssemblyLine(List.of(firstOrder, secondOrder));
    AssemblyTime expectedAssemblyTime =
        A_FIRST_MODEL_IN_CYCLE
            .getAssemblyTime()
            .add(A_SECOND_MODEL_IN_CYCLE.getAssemblyTime())
            .add(A_THIRD_MODEL_IN_CYCLE.getAssemblyTime());

    // when
    AssemblyTime actualAssemblyTime =
        assemblyLineStrategy.computeRemainingTimeToProduce(secondOrder.getId());

    // then
    assertThat(actualAssemblyTime).isEqualTo(expectedAssemblyTime);
  }

  @Test
  public void
      givenTwoOrdersAddedWithFirstModelsOfCycle_whenComputeRemainingTimeToProduceOfSecondOrder_thenReturnSumOfModelAndWholeCycleAssemblyTime() {
    // given
    Order firstOrder = createAnOrderWithModel(A_FIRST_MODEL_ORDER);
    Order secondOrder = createAnOrderWithModel(A_FIRST_MODEL_ORDER);
    addOrdersToAssemblyLine(List.of(firstOrder, secondOrder));
    AssemblyTime expectedAssemblyTime =
        A_FIRST_MODEL_IN_CYCLE
            .getAssemblyTime()
            .add(A_SECOND_MODEL_IN_CYCLE.getAssemblyTime())
            .add(
                A_THIRD_MODEL_IN_CYCLE.getAssemblyTime().add(A_FIRST_MODEL_IN_CYCLE.getAssemblyTime()));

    // when
    AssemblyTime actualAssemblyTime =
        assemblyLineStrategy.computeRemainingTimeToProduce(secondOrder.getId());

    // then
    assertThat(actualAssemblyTime).isEqualTo(expectedAssemblyTime);
  }

  @Test
  public void givenOrdersInQueue_whenGetActiveOrders_thenReturnOrders() {
    // given
    Order firstOrder = createAnOrderWithModelType(A_FIRST_MODEL_TYPE);
    Order secondOrder = createAnOrderWithModelType(A_FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(A_FIRST_MODEL_TYPE)).willReturn(false);
    addOrdersToAssemblyLine(List.of(firstOrder, secondOrder));

    // when
    List<Order> ordersInQueue = assemblyLineStrategy.getActiveOrders();

    // then
    assertThat(ordersInQueue).containsExactly(firstOrder, secondOrder);
  }

  private Order createAnOrderWithModelType(String modelType) {
    ModelOrder aModelOrder = new ModelOrderBuilder().withModelName(modelType).build();
    return new OrderBuilder().withModelOrder(aModelOrder).build();
  }

  private Order createAnOrderWithModel(ModelOrder modelOrder) {
    return new OrderBuilder().withModelOrder(modelOrder).build();
  }

  private AccumulateModelAssemblyLineStrategy createAccumulateModelAssemblyLineStrategy(
      List<ModelOrder> modelAssemblyOrder) {
    return new AccumulateModelAssemblyLineStrategy(
        modelAssemblyOrder, assemblyLineAdapter, modelInventory);
  }

  private void setUpFirstModelOrderStillInProgress() {
    given(assemblyLineAdapter.getAssemblyStatus(A_FIRST_MODEL_IN_CYCLE.getOrderId()))
        .willReturn(AssemblyStatus.IN_PROGRESS);
  }

  private void addOrdersToAssemblyLine(List<Order> orders) {
    for (Order order : orders) {
      assemblyLineStrategy.addOrder(order);
    }
  }
}
