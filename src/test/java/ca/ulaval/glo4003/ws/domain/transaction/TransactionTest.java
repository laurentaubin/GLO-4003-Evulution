package ca.ulaval.glo4003.ws.domain.transaction;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.transaction.exception.CannotAddBatteryBeforeVehicleException;
import ca.ulaval.glo4003.ws.domain.transaction.exception.IncompleteTransactionException;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Payment;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionTest {
  private static final BigDecimal A_RANGE = BigDecimal.valueOf(432243);

  @Mock private TransactionId transactionId;
  @Mock private Vehicle vehicle;
  @Mock private Battery battery;
  @Mock private Payment payment;

  private Transaction transaction;

  @BeforeEach
  public void setUpTransaction() {
    transaction = new Transaction(transactionId);
  }

  @Test
  public void givenAVehicle_whenAddVehicle_thenVehicleIsAddedToTransaction() {
    // when
    transaction.addVehicle(vehicle);

    // then
    assertThat(transaction.getVehicle()).isEqualTo(vehicle);
  }

  @Test
  public void givenAVehicle_whenAddBattery_thenBatteryIsAddedToVehicle() {
    // given
    transaction.addVehicle(vehicle);

    // when
    transaction.addBattery(battery);

    // then
    verify(vehicle).addBattery(battery);
  }

  @Test
  public void givenNoVehicle_whenAddBattery_thenThrowCannotAddBatteryBeforeVehicleException() {
    // when
    Executable addingBattery = () -> transaction.addBattery(battery);

    // then
    assertThrows(CannotAddBatteryBeforeVehicleException.class, addingBattery);
  }

  @Test
  public void
      givenAVehicleWithBattery_whenComputedEstimateVehicleRange_thenReturnVehicleEstimatedRange() {
    // given
    given(vehicle.computeRange()).willReturn(A_RANGE);
    givenATransactionReadyToBeCompleted();

    // when
    BigDecimal estimatedRange = transaction.computeEstimatedVehicleRange();

    // then
    assertThat(estimatedRange).isEqualTo(A_RANGE);
  }

  @Test
  public void
      givenAVehicleWithoutBattery_whenComputeEstimatedVehicleRange_thenThrowIncompleteTransactionException() {
    // given
    vehicle.addBattery(battery);
    transaction.addVehicle(vehicle);
    given(vehicle.hasBattery()).willReturn(false);

    // when
    Executable computingEstimatedRange = () -> transaction.computeEstimatedVehicleRange();

    // then
    assertThrows(IncompleteTransactionException.class, computingEstimatedRange);
  }

  @Test
  public void givenNoVehicle_whenAddPayment_thenThrowIncompleteTransactionException() {
    // when
    Executable addingPayment = () -> transaction.addPayment(payment);

    // then
    assertThrows(IncompleteTransactionException.class, addingPayment);
  }

  @Test
  public void givenAVehicleWithoutBattery_whenAddPayment_thenThrowIncompleteTransactionException() {
    // given
    transaction.addVehicle(vehicle);
    given(vehicle.hasBattery()).willReturn(false);

    // when
    Executable addingPayment = () -> transaction.addPayment(payment);

    // then
    assertThrows(IncompleteTransactionException.class, addingPayment);
  }

  @Test
  public void
      givenAVehicleWithBattery_whenAddPayment_thenDoNotThrowIncompleteTransactionException() {
    // given
    givenATransactionReadyToBeCompleted();

    // when
    Executable addingPayment = () -> transaction.addPayment(payment);

    // then
    assertDoesNotThrow(addingPayment);
  }

  @Test
  public void givenValidTransaction_whenAddPayment_thenPaymentIsAddedToTransaction() {
    // given
    givenATransactionReadyToBeCompleted();

    // when
    transaction.addPayment(payment);

    // then
    assertThat(transaction.getPayment()).isEqualTo(payment);
  }

  private void givenATransactionReadyToBeCompleted() {
    given(vehicle.hasBattery()).willReturn(true);
    transaction.addVehicle(vehicle);
  }
}
