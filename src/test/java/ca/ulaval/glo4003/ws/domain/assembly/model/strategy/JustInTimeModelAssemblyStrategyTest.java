package ca.ulaval.glo4003.ws.domain.assembly.model.strategy;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JustInTimeModelAssemblyStrategyTest {
  private static final AssemblyTime ASSEMBLY_TIME_OF_ONE_WEEK = new AssemblyTime(1);

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
  private static final OrderId FIRST_ORDER_ID = new OrderId("firstOrderId");
  private static final ModelOrder FIRST_MODEL_ORDER =
      new ModelOrderBuilder()
          .withOrderId(FIRST_ORDER_ID)
          .withModelName(FIRST_MODEL_TYPE)
          .withAssemblyTime(ASSEMBLY_TIME_OF_ONE_WEEK)
          .build();
  private static final OrderId SECOND_ORDER_ID = new OrderId("secondOrderId");

  @Mock private ModelAssemblyLineAdapter assemblyLineAdapter;
  @Mock private ModelInventory modelInventory;
  @Mock private ModelAssembledObserver modelAssembledObserver;
  @Mock private ModelAssembledObserver anotherModelAssembledObserver;

  private JustInTimeModelAssemblyLineStrategy assemblyLineStrategy;

  @BeforeEach
  public void setUp() {
    assemblyLineStrategy =
        new JustInTimeModelAssemblyLineStrategy(
            assemblyLineAdapter, modelInventory, MODELS_TO_ASSEMBLE);
  }

  @Test
  public void whenAddOrder_thenCheckIfModelIsInStock() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);

    // when
    assemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelInventory).isInStock(FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelIsInStock_whenAddOrder_thenModelIsTakenFromInventory() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(true);

    // when
    assemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelInventory).removeOne(FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelIsNotInStock_whenAddOrder_thenModelIsNotTakenFromInventory() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(false);

    // when
    assemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelInventory, never()).removeOne(FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelIsInStock_whenAddOrder_thenNotifyAllModelAssembledObservers() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(true);
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
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(false);

    JustInTimeModelAssemblyLineStrategy justInTimeModelAssemblyLineStrategy =
        createJustInTimeModelAssemblyStrategy();
    justInTimeModelAssemblyLineStrategy.register(modelAssembledObserver);
    justInTimeModelAssemblyLineStrategy.register(anotherModelAssembledObserver);

    // when
    justInTimeModelAssemblyLineStrategy.addOrder(anOrder);

    // then
    verify(modelAssembledObserver, never()).listenToModelAssembled(anOrder);
    verify(anotherModelAssembledObserver, never()).listenToModelAssembled(anOrder);
  }

  @Test
  public void whenAdvance_thenCallAdvanceOnModelAssemblyLine() {
    // given
    JustInTimeModelAssemblyLineStrategy JustInTimeModelAssemblyLineStrategy =
        createJustInTimeModelAssemblyStrategy();

    // when
    JustInTimeModelAssemblyLineStrategy.advance();

    // then
    verify(assemblyLineAdapter).advance();
  }

  @Test
  public void whenCreated_thenSendModelOrderToModelAssemblyLine() {
    // then
    verify(assemblyLineAdapter).addOrder(FIRST_INITIAL_MODEL_ORDER);
  }

  @Test
  public void whenAdvance_thenCheckIfCurrentModelBeingAssembledIsDoneBeingAssembled() {
    // when
    assemblyLineStrategy.advance();

    // then
    verify(assemblyLineAdapter).getAssemblyStatus(FIRST_INITIAL_MODEL_ORDER.getOrderId());
  }

  @Test
  public void
      givenCurrentModelOrderStillInProgress_whenAdvance_thenNextModelInModelAssemblyOrderIsNotSentToBeAssembled() {
    // given
    setUpFirstModelOrderStillInProgress();

    // when
    assemblyLineStrategy.advance();

    // then
    verify(assemblyLineAdapter, never()).addOrder(SECOND_INITIAL_MODEL_ORDER);
  }

  @Test
  public void givenNoAddedOrder_whenAdvanceMultipleTimes_thenModelsAreAssembledOnce() {
    // given
    given(assemblyLineAdapter.getAssemblyStatus(FIRST_INITIAL_MODEL_ORDER.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);
    given(assemblyLineAdapter.getAssemblyStatus(SECOND_INITIAL_MODEL_ORDER.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);
    given(assemblyLineAdapter.getAssemblyStatus(THIRD_INITIAL_MODEL_ORDER.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);

    // when
    assemblyLineStrategy.advance();
    assemblyLineStrategy.advance();
    assemblyLineStrategy.advance();

    verify(assemblyLineAdapter, times(1)).addOrder(FIRST_INITIAL_MODEL_ORDER);
    verify(assemblyLineAdapter, times(1)).addOrder(SECOND_INITIAL_MODEL_ORDER);
    verify(assemblyLineAdapter, times(1)).addOrder(THIRD_INITIAL_MODEL_ORDER);
  }

  @Test
  public void givenModelStillInProgress_whenAdvance_thenModelIsNotAddedToInventory() {
    // given
    setUpFirstModelOrderStillInProgress();

    // when
    assemblyLineStrategy.advance();

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
    given(assemblyLineAdapter.getAssemblyStatus(FIRST_INITIAL_MODEL_ORDER.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);
    assemblyLineStrategy.addOrder(firstOrder);
    assemblyLineStrategy.addOrder(secondOrder);
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
    given(assemblyLineAdapter.getAssemblyStatus(FIRST_INITIAL_MODEL_ORDER.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);

    // when
    assemblyLineStrategy.advance();

    // then
    verify(modelInventory).addOne(FIRST_MODEL_TYPE);
  }

  @Test
  public void
      givenOneOrderInQueue_whenComputeRemainingTimeToProduce_thenReturnOrderRemainingTimeToProduce() {
    // given
    Order order = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    List<ModelOrder> modelsToAssemble = List.of(FIRST_INITIAL_MODEL_ORDER);
    assemblyLineStrategy =
        new JustInTimeModelAssemblyLineStrategy(
            assemblyLineAdapter, modelInventory, modelsToAssemble);
    assemblyLineStrategy.addOrder(order);

    // when
    AssemblyTime assemblyTime = assemblyLineStrategy.computeRemainingTimeToProduce(order.getId());

    // then
    assertThat(assemblyTime).isEqualTo(order.getModelOrder().getAssemblyTime());
  }

  @Test
  public void
      givenMultipleOrdersRequestingTheSameModelTypeAndMultipleModelsToAssemble_whenComputeRemainingTimeToProduce_thenReturnCorrectRemainingTimeToProduce() {
    // given
    Order order = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    Order anotherOrder =
        new OrderBuilder().withOrderId(SECOND_ORDER_ID).withModelOrder(FIRST_MODEL_ORDER).build();
    List<ModelOrder> modelsToAssemble =
        List.of(FIRST_INITIAL_MODEL_ORDER, SECOND_INITIAL_MODEL_ORDER);
    assemblyLineStrategy =
        new JustInTimeModelAssemblyLineStrategy(
            assemblyLineAdapter, modelInventory, modelsToAssemble);
    assemblyLineStrategy.addOrder(order);
    assemblyLineStrategy.addOrder(anotherOrder);
    AssemblyTime expectedAssemblyTime =
        new AssemblyTime(FIRST_INITIAL_MODEL_ORDER.getAssemblyTime().inWeeks())
            .add(SECOND_INITIAL_MODEL_ORDER.getAssemblyTime())
            .add(FIRST_INITIAL_MODEL_ORDER.getAssemblyTime());

    // when
    AssemblyTime assemblyTime =
        assemblyLineStrategy.computeRemainingTimeToProduce(anotherOrder.getId());

    // then
    assertThat(assemblyTime).isEqualTo(expectedAssemblyTime);
  }

  @Test
  public void
      givenCurrentOrderIsInAssembly_whenComputeRemainingTimeToProduce_thenReturnRemainingTimeRemainingTimeOfOrderInAssembly() {
    // given
    Order order = createAnOrderWithModelType(FIRST_INITIAL_MODEL_ORDER.getModelType());
    List<ModelOrder> modelsToAssemble = List.of(FIRST_INITIAL_MODEL_ORDER);
    assemblyLineStrategy =
        new JustInTimeModelAssemblyLineStrategy(
            assemblyLineAdapter, modelInventory, modelsToAssemble);
    assemblyLineStrategy.addOrder(order);

    // when
    AssemblyTime assemblyTime = assemblyLineStrategy.computeRemainingTimeToProduce(order.getId());

    // then
    assertThat(assemblyTime).isEqualTo(FIRST_INITIAL_MODEL_ORDER.getAssemblyTime());
  }

  @Test
  public void
      givenModelInInventoryAndAnOrderForThisModel_whenAddOrder_thenOnAdvanceAddModelOrderToModelAssemblyLine() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    when(modelInventory.isInStock(FIRST_MODEL_TYPE)).thenReturn(true);
    JustInTimeModelAssemblyLineStrategy justInTimeModelAssemblyLineStrategy =
        createJustInTimeModelAssemblyStrategyWithoutInitialAssemblyStep();

    // when
    justInTimeModelAssemblyLineStrategy.addOrder(anOrder);

    // then
    justInTimeModelAssemblyLineStrategy.advance();
    verify(modelInventory).removeOne(anOrder.getModelOrder().getModelType());
    verify(assemblyLineAdapter).addOrder(anOrder.getModelOrder());
  }

  @Test
  public void givenOrders_whenGetActiveOrders_thenReturnOrders() {
    // given
    Order order = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    JustInTimeModelAssemblyLineStrategy justInTimeModelAssemblyLineStrategy =
        createJustInTimeModelAssemblyStrategy();
    justInTimeModelAssemblyLineStrategy.addOrder(order);

    // when
    List<Order> activeOrders = justInTimeModelAssemblyLineStrategy.getActiveOrders();

    // then
    assertThat(activeOrders).containsExactly(order);
  }

  private Order createAnOrderWithModelType(String modelType) {
    ModelOrder aModelOrder = new ModelOrderBuilder().withModelName(modelType).build();
    return new OrderBuilder().withModelOrder(aModelOrder).build();
  }

  private JustInTimeModelAssemblyLineStrategy createJustInTimeModelAssemblyStrategy() {
    return new JustInTimeModelAssemblyLineStrategy(
        assemblyLineAdapter, modelInventory, MODELS_TO_ASSEMBLE);
  }

  private JustInTimeModelAssemblyLineStrategy
      createJustInTimeModelAssemblyStrategyWithoutInitialAssemblyStep() {
    return new JustInTimeModelAssemblyLineStrategy(
        assemblyLineAdapter, modelInventory, new ArrayList<>());
  }

  private void setUpFirstModelOrderStillInProgress() {
    given(assemblyLineAdapter.getAssemblyStatus(FIRST_INITIAL_MODEL_ORDER.getOrderId()))
        .willReturn(AssemblyStatus.IN_PROGRESS);
  }
}
