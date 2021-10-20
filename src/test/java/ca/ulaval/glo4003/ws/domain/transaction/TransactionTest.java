package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.transaction.exception.CannotAddBatteryBeforeVehicleException;
import ca.ulaval.glo4003.ws.domain.transaction.exception.IncompleteTransactionException;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionTest {
  private static final BigDecimal A_RANGE = BigDecimal.valueOf(432243);

  @Mock private TransactionId transactionId;
  @Mock private Vehicle aVehicle;
  @Mock private Battery aBattery;
  @Mock private Payment aPayment;

  private Transaction transaction;

  @BeforeEach
  public void setUpTransaction() {
    transaction = new Transaction(transactionId);
  }

  @Test
  public void givenAVehicle_whenAddVehicle_thenVehicleIsAddedToTransaction() {
    // when
    transaction.addVehicle(aVehicle);

    // then
    assertThat(transaction.getVehicle()).isEqualTo(aVehicle);
  }

  @Test
  public void givenAVehicle_whenAddBattery_thenBatteryIsAddedToVehicle() {
    // given
    transaction.addVehicle(aVehicle);

    // when
    transaction.addBattery(aBattery);

    // then
    verify(aVehicle).addBattery(aBattery);
  }

  @Test
  public void givenNoVehicle_whenAddBattery_thenThrowCannotAddBatteryBeforeVehicleException() {
    // when
    Executable addingBattery = () -> transaction.addBattery(aBattery);

    // then
    assertThrows(CannotAddBatteryBeforeVehicleException.class, addingBattery);
  }

  @Test
  public void
      givenAVehicleWithBattery_whenComputedEstimateVehicleRange_thenReturnVehicleEstimatedRange() {
    // given
    given(aVehicle.computeRange()).willReturn(A_RANGE);
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
    aVehicle.addBattery(aBattery);
    transaction.addVehicle(aVehicle);
    given(aVehicle.hasBattery()).willReturn(false);

    // when
    Executable computingEstimatedRange = () -> transaction.computeEstimatedVehicleRange();

    // then
    assertThrows(IncompleteTransactionException.class, computingEstimatedRange);
  }

  @Test
  public void givenNoVehicle_whenAddPayment_thenThrowIncompleteTransactionException() {
    // when
    Executable addingPayment = () -> transaction.addPayment(aPayment);

    // then
    assertThrows(IncompleteTransactionException.class, addingPayment);
  }

  @Test
  public void givenAVehicleWithoutBattery_whenAddPayment_thenThrowIncompleteTransactionException() {
    // given
    transaction.addVehicle(aVehicle);
    given(aVehicle.hasBattery()).willReturn(false);

    // when
    Executable addingPayment = () -> transaction.addPayment(aPayment);

    // then
    assertThrows(IncompleteTransactionException.class, addingPayment);
  }

  @Test
  public void
      givenAVehicleWithBattery_whenAddPayment_thenDoNotThrowIncompleteTransactionException() {
    // given
    givenATransactionReadyToBeCompleted();

    // when
    Executable addingPayment = () -> transaction.addPayment(aPayment);

    // then
    assertDoesNotThrow(addingPayment);
  }

  @Test
  public void givenValidTransaction_whenAddPayment_thenPaymentIsAddedToTransaction() {
    // given
    givenATransactionReadyToBeCompleted();

    // when
    transaction.addPayment(aPayment);

    // then
    assertThat(transaction.getPayment()).isEqualTo(aPayment);
  }

  private void givenATransactionReadyToBeCompleted() {
    given(aVehicle.hasBattery()).willReturn(true);
    transaction.addVehicle(aVehicle);
  }
}
