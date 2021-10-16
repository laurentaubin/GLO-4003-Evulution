package ca.ulaval.glo4003.ws.domain.assembly.order;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.vehicle.Color;
import ca.ulaval.glo4003.ws.domain.vehicle.Model;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderFactoryTest {
  private static final String AN_ID = "3524133";
  private static final Color A_COLOR = Color.WHITE;

  @Mock private Model aModel;
  @Mock private Battery aBattery;
  @Mock private OrderId anOrderId;

  private Transaction transaction;
  private OrderFactory orderFactory;

  @BeforeEach
  public void setUp() {
    transaction = new Transaction(new TransactionId(AN_ID));
    orderFactory = new OrderFactory();
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
}
