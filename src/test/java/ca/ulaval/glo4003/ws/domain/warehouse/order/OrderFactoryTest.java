package ca.ulaval.glo4003.ws.domain.warehouse.order;

import ca.ulaval.glo4003.ws.domain.manufacturer.vehicle.VehicleAssemblyPlanner;
import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderFactoryTest {
  private static final String AN_ID = "3524133";
  private static final LocalDate A_PRESENT_DATE = LocalDate.of(10, 10, 10);

  @Mock private ModelOrder modelOrder;
  @Mock private BatteryOrder batteryOrder;
  @Mock private LocalDateProvider localDateProvider;
  @Mock private VehicleAssemblyPlanner vehicleAssemblyPlanner;

  private Transaction transaction;
  private OrderFactory orderFactory;

  @BeforeEach
  public void setUp() {
    given(vehicleAssemblyPlanner.getNormalAssemblyTime()).willReturn(new AssemblyTime(2));
    given(modelOrder.getAssemblyTime()).willReturn(new AssemblyTime(1));
    given(batteryOrder.getAssemblyTime()).willReturn(new AssemblyTime(1));
    transaction = new Transaction(new TransactionId(AN_ID));

    orderFactory = new OrderFactory(localDateProvider, vehicleAssemblyPlanner);
  }

  @Test
  public void whenCreate_thenOrderIsCreatedFromTransactionInfo() {
    // when
    Order order = orderFactory.create(transaction.getId().toString(), modelOrder, batteryOrder);

    // then
    assertThat(order.getId().toString()).isEqualTo(AN_ID);
    assertThat(order.getModelOrder()).isEqualTo(modelOrder);
    assertThat(order.getBatteryOrder()).isEqualTo(batteryOrder);
  }

  @Test
  public void givenLocalDate_whenCreate_thenOrderIsCreatedWithRightCreatedDate() {
    // given
    given(localDateProvider.today()).willReturn(A_PRESENT_DATE);

    // when
    Order order = orderFactory.create(transaction.getId().toString(), modelOrder, batteryOrder);

    // then
    assertThat(order.getCreatedAt()).isEqualTo(A_PRESENT_DATE);
  }
}
