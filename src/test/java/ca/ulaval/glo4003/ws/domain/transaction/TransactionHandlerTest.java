package ca.ulaval.glo4003.ws.domain.transaction;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionHandlerTest {
  private static final String A_MODEL = "Vandry";
  private static final String A_COLOR = "Color";

  @Mock private Transaction transaction;

  private TransactionHandler transactionHandler;

  @BeforeEach
  void setUp() {
    transactionHandler = new TransactionHandler();
  }

  @Test
  void whenCreateTransaction_thenCreatedTransactionShouldHaveTransactionId() {
    // when
    var transaction = transactionHandler.createTransaction();

    // then
    assertThat(transaction).isNotNull();
    assertThat(transaction.getId()).isNotNull();
  }

  @Test
  void givenVehicle_whenSetVehicle_thenSetVehicle() {
    // given
    var vehicle = createVehicle();

    // when
    transactionHandler.setVehicle(transaction, vehicle);

    // then
    verify(transaction).setVehicle(vehicle);
  }

  private Vehicle createVehicle() {
    return new Vehicle(Model.fromString(A_MODEL), new Color(A_COLOR));
  }
}
