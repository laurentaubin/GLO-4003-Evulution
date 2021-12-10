package ca.ulaval.glo4003.ws.infrastructure.transaction.log;

import ca.ulaval.glo4003.ws.domain.transaction.log.TransactionLogEntry;
import ca.ulaval.glo4003.ws.fixture.TransactionLogFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collection;

import static com.google.common.truth.Truth.assertThat;

@ExtendWith(MockitoExtension.class)
class InMemoryTransactionLogRepositoryTest {
  private static final LocalDate A_DATE = LocalDate.of(1, 1, 1);

  private final TransactionLogFixture transactionLogFixture = new TransactionLogFixture();

  private InMemoryTransactionLogRepository inMemoryTransactionLogRepository;

  @BeforeEach
  public void setUp() {
    TransactionLogDtoAssembler transactionLogDtoAssembler = new TransactionLogDtoAssembler();

    inMemoryTransactionLogRepository =
        new InMemoryTransactionLogRepository(transactionLogDtoAssembler);
  }

  @Test
  public void givenNoTransactionLogsSaved_whenFindAllByDate_thenReturnEmptyCollection() {
    // when
    Collection<TransactionLogEntry> transactionLogs =
        inMemoryTransactionLogRepository.findAllForDate(A_DATE);

    // then
    assertThat(transactionLogs).isEmpty();
  }

  @Test
  public void givenTransactionLogSavedOnDate_whenFindAllByDate_thenReturnCollectionWithSavedLog() {
    // given
    TransactionLogEntry transactionLog = transactionLogFixture.givenATransactionLog();
    inMemoryTransactionLogRepository.save(transactionLog);

    // when
    Collection<TransactionLogEntry> transactionLogs =
        inMemoryTransactionLogRepository.findAllForDate(transactionLog.getCreationDate());

    // then
    assertThat(transactionLogs).contains(transactionLog);
  }

  @Test
  public void
      givenTransactionLogSavedOnDate_whenFindAllByDifferentDate_thenReturnEmptyCollection() {
    // given
    TransactionLogEntry transactionLog = transactionLogFixture.givenATransactionLog();
    LocalDate aDifferentDate = transactionLog.getCreationDate().plusWeeks(1);
    inMemoryTransactionLogRepository.save(transactionLog);

    // when
    Collection<TransactionLogEntry> transactionLogs =
        inMemoryTransactionLogRepository.findAllForDate(aDifferentDate);

    // then
    assertThat(transactionLogs).isEmpty();
  }
}
