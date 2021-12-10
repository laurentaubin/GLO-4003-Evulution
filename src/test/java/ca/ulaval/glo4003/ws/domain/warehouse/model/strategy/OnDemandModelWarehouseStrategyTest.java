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

    // when
    onDemandModelWarehouseStrategy.addOrder(order);

    // then
    verify(modelManufacturer).addOrder(order.getModelOrder());
  }

  @Test
  public void
      givenAnOrderSentToBeAssembledAndNoDelay_whenComputeEstimatedTime_thenReturnTheModelRemainingTimeToProduce() {
    // given
    Order order = createOrder(new OrderId(AN_ID));
    onDemandModelWarehouseStrategy.addOrder(order);

    // when
    AssemblyTime delay =
        onDemandModelWarehouseStrategy.computeRemainingTimeToProduce(order.getId());

    // then
    assertThat(delay.inWeeks()).isEqualTo(0);
  }

  @Test
  public void
      givenModelOrderAssembled_whenListenToModelAssembled_thenObserversNotifyModelInventoryObserver() {
    // given
    ModelOrder modelOrder = new ModelOrderBuilder().build();
    Order order = new OrderBuilder().withModelOrder(modelOrder).build();
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

    // when
    onDemandModelWarehouseStrategy.addOrder(order);

    // then
    verify(modelOrderDelayObserver, never()).listenModelOrderDelay(any());
  }

  @Test
  public void givenQueuedOrders_whenAddOrder_thenNotifyAssemblyDelayForAddedOrder() {
    // given
    ModelOrder modelOrder = new ModelOrderBuilder().build();
    Order order = new OrderBuilder().withModelOrder(modelOrder).build();
    ModelOrder anotherModelOrder = new ModelOrderBuilder().build();
    Order anotherOrder = new OrderBuilder().withModelOrder(anotherModelOrder).build();
    given(modelManufacturer.computeRemainingTimeToProduceNextModelType(modelOrder.getModelType()))
        .willReturn(modelOrder.getAssemblyTime());
    onDemandModelWarehouseStrategy.addOrder(order);

    // when
    onDemandModelWarehouseStrategy.addOrder(anotherOrder);

    // then
    verify(modelOrderDelayObserver).listenModelOrderDelay(anotherOrder);
  }

  @Test
  public void givenQueuedOrder_whenAddOrder_thenComputedDelayIsAddedToQueuedOrder() {
    // given
    OrderId id = new OrderId("id");
    Order firstOrder = createOrder(id);
    OrderId secondId = new OrderId("secondId");
    Order secondOrder = createOrder(secondId);
    OrderId thirdId = new OrderId("thirdId");
    Order thirdOrder = createOrder(thirdId);
    given(
            modelManufacturer.computeRemainingTimeToProduceNextModelType(
                firstOrder.getModelOrder().getModelType()))
        .willReturn(firstOrder.getModelOrder().getAssemblyTime());
    onDemandModelWarehouseStrategy.addOrder(firstOrder);
    onDemandModelWarehouseStrategy.addOrder(secondOrder);
    onDemandModelWarehouseStrategy.addOrder(thirdOrder);
    AssemblyTime expectedDelay =
        firstOrder
            .getModelOrder()
            .getAssemblyTime()
            .add(secondOrder.getModelOrder().getAssemblyTime());

    // when
    AssemblyTime assemblyTime =
        onDemandModelWarehouseStrategy.computeRemainingTimeToProduce(thirdId);

    // then
    assertThat(assemblyTime.inWeeks()).isEqualTo(expectedDelay.inWeeks());
  }

  private Order createOrder(OrderId id) {
    ModelOrder modelOrder = new ModelOrderBuilder().build();
    return new OrderBuilder().withOrderId(id).withModelOrder(modelOrder).build();
  }
}
