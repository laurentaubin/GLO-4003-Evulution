package ca.ulaval.glo4003.ws.infrastructure.transaction;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Payment;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransactionAssemblerTest {
  @Mock private TransactionId transactionId;
  @Mock private Vehicle vehicle;
  @Mock private Payment payment;

  private TransactionAssembler transactionAssembler;

  @BeforeEach
  public void setUp() {
    transactionAssembler = new TransactionAssembler();
  }

  @Test
  public void
      givenTransactionDtoWithAllAttributes_whenAssemble_thenReturnTransactionWithSameAttributes() {
    // given
    given(vehicle.hasBattery()).willReturn(true);
    TransactionDto transactionDto = new TransactionDto(transactionId, vehicle, payment);

    // when
    Transaction transaction = transactionAssembler.assemble(transactionDto);

    // then
    assertThat(transaction.getId()).isEqualTo(transactionDto.getId());
    assertThat(transaction.getVehicle()).isEqualTo(transactionDto.getVehicle());
    assertThat(transaction.getPayment()).isEqualTo(transactionDto.getPayment());
  }

  @Test
  public void
      givenTransactionDtoWithoutPayment_whenAssemble_thenReturnTransactionWithNullPayment() {
    // given
    given(vehicle.hasBattery()).willReturn(true);
    TransactionDto transactionDto = new TransactionDto(transactionId, vehicle, null);

    // when
    Transaction transaction = transactionAssembler.assemble(transactionDto);

    // then
    assertThat(transaction.getId()).isEqualTo(transactionDto.getId());
    assertThat(transaction.getVehicle()).isEqualTo(transactionDto.getVehicle());
    assertThat(transaction.getPayment()).isNull();
  }

  @Test
  public void
      givenTransactionDtoWithoutBattery_whenAssemble_thenReturnTransactionWithNullPayment() {
    // given
    given(vehicle.hasBattery()).willReturn(false);
    TransactionDto transactionDto = new TransactionDto(transactionId, vehicle, null);

    // when
    Transaction transaction = transactionAssembler.assemble(transactionDto);

    // then
    assertThat(transaction.getId()).isEqualTo(transactionDto.getId());
    assertThat(transaction.getVehicle()).isEqualTo(transactionDto.getVehicle());
    assertThat(transaction.getPayment()).isNull();
  }

  @Test
  public void
      givenTransactionDtoWithoutVehicle_whenAssemble_thenReturnTransactionWithNullVehicle() {
    // given
    TransactionDto transactionDto = new TransactionDto(transactionId, null, null);

    // when
    Transaction transaction = transactionAssembler.assemble(transactionDto);

    // then
    assertThat(transaction.getId()).isEqualTo(transactionDto.getId());
    assertThat(transaction.getVehicle()).isNull();
    assertThat(transaction.getPayment()).isNull();
  }

  @Test
  public void givenTransaction_whenAssemble_thenReturnTransactionDtoWithSameAttributes() {
    // given
    given(vehicle.hasBattery()).willReturn(true);
    Transaction transaction = new Transaction(transactionId);
    transaction.addVehicle(vehicle);
    transaction.addPayment(payment);

    // when
    TransactionDto transactionDto = transactionAssembler.assemble(transaction);

    // then
    assertThat(transactionDto.getId()).isEqualTo(transaction.getId());
    assertThat(transactionDto.getVehicle()).isEqualTo(transaction.getVehicle());
    assertThat(transactionDto.getPayment()).isEqualTo(transaction.getPayment());
  }
}
