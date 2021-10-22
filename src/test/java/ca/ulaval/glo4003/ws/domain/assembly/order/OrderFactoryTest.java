package ca.ulaval.glo4003.ws.domain.assembly.order;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

import ca.ulaval.glo4003.ws.api.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.VehicleAssemblyPlanner;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.vehicle.Color;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
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

  @Mock private Model aModel;
  @Mock private Battery aBattery;
  @Mock private LocalDateProvider localDateProvider;
  @Mock private VehicleAssemblyPlanner vehicleAssemblyPlanner;

  private Transaction transaction;
  private OrderFactory orderFactory;

  @BeforeEach
  public void setUp() {
    given(vehicleAssemblyPlanner.getNormalAssemblyTime()).willReturn(new ProductionTime(2));
    given(aModel.getProductionTime()).willReturn(new ProductionTime(1));
    given(aBattery.getProductionTime()).willReturn(new ProductionTime(1));
    transaction = new Transaction(new TransactionId(AN_ID));

    orderFactory = new OrderFactory(localDateProvider, vehicleAssemblyPlanner);
  }

  @Test
  public void givenACompletedTransaction_whenCreate_thenOrderIsCreatedFromTransactionInfo() {
    // given
    Vehicle vehicle = new Vehicle(aModel, A_COLOR);
    transaction.addVehicle(vehicle);
    transaction.addBattery(aBattery);

    // when
    Order order = orderFactory.create(transaction);

    // then
    assertThat(order.getId().toString()).isEqualTo(AN_ID);
    assertThat(order.getModel()).isEqualTo(aModel);
    assertThat(order.getBattery()).isEqualTo(aBattery);
  }

  @Test
  public void givenLocalDate_whenCreate_thenOrderIsCreatedWithRightCreatedDate() {
    // given
    Vehicle vehicle = new Vehicle(aModel, A_COLOR);
    transaction.addVehicle(vehicle);
    transaction.addBattery(aBattery);
    given(localDateProvider.today()).willReturn(TODAY);

    // when
    Order order = orderFactory.create(transaction);

    // then
    assertThat(order.getCreatedAt()).isEqualTo(TODAY);
  }
}
