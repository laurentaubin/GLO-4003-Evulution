package ca.ulaval.glo4003.ws.infrastructure.transaction;

import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Payment;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TransactionAssemblerTest {

  @Mock private TransactionId aTransactionId;
  @Mock private Vehicle aVehicle;
  @Mock private Payment aPayment;

  private TransactionAssembler transactionAssembler;

  @BeforeEach
  public void setUp() {
    transactionAssembler = new TransactionAssembler();
  }

  @Test
  public void
      givenTransactionDtoWithAllAttributes_whenAssemble_thenReturnTransactionWithSameAttributes() {
    // given
    given(aVehicle.hasBattery()).willReturn(true);
    TransactionDto transactionDto = new TransactionDto(aTransactionId, aVehicle, aPayment);

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
    given(aVehicle.hasBattery()).willReturn(true);
    TransactionDto transactionDto = new TransactionDto(aTransactionId, aVehicle, null);

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
    given(aVehicle.hasBattery()).willReturn(false);
    TransactionDto transactionDto = new TransactionDto(aTransactionId, aVehicle, null);

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
    TransactionDto transactionDto = new TransactionDto(aTransactionId, null, null);

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
    given(aVehicle.hasBattery()).willReturn(true);
    Transaction transaction = new Transaction(aTransactionId);
    transaction.addVehicle(aVehicle);
    transaction.addPayment(aPayment);

    // when
    TransactionDto transactionDto = transactionAssembler.assemble(transaction);

    // then
    assertThat(transactionDto.getId()).isEqualTo(transaction.getId());
    assertThat(transactionDto.getVehicle()).isEqualTo(transaction.getVehicle());
    assertThat(transactionDto.getPayment()).isEqualTo(transaction.getPayment());
  }
}
