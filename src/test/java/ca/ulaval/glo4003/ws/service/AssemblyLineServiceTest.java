package ca.ulaval.glo4003.ws.service;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.assembly.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.assembly.battery.BatteryOrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.time.AssemblyTime;
import ca.ulaval.glo4003.ws.domain.assembly.time.AssemblyTimeFactory;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.vehicle.Color;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import ca.ulaval.glo4003.ws.testUtil.BatteryBuilder;
import ca.ulaval.glo4003.ws.testUtil.ModelBuilder;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssemblyLineServiceTest {
  private static final String AN_ID = "an id";
  private static final TransactionId TRANSACTION_ID = new TransactionId(AN_ID);
  private static final Model MODEL =
      new ModelBuilder().withProductionTime(new ProductionTime(1)).build();
  private static final Battery BATTERY =
      new BatteryBuilder().withProductionTime(new ProductionTime(2)).build();

  @Mock private OrderFactory orderFactory;
  @Mock private Order anOrder;
  @Mock private AssemblyStrategy assemblyStrategy;
  @Mock private ModelOrderFactory modelOrderFactory;
  @Mock private BatteryOrderFactory batteryOrderFactory;
  @Mock private AssemblyTimeFactory assemblyTimeFactory;
  @Mock private Transaction transaction;
  @Mock private ModelOrder modelOrder;
  @Mock private BatteryOrder batteryOrder;
  @Mock private AssemblyTime modelAssemblyTime;
  @Mock private AssemblyTime batteryAssemblyTime;

  private AssemblyLineService assemblyLine;
  private Vehicle vehicle;

  @BeforeEach
  public void setUp() {
    vehicle = new Vehicle(MODEL, Color.WHITE);
    vehicle.addBattery(BATTERY);

    assemblyLine =
        new AssemblyLineService(
            orderFactory,
            assemblyStrategy,
            modelOrderFactory,
            batteryOrderFactory,
            assemblyTimeFactory);
  }

  @Test
  public void whenListenToTransactionCompleted_thenOrderIsSent() {
    // given
    given(transaction.getId()).willReturn(TRANSACTION_ID);
    given(transaction.getVehicle()).willReturn(vehicle);
    given(assemblyTimeFactory.create(MODEL.getProductionTime().inWeeks()))
        .willReturn(modelAssemblyTime);
    given(assemblyTimeFactory.create(BATTERY.getProductionTime().inWeeks()))
        .willReturn(batteryAssemblyTime);
    given(modelOrderFactory.create(MODEL.getName(), modelAssemblyTime)).willReturn(modelOrder);
    given(batteryOrderFactory.create(BATTERY.getType(), batteryAssemblyTime))
        .willReturn(batteryOrder);
    given(orderFactory.create(AN_ID, modelOrder, batteryOrder)).willReturn(anOrder);

    // when
    assemblyLine.listenToTransactionCompleted(transaction);

    // then
    verify(assemblyStrategy).addOrder(anOrder);
  }

  @Test
  public void whenAdvance_thenCallAdvanceOnAssemblyStrategy() {
    // when
    assemblyLine.advance();

    // then
    verify(assemblyStrategy).advance();
  }

  @Test
  public void whenShutdown_thenCallShutdownOnAssemblyStrategy() {
    // when
    assemblyLine.shutdown();

    // then
    verify(assemblyStrategy).shutdown();
  }

  @Test
  public void whenActivate_thenCallReactivateOnAssemblyStrategy() {
    // when
    assemblyLine.activate();

    // then
    verify(assemblyStrategy).reactivate();
  }

  @Test
  public void givenActiveOrdersInAssembly_whenGetActiveOrders_thenShouldReturnActiveOrders() {
    // given
    var expectedOrders = new ArrayList<>(List.of(anOrder));
    given(assemblyStrategy.getActiveOrders()).willReturn(expectedOrders);

    // when
    var result = assemblyLine.getActiveOrders();

    // then
    assertThat(result).isEqualTo(expectedOrders);
  }
}
