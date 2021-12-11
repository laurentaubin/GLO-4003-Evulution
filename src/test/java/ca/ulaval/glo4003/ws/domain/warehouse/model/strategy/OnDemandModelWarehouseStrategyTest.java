package ca.ulaval.glo4003.ws.domain.warehouse.model.strategy;

import ca.ulaval.glo4003.ws.domain.manufacturer.model.ModelManufacturer;
import ca.ulaval.glo4003.ws.domain.notification.ModelOrderDelayObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelInventoryObserver;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import ca.ulaval.glo4003.ws.fixture.ModelOrderBuilder;
import ca.ulaval.glo4003.ws.fixture.OrderBuilder;
import ca.ulaval.glo4003.ws.infrastructure.manufacturer.model.exception.InvalidModelQuantityInQueueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OnDemandModelWarehouseStrategyTest {
  private static final String AN_ID = "fdsnj9203";
  private static final AssemblyTime AN_ASSEMBLY_TIME = new AssemblyTime(23);

  @Mock private ModelManufacturer modelManufacturer;
  @Mock private ModelInventoryObserver modelAssembledObserver;
  @Mock private ModelOrderDelayObserver modelOrderDelayObserver;

  private OnDemandModelWarehouseStrategy onDemandModelWarehouseStrategy;

  @BeforeEach
  public void setUp() {
    onDemandModelWarehouseStrategy = new OnDemandModelWarehouseStrategy(modelManufacturer);
    onDemandModelWarehouseStrategy.register(modelAssembledObserver);
    onDemandModelWarehouseStrategy.register(modelOrderDelayObserver);
  }

  @Test
  public void givenAnOrder_whenAddOrder_thenOrderIsSentToBeAssembled() {
    // given
    Order order = createOrder(new OrderId(AN_ID));
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(order.getModelOrder().getAssemblyTime()));

    // when
    onDemandModelWarehouseStrategy.addOrder(order);

    // then
    verify(modelManufacturer).addOrder(order.getModelOrder());
  }

  @Test
  public void
      givenModelOrderAssembled_whenListenToModelAssembled_thenObserversNotifyModelInventoryObserver() {
    // given
    ModelOrder modelOrder = new ModelOrderBuilder().build();
    Order order = new OrderBuilder().withModelOrder(modelOrder).build();
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(modelOrder.getAssemblyTime()));
    onDemandModelWarehouseStrategy.addOrder(order);

    // when
    onDemandModelWarehouseStrategy.listenToModelAssembled(modelOrder);

    // then
    verify(modelAssembledObserver).listenToModelInStock(order);
  }

  @Test
  public void givenNoQueuedOrder_whenAddOrder_thenNotifyAssemblyDelayEventIsNeverCalled() {
    // given
    ModelOrder modelOrder = new ModelOrderBuilder().build();
    Order order = new OrderBuilder().withModelOrder(modelOrder).build();
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(modelOrder.getAssemblyTime());

    // when
    onDemandModelWarehouseStrategy.addOrder(order);

    // then
    verify(modelOrderDelayObserver, never()).listenModelOrderDelay(any());
  }

  @Test
  public void givenManufacturerHasModelsInQueue_whenAddOrder_thenOrderRemainingAssemblyTimeIsSet() {
    // given
    Order anOrder = createOrder(new OrderId(AN_ID));
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(123));

    // when
    onDemandModelWarehouseStrategy.addOrder(anOrder);

    // then
    assertThat(anOrder.getAssemblyDelay().inWeeks()).isGreaterThan(0);
  }

  @Test
  public void givenManufacturerHasModelsInQueue_whenAddOrder_thenNotifyModelOrderDelay() {
    // given
    Order anOrder = createOrder(new OrderId(AN_ID));
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(123));

    // when
    onDemandModelWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelOrderDelayObserver).listenModelOrderDelay(anOrder);
  }

  @Test
  public void givenNoOrderInQueue_whenAddOrder_thenOrderHasNoDelay() {
    // given
    Order anOrder = new OrderBuilder().build();
    given(
            modelManufacturer.computeTimeToProduceQuantityOfModel(
                1, anOrder.getModelOrder().getModelType()))
        .willReturn(new AssemblyTime(anOrder.getModelOrder().getAssemblyTime()));
    AssemblyTime expectedDelay = new AssemblyTime(0);

    // when
    onDemandModelWarehouseStrategy.addOrder(anOrder);

    // then
    assertThat(anOrder.getAssemblyDelay()).isEqualTo(expectedDelay);
  }

  @Test
  public void
      givenTwoOrdersOfDifferentTypeInQueue_whenAddOrder_thenOrderHasDelayEqualToPreviousOrderAssemblyTime() {
    // given
    Order anOrder = new OrderBuilder().withModelType("a type").build();
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(anOrder.getModelOrder().getAssemblyTime()));
    onDemandModelWarehouseStrategy.addOrder(anOrder);
    Order anotherOrder = new OrderBuilder().withModelType("another type").build();
    String modelType = anotherOrder.getModelOrder().getModelType();
    onDemandModelWarehouseStrategy.addOrder(anotherOrder);
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(1, modelType))
        .willReturn(AN_ASSEMBLY_TIME);
    AssemblyTime expectedDelay =
        AN_ASSEMBLY_TIME.subtract(anotherOrder.getModelOrder().getAssemblyTime());

    // when
    onDemandModelWarehouseStrategy.addOrder(anotherOrder);

    // then
    assertThat(anotherOrder.getAssemblyDelay()).isEqualTo(expectedDelay);
  }

  @Test
  public void
      givenTwoOrdersOfSameTypeInQueue_whenAddOrder_thenOrderHasDelayEqualToPreviousOrderAssemblyTime() {
    // given
    Order anOrder = new OrderBuilder().withModelType("a type").build();
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
        .willReturn(new AssemblyTime(anOrder.getModelOrder().getAssemblyTime()));
    onDemandModelWarehouseStrategy.addOrder(anOrder);
    Order anotherOrder = new OrderBuilder().withModelType("a type").build();
    String modelType = anotherOrder.getModelOrder().getModelType();
    onDemandModelWarehouseStrategy.addOrder(anotherOrder);
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(2, modelType))
        .willReturn(AN_ASSEMBLY_TIME);
    AssemblyTime expectedDelay =
        AN_ASSEMBLY_TIME.subtract(anotherOrder.getModelOrder().getAssemblyTime());

    // when
    onDemandModelWarehouseStrategy.addOrder(anotherOrder);

    // then
    assertThat(anotherOrder.getAssemblyDelay()).isEqualTo(expectedDelay);
  }

  @Test
  public void givenComputeRemainingTimeWithWrongModelQuantity_whenAddOrder_thenDoNotNotifyDelay() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
      .willThrow(new InvalidModelQuantityInQueueException());
    Order anOrder = new OrderBuilder().withModelType("a type").build();

    // when
    onDemandModelWarehouseStrategy.addOrder(anOrder);

    // then
    verify(modelOrderDelayObserver, never()).listenModelOrderDelay(anOrder);
  }

  @Test
  public void givenComputeRemainingTimeWithWrongModelQuantity_whenAddOrder_thenDoNotAddDelayToOrder() {
    // given
    given(modelManufacturer.computeTimeToProduceQuantityOfModel(any(), any()))
      .willThrow(new InvalidModelQuantityInQueueException());
    Order anOrder = new OrderBuilder().withModelType("a type").build();
    AssemblyTime noDelay = new AssemblyTime(0);

    // when
    onDemandModelWarehouseStrategy.addOrder(anOrder);

    // then
    assertThat(anOrder.getAssemblyDelay()).isEqualTo(noDelay);
  }


  private Order createOrder(OrderId id) {
    ModelOrder modelOrder = new ModelOrderBuilder().build();
    return new OrderBuilder().withOrderId(id).withModelOrder(modelOrder).build();
  }
}
