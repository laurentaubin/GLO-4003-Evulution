package ca.ulaval.glo4003.ws.domain.assembly.model.strategy;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelAssembledObserver;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelInventory;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import ca.ulaval.glo4003.ws.testUtil.ModelBuilder;
import ca.ulaval.glo4003.ws.testUtil.ModelOrderBuilder;
import ca.ulaval.glo4003.ws.testUtil.OrderBuilder;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JustInTimeModelAssemblyStrategyTest {
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
          .withProductionTime(PRODUCTION_TIME_OF_ONE_WEEK)
          .build();
  private static final List<Model> MODELS_TO_ASSEMBLE =
      List.of(FIRST_MODEL, SECOND_MODEL, THIRD_MODEL);
  private static final OrderId FIRST_ORDER_ID = new OrderId("firstOrderId");
  private static final ModelOrder FIRST_MODEL_ORDER =
      new ModelOrderBuilder()
          .withOrderId(FIRST_ORDER_ID)
          .withModelName(FIRST_MODEL_TYPE)
          .withProductionTime(PRODUCTION_TIME_OF_ONE_WEEK)
          .build();
  private static final OrderId SECOND_ORDER_ID = new OrderId("secondOrderId");
  private static final ModelOrder SECOND_MODEL_ORDER =
      new ModelOrderBuilder()
          .withOrderId(SECOND_ORDER_ID)
          .withModelName(SECOND_MODEL_TYPE)
          .withProductionTime(PRODUCTION_TIME_OF_ONE_WEEK)
          .build();
  private static final OrderId THIRD_ORDER_ID = new OrderId("thirdOrderId");
  private static final ModelOrder THIRD_MODEL_ORDER =
      new ModelOrderBuilder()
          .withOrderId(THIRD_ORDER_ID)
          .withModelName(THIRD_MODEL_TYPE)
          .withProductionTime(PRODUCTION_TIME_OF_ONE_WEEK)
          .build();
  private static final OrderId FOURTH_ORDER_ID = new OrderId("fourthOrderId");
  private static final ModelOrder FOURTH_MODEL_ORDER_WITH_FIRST_MODEL_TYPE =
      new ModelOrderBuilder().withOrderId(FOURTH_ORDER_ID).withModelName(FIRST_MODEL_TYPE).build();

  @Mock private ModelAssemblyLineAdapter assemblyLineAdapter;
  @Mock private ModelInventory modelInventory;
  @Mock private ModelAssembledObserver modelAssembledObserver;
  @Mock private ModelAssembledObserver anotherModelAssembledObserver;
  @Mock private ModelOrderFactory modelOrderFactory;

  @Test
  public void whenAddOrder_thenCheckIfModelIsInStock() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK))
        .willReturn(FIRST_MODEL_ORDER);

    JustInTimeModelAssemblyStrategy justInTimeModelAssemblyStrategy =
        createJustInTimeModelAssemblyStrategy();

    // when
    justInTimeModelAssemblyStrategy.addOrder(anOrder);

    // then
    verify(modelInventory).isInStock(FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelIsInStock_whenAddOrder_thenModelIsTakenFromInventory() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(true);
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK))
        .willReturn(FIRST_MODEL_ORDER);

    JustInTimeModelAssemblyStrategy JustInTimeModelAssemblyStrategy =
        createJustInTimeModelAssemblyStrategy();

    // when
    JustInTimeModelAssemblyStrategy.addOrder(anOrder);

    // then
    verify(modelInventory).removeOne(FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelIsNotInStock_whenAddOrder_thenModelIsNotTakenFromInventory() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(false);
    JustInTimeModelAssemblyStrategy JustInTimeModelAssemblyStrategy =
        createJustInTimeModelAssemblyStrategy();

    // when
    JustInTimeModelAssemblyStrategy.addOrder(anOrder);

    // then
    verify(modelInventory, never()).removeOne(FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelIsInStock_whenAddOrder_thenNotifyAllModelAssembledObservers() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(true);

    JustInTimeModelAssemblyStrategy justInTimeModelAssemblyStrategy =
        createJustInTimeModelAssemblyStrategy();
    justInTimeModelAssemblyStrategy.register(modelAssembledObserver);
    justInTimeModelAssemblyStrategy.register(anotherModelAssembledObserver);

    // when
    justInTimeModelAssemblyStrategy.addOrder(anOrder);

    // then
    verify(modelAssembledObserver).listenToModelAssembled(anOrder);
    verify(anotherModelAssembledObserver).listenToModelAssembled(anOrder);
  }

  @Test
  public void givenModelIsNotInStock_whenAddOrder_thenModelAssembledObserversAreNotNotify() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelInventory.isInStock(FIRST_MODEL_TYPE)).willReturn(false);
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK))
        .willReturn(FIRST_MODEL_ORDER);

    JustInTimeModelAssemblyStrategy justInTimeModelAssemblyStrategy =
        createJustInTimeModelAssemblyStrategy();
    justInTimeModelAssemblyStrategy.register(modelAssembledObserver);
    justInTimeModelAssemblyStrategy.register(anotherModelAssembledObserver);

    // when
    justInTimeModelAssemblyStrategy.addOrder(anOrder);

    // then
    verify(modelAssembledObserver, never()).listenToModelAssembled(anOrder);
    verify(anotherModelAssembledObserver, never()).listenToModelAssembled(anOrder);
  }

  @Test
  public void whenAdvance_thenCallAdvanceOnModelAssemblyLine() {
    // given
    JustInTimeModelAssemblyStrategy JustInTimeModelAssemblyStrategy =
        createJustInTimeModelAssemblyStrategy();

    // when
    JustInTimeModelAssemblyStrategy.advance();

    // then
    verify(assemblyLineAdapter).advance();
  }

  @Test
  public void whenCreateAModel_thenSendModelOrderToModelAssemblyLine() {
    // given

    // when
    createJustInTimeModelAssemblyStrategy();

    // then
    verify(assemblyLineAdapter).addOrder(FIRST_MODEL_ORDER);
  }

  @Test
  public void whenAdvance_thenCheckIfCurrentModelBeingAssembledIsDoneBeingAssembled() {
    // given
    JustInTimeModelAssemblyStrategy JustInTimeModelAssemblyStrategy =
        createJustInTimeModelAssemblyStrategy();

    // when
    JustInTimeModelAssemblyStrategy.advance();

    // then
    verify(assemblyLineAdapter).getAssemblyStatus(FIRST_MODEL_ORDER.getOrderId());
  }

  @Test
  public void
      givenCurrentModelOrderStillInProgress_whenAdvance_thenNextModelInModelAssemblyOrderIsNotSentToBeAssembled() {
    // given
    setUpFirstModelOrderStillInProgress();
    JustInTimeModelAssemblyStrategy JustInTimeModelAssemblyStrategy =
        createJustInTimeModelAssemblyStrategy();

    // when
    JustInTimeModelAssemblyStrategy.advance();

    // then
    verify(assemblyLineAdapter, never()).addOrder(SECOND_MODEL_ORDER);
  }

  @Test
  public void givenNoOrder_whenAdvanceMultipleTimes_thenModelsAreAssembledOnce() {
    // given
    when(modelOrderFactory.create(FIRST_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK))
        .thenReturn(FIRST_MODEL_ORDER, FOURTH_MODEL_ORDER_WITH_FIRST_MODEL_TYPE);
    given(assemblyLineAdapter.getAssemblyStatus(FIRST_ORDER_ID))
        .willReturn(AssemblyStatus.ASSEMBLED);
    setUpSecondModelOrder();
    setUpThirdModelOrder();

    JustInTimeModelAssemblyStrategy JustInTimeModelAssemblyStrategy =
        createJustInTimeModelAssemblyStrategy();

    // when
    JustInTimeModelAssemblyStrategy.advance();
    JustInTimeModelAssemblyStrategy.advance();
    JustInTimeModelAssemblyStrategy.advance();

    verify(assemblyLineAdapter, times(1)).addOrder(FIRST_MODEL_ORDER);
    verify(assemblyLineAdapter, times(1)).addOrder(SECOND_MODEL_ORDER);
    verify(assemblyLineAdapter, times(1)).addOrder(THIRD_MODEL_ORDER);
    verify(assemblyLineAdapter, never()).addOrder(FOURTH_MODEL_ORDER_WITH_FIRST_MODEL_TYPE);
  }

  @Test
  public void givenModelStillInProgress_whenAdvance_thenModelIsNotAddedToInventory() {
    // given
    setUpFirstModelOrderStillInProgress();
    JustInTimeModelAssemblyStrategy JustInTimeModelAssemblyStrategy =
        createJustInTimeModelAssemblyStrategy();

    // when
    JustInTimeModelAssemblyStrategy.advance();

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
    JustInTimeModelAssemblyStrategy JustInTimeModelAssemblyStrategy =
        createJustInTimeModelAssemblyStrategy();
    JustInTimeModelAssemblyStrategy.addOrder(firstOrder);
    JustInTimeModelAssemblyStrategy.addOrder(secondOrder);
    JustInTimeModelAssemblyStrategy.register(modelAssembledObserver);

    // when
    JustInTimeModelAssemblyStrategy.advance();

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

    given(assemblyLineAdapter.getAssemblyStatus(FIRST_ORDER_ID))
        .willReturn(AssemblyStatus.ASSEMBLED);
    JustInTimeModelAssemblyStrategy justInTimeModelAssemblyStrategy =
        createJustInTimeModelAssemblyStrategy();
    justInTimeModelAssemblyStrategy.addOrder(firstOrder);
    justInTimeModelAssemblyStrategy.addOrder(secondOrder);

    // when
    justInTimeModelAssemblyStrategy.advance();

    // then
    verify(modelInventory).addOne(FIRST_MODEL_TYPE);
  }

  @Test
  public void
      givenOneOrderInQueue_whenComputeRemainingTimeToProduce_thenReturnOrderRemainingTimeToProduce() {
    // given
    Order order = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    given(modelOrderFactory.create(FIRST_MODEL.getName(), FIRST_MODEL.getProductionTime()))
        .willReturn(FIRST_MODEL_ORDER);
    List<Model> modelsToAssemble = List.of(FIRST_MODEL);
    JustInTimeModelAssemblyStrategy justInTimeModelAssemblyStrategy =
        new JustInTimeModelAssemblyStrategy(
            assemblyLineAdapter, modelInventory, modelOrderFactory, modelsToAssemble);
    justInTimeModelAssemblyStrategy.addOrder(order);

    // when
    ProductionTime productionTime =
        justInTimeModelAssemblyStrategy.computeRemainingTimeToProduce(order.getId());

    // then
    assertThat(productionTime).isEqualTo(order.getModelOrder().getProductionTime());
  }

  @Test
  public void
      givenMultipleOrdersRequestingTheSameModelTypeAndMultipleModelsToAssemble_whenComputeRemainingTimeToProduce_thenReturnCorrectRemainingTimeToProduce() {
    // given
    Order order = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    Order anotherOrder =
        new OrderBuilder().withOrderId(SECOND_ORDER_ID).withModelOrder(FIRST_MODEL_ORDER).build();

    given(modelOrderFactory.create(FIRST_MODEL.getName(), FIRST_MODEL.getProductionTime()))
        .willReturn(FIRST_MODEL_ORDER);
    given(modelOrderFactory.create(SECOND_MODEL.getName(), SECOND_MODEL.getProductionTime()))
        .willReturn(SECOND_MODEL_ORDER);
    List<Model> modelsToAssemble = List.of(FIRST_MODEL, SECOND_MODEL);
    JustInTimeModelAssemblyStrategy justInTimeModelAssemblyStrategy =
        new JustInTimeModelAssemblyStrategy(
            assemblyLineAdapter, modelInventory, modelOrderFactory, modelsToAssemble);
    justInTimeModelAssemblyStrategy.addOrder(order);
    justInTimeModelAssemblyStrategy.addOrder(anotherOrder);

    // when
    ProductionTime productionTime =
        justInTimeModelAssemblyStrategy.computeRemainingTimeToProduce(anotherOrder.getId());

    // then
    ProductionTime expectedProductionTime =
        new ProductionTime(FIRST_MODEL.getProductionTime().inWeeks())
            .add(SECOND_MODEL.getProductionTime())
            .add(FIRST_MODEL.getProductionTime());
    assertThat(productionTime).isEqualTo(expectedProductionTime);
  }

  @Test
  public void
      givenCurrentOrderIsInAssembly_whenComputeRemainingTimeToProduce_thenReturnRemainingTimeRemainingTimeOfOrderInAssembly() {
    // given
    given(modelOrderFactory.create(FIRST_MODEL.getName(), FIRST_MODEL.getProductionTime()))
        .willReturn(FIRST_MODEL_ORDER);
    List<Model> modelsToAssemble = List.of(FIRST_MODEL);
    JustInTimeModelAssemblyStrategy justInTimeModelAssemblyStrategy =
        new JustInTimeModelAssemblyStrategy(
            assemblyLineAdapter, modelInventory, modelOrderFactory, modelsToAssemble);
    Order order = createAnOrderWithModelType(FIRST_MODEL.getName());
    justInTimeModelAssemblyStrategy.addOrder(order);

    // when
    ProductionTime productionTime =
        justInTimeModelAssemblyStrategy.computeRemainingTimeToProduce(order.getId());

    // then
    assertThat(productionTime).isEqualTo(FIRST_MODEL.getProductionTime());
  }

  @Test
  public void
      givenModelInInventoryAndAnOrderForThisModel_whenAddOrder_thenOnAdvanceAddModelOrderToModelAssemblyLine() {
    // given
    Order anOrder = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    when(modelInventory.isInStock(FIRST_MODEL_TYPE)).thenReturn(true);
    JustInTimeModelAssemblyStrategy justInTimeModelAssemblyStrategy =
        createJustInTimeModelAssemblyStrategyWithoutInitialAssemblyStep();

    // when
    justInTimeModelAssemblyStrategy.addOrder(anOrder);

    // then
    justInTimeModelAssemblyStrategy.advance();
    verify(modelInventory).removeOne(anOrder.getModelOrder().getModelType());
    verify(assemblyLineAdapter).addOrder(anOrder.getModelOrder());
  }

  @Test
  public void givenOrders_whenGetActiveOrders_thenReturnOrders() {
    // given
    Order order = createAnOrderWithModelType(FIRST_MODEL_TYPE);
    JustInTimeModelAssemblyStrategy justInTimeModelAssemblyStrategy =
        createJustInTimeModelAssemblyStrategy();
    justInTimeModelAssemblyStrategy.addOrder(order);

    // when
    List<Order> activeOrders = justInTimeModelAssemblyStrategy.getActiveOrders();

    // then
    assertThat(activeOrders).containsExactly(order);
  }

  private Order createAnOrderWithModelType(String modelType) {
    ModelOrder aModelOrder = new ModelOrderBuilder().withModelName(modelType).build();
    return new OrderBuilder().withModelOrder(aModelOrder).build();
  }

  private JustInTimeModelAssemblyStrategy createJustInTimeModelAssemblyStrategy() {
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK))
        .willReturn(FIRST_MODEL_ORDER);
    given(modelOrderFactory.create(SECOND_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK))
        .willReturn(SECOND_MODEL_ORDER);
    given(modelOrderFactory.create(THIRD_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK))
        .willReturn(THIRD_MODEL_ORDER);
    return new JustInTimeModelAssemblyStrategy(
        assemblyLineAdapter, modelInventory, modelOrderFactory, MODELS_TO_ASSEMBLE);
  }

  private JustInTimeModelAssemblyStrategy
      createJustInTimeModelAssemblyStrategyWithoutInitialAssemblyStep() {
    return new JustInTimeModelAssemblyStrategy(
        assemblyLineAdapter, modelInventory, modelOrderFactory, new ArrayList<>());
  }

  private void setUpFirstModelOrderAssembledWhenAdvance() {
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK))
        .willReturn(FIRST_MODEL_ORDER);
    given(assemblyLineAdapter.getAssemblyStatus(FIRST_ORDER_ID))
        .willReturn(AssemblyStatus.ASSEMBLED);
  }

  private void setUpFirstModelOrderStillInProgress() {
    given(modelOrderFactory.create(FIRST_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK))
        .willReturn(FIRST_MODEL_ORDER);
    given(assemblyLineAdapter.getAssemblyStatus(FIRST_ORDER_ID))
        .willReturn(AssemblyStatus.IN_PROGRESS);
  }

  private void setUpSecondModelOrder() {
    given(modelOrderFactory.create(SECOND_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK))
        .willReturn(SECOND_MODEL_ORDER);
    given(assemblyLineAdapter.getAssemblyStatus(SECOND_MODEL_ORDER.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);
  }

  private void setUpThirdModelOrder() {
    given(modelOrderFactory.create(THIRD_MODEL_TYPE, PRODUCTION_TIME_OF_ONE_WEEK))
        .willReturn(THIRD_MODEL_ORDER);
    given(assemblyLineAdapter.getAssemblyStatus(THIRD_MODEL_ORDER.getOrderId()))
        .willReturn(AssemblyStatus.ASSEMBLED);
  }
}
