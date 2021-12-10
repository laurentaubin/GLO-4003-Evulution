package ca.ulaval.glo4003.ws.service.warehouse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrderFactory;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrderFactory;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.warehouse.strategy.WarehouseStrategy;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTimeFactory;
import ca.ulaval.glo4003.ws.fixture.TransactionFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

  @Mock private OrderFactory orderFactory;
  @Mock private WarehouseStrategy warehouseStrategy;
  @Mock private ModelOrderFactory modelOrderFactory;
  @Mock private BatteryOrderFactory batteryOrderFactory;
  @Mock private AssemblyTimeFactory assemblyTimeFactory;
  @Mock private ModelOrder modelOrder;
  @Mock private BatteryOrder batteryOrder;
  @Mock private Order order;

  private WarehouseService warehouseService;

  @BeforeEach
  public void setUp() {
    warehouseService =
        new WarehouseService(
            orderFactory,
            warehouseStrategy,
            modelOrderFactory,
            batteryOrderFactory,
            assemblyTimeFactory);
  }

  @Test
  public void
      givenACompletedTransaction_whenListenToTransactionCompleted_thenCreateOrderFromCompletedTransaction() {
    // given
    Transaction aTransaction = new TransactionFixture().givenACompleteTransaction();
    given(modelOrderFactory.create(any(), any())).willReturn(modelOrder);
    given(batteryOrderFactory.create(any(), any())).willReturn(batteryOrder);

    // when
    warehouseService.listenToTransactionCompleted(aTransaction);

    // then
    verify(orderFactory).create(aTransaction.getId().toString(), modelOrder, batteryOrder);
  }

  @Test
  public void
      givenACompletedTransaction_whenListenToTransactionCompleted_thenAddOrderToWarehouse() {
    // given
    Transaction aTransaction = new TransactionFixture().givenACompleteTransaction();
    given(modelOrderFactory.create(any(), any())).willReturn(modelOrder);
    given(batteryOrderFactory.create(any(), any())).willReturn(batteryOrder);
    given(orderFactory.create(any(), any(), any())).willReturn(order);

    // when
    warehouseService.listenToTransactionCompleted(aTransaction);

    // then
    verify(warehouseStrategy).addOrder(order);
  }
}
