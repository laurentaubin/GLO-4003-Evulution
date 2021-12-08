package ca.ulaval.glo4003.ws.domain.transaction.log;

import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.fixture.TransactionFixture;
import ca.ulaval.glo4003.ws.fixture.TransactionLogFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionLogEntryServiceTest {
  TransactionFixture transactionFixture = new TransactionFixture();
  TransactionLogFixture transactionLogFixture = new TransactionLogFixture();

  @Mock TransactionLogFactory transactionLogFactory;
  @Mock TransactionLogSink transactionLogSink;

  private TransactionLogService transactionLogService;

  @BeforeEach
  public void setUp() {
    transactionLogService = new TransactionLogService(transactionLogFactory, transactionLogSink);
  }

  @Test
  public void givenTransactionLogCreated_whenListenTransactionCompleted_thenSaveLog() {
    // given
    Transaction transaction = transactionFixture.givenACompleteTransaction();
    TransactionLogEntry transactionLogEntry = transactionLogFixture.givenATransactionLog();
    given(transactionLogFactory.create(transaction)).willReturn(transactionLogEntry);

    // when
    transactionLogService.listenToTransactionCompleted(transaction);

    // then
    verify(transactionLogSink).save(transactionLogEntry);
  }
}
