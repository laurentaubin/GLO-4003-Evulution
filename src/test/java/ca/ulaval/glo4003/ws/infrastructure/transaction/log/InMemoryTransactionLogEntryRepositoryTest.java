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
class InMemoryTransactionLogEntryRepositoryTest {
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
    Collection<TransactionLogEntry> transactionLogEntries =
        inMemoryTransactionLogRepository.findAllForDate(A_DATE);

    // then
    assertThat(transactionLogEntries).isEmpty();
  }

  @Test
  public void givenTransactionLogSavedOnDate_whenFindAllByDate_thenReturnCollectionWithSavedLog() {
    // given
    TransactionLogEntry transactionLogEntry = transactionLogFixture.givenATransactionLog();
    inMemoryTransactionLogRepository.save(transactionLogEntry);

    // when
    Collection<TransactionLogEntry> transactionLogEntries =
        inMemoryTransactionLogRepository.findAllForDate(transactionLogEntry.getCreationDate());

    // then
    assertThat(transactionLogEntries).contains(transactionLogEntry);
  }

  @Test
  public void
      givenTransactionLogSavedOnDate_whenFindAllByDifferentDate_thenReturnEmptyCollection() {
    // given
    TransactionLogEntry transactionLogEntry = transactionLogFixture.givenATransactionLog();
    LocalDate aDifferentDate = transactionLogEntry.getCreationDate().plusWeeks(1);
    inMemoryTransactionLogRepository.save(transactionLogEntry);

    // when
    Collection<TransactionLogEntry> transactionLogEntries =
        inMemoryTransactionLogRepository.findAllForDate(aDifferentDate);

    // then
    assertThat(transactionLogEntries).isEmpty();
  }
}
