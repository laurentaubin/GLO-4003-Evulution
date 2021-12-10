package ca.ulaval.glo4003.ws.domain.transaction.log;

import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.warehouse.TransactionCompletedObserver;

public class TransactionLogService implements TransactionCompletedObserver {
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
