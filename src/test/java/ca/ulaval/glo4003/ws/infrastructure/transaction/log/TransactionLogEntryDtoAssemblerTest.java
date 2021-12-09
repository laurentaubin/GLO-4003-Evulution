package ca.ulaval.glo4003.ws.infrastructure.transaction.log;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.transaction.log.TransactionLogEntry;
import ca.ulaval.glo4003.ws.fixture.TransactionLogFixture;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionLogEntryDtoAssemblerTest {
  private final TransactionLogFixture transactionLogFixture = new TransactionLogFixture();

  private TransactionLogDtoAssembler transactionLogDtoAssembler;

  @BeforeEach
  public void seUp() {
    transactionLogDtoAssembler = new TransactionLogDtoAssembler();
  }

  @Test
  public void givenTransactionLog_whenAssemble_thenReturnTransactionLogDtoWithSameAttributes() {
    // given
    TransactionLogEntry transactionLogEntry = transactionLogFixture.givenATransactionLog();

    // when
    TransactionLogDto transactionLogDto = transactionLogDtoAssembler.assemble(transactionLogEntry);

    // then
    assertThat(transactionLogDto.getCreationDate())
        .isEquivalentAccordingToCompareTo(transactionLogEntry.getCreationDate());
    assertThat(transactionLogDto.getTotalPrice().doubleValue())
        .isEqualTo(transactionLogEntry.getTotalPrice().toDouble());
    assertThat(transactionLogDto.getBatteryType()).matches(transactionLogEntry.getBatteryType());
    assertThat(transactionLogDto.getModelName()).matches(transactionLogEntry.getVehicleModel());
  }

  @Test
  public void givenTransactionLogDto_whenAssemble_thenReturnTransactionLogWithSameAttributes() {
    // given
    TransactionLogDto transactionLogDto = givenATransactionLogDto();

    // when
    TransactionLogEntry transactionLogEntry =
        transactionLogDtoAssembler.assemble(transactionLogDto);

    // then
    assertThat(transactionLogEntry.getCreationDate())
        .isEquivalentAccordingToCompareTo(transactionLogDto.getCreationDate());
    assertThat(transactionLogEntry.getTotalPrice().toDouble())
        .isEqualTo(transactionLogDto.getTotalPrice().doubleValue());
    assertThat(transactionLogEntry.getBatteryType()).matches(transactionLogDto.getBatteryType());
    assertThat(transactionLogEntry.getVehicleModel()).matches(transactionLogDto.getModelName());
  }

  private TransactionLogDto givenATransactionLogDto() {
    return new TransactionLogDto(LocalDate.of(1, 1, 1), BigDecimal.ONE, "abc", "dfg");
  }
}
