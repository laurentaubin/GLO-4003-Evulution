package ca.ulaval.glo4003.ws.domain.transaction.log;

import ca.ulaval.glo4003.ws.domain.assembly.TransactionObserver;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;

public class TransactionLogService implements TransactionObserver {
  private final TransactionLogFactory transactionLogFactory;
  private final TransactionLogSink transactionLogSink;

  public TransactionLogService(
      TransactionLogFactory transactionLogFactory, TransactionLogSink transactionLogSink) {
    this.transactionLogFactory = transactionLogFactory;
    this.transactionLogSink = transactionLogSink;
  }

  @Override
  public void listenToTransactionCompleted(Transaction transaction) {
    TransactionLogEntry transactionLogEntry = transactionLogFactory.create(transaction);
    transactionLogSink.save(transactionLogEntry);
  }
}
