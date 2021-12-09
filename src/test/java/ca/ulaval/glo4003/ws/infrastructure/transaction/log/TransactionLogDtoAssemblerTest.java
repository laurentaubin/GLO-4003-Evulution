package ca.ulaval.glo4003.ws.infrastructure.transaction.log;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.transaction.log.TransactionLogEntry;
import ca.ulaval.glo4003.ws.fixture.TransactionLogFixture;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionLogDtoAssemblerTest {
  private final TransactionLogFixture transactionLogFixture = new TransactionLogFixture();

  private TransactionLogDtoAssembler transactionLogDtoAssembler;

  @BeforeEach
  public void seUp() {
    transactionLogDtoAssembler = new TransactionLogDtoAssembler();
  }

  @Test
  public void givenTransactionLog_whenAssemble_thenReturnTransactionLogDtoWithSameAttributes() {
    // given
    TransactionLogEntry transactionLog = transactionLogFixture.givenATransactionLog();

    // when
    TransactionLogDto transactionLogDto = transactionLogDtoAssembler.assemble(transactionLog);

    // then
    assertThat(transactionLogDto.getCreationDate())
        .isEquivalentAccordingToCompareTo(transactionLog.getCreationDate());
    assertThat(transactionLogDto.getTotalPrice().doubleValue())
        .isEqualTo(transactionLog.getTotalPrice().toDouble());
    assertThat(transactionLogDto.getBatteryType()).matches(transactionLog.getBatteryType());
    assertThat(transactionLogDto.getModelName()).matches(transactionLog.getVehicleModel());
  }

  @Test
  public void givenTransactionLogDto_whenAssemble_thenReturnTransactionLogWithSameAttributes() {
    // given
    TransactionLogDto transactionLogDto = givenATransactionLogDto();

    // when
    TransactionLogEntry transactionLog = transactionLogDtoAssembler.assemble(transactionLogDto);

    // then
    assertThat(transactionLog.getCreationDate())
        .isEquivalentAccordingToCompareTo(transactionLogDto.getCreationDate());
    assertThat(transactionLog.getTotalPrice().toDouble())
        .isEqualTo(transactionLogDto.getTotalPrice().doubleValue());
    assertThat(transactionLog.getBatteryType()).matches(transactionLogDto.getBatteryType());
    assertThat(transactionLog.getVehicleModel()).matches(transactionLogDto.getModelName());
  }

  private TransactionLogDto givenATransactionLogDto() {
    return new TransactionLogDto(LocalDate.of(1, 1, 1), BigDecimal.ONE, "abc", "dfg");
  }
}
