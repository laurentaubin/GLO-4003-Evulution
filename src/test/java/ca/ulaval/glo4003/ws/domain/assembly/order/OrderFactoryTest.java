package ca.ulaval.glo4003.ws.domain.assembly.order;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

import ca.ulaval.glo4003.ws.api.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.assembly.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.VehicleAssemblyPlanner;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.vehicle.Color;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderFactoryTest {
  private static final String AN_ID = "3524133";
  private static final Color A_COLOR = Color.WHITE;
  private static final LocalDate TODAY = LocalDate.of(10, 10, 10);

  @Mock private ModelOrder aModelOrder;
  @Mock private BatteryOrder aBatteryOrder;
  @Mock private LocalDateProvider localDateProvider;
  @Mock private VehicleAssemblyPlanner vehicleAssemblyPlanner;

  private Transaction transaction;
  private OrderFactory orderFactory;

  @BeforeEach
  public void setUp() {
    given(vehicleAssemblyPlanner.getNormalAssemblyTime()).willReturn(new ProductionTime(2));
    given(aModelOrder.getProductionTime()).willReturn(new ProductionTime(1));
    given(aBatteryOrder.getProductionTime()).willReturn(new ProductionTime(1));
    transaction = new Transaction(new TransactionId(AN_ID));

    orderFactory = new OrderFactory(localDateProvider, vehicleAssemblyPlanner);
  }

  @Test
  public void whenCreate_thenOrderIsCreatedFromTransactionInfo() {
    // when
    Order order = orderFactory.create(transaction.getId().toString(), aModelOrder, aBatteryOrder);

    // then
    assertThat(order.getId().toString()).isEqualTo(AN_ID);
    assertThat(order.getModelOrder()).isEqualTo(aModelOrder);
    assertThat(order.getBatteryOrder()).isEqualTo(aBatteryOrder);
  }

  @Test
  public void givenLocalDate_whenCreate_thenOrderIsCreatedWithRightCreatedDate() {
    // given
    given(localDateProvider.today()).willReturn(TODAY);

    // when
    Order order = orderFactory.create(transaction.getId().toString(), aModelOrder, aBatteryOrder);

    // then
    assertThat(order.getCreatedAt()).isEqualTo(TODAY);
  }
}
