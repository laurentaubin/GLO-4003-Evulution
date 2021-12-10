package ca.ulaval.glo4003.ws.domain.warehouse;

import ca.ulaval.glo4003.ws.domain.transaction.Transaction;

public interface TransactionCompletedObserver {
  void listenToTransactionCompleted(Transaction transaction);
}
