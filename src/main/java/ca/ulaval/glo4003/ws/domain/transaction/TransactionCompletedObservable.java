package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.assembly.TransactionObserver;
import java.util.ArrayList;
import java.util.List;

public class TransactionCompletedObservable {
  private final List<TransactionObserver> observers = new ArrayList<>();

  public void register(TransactionObserver observer) {
    observers.add(observer);
  }

  public void notifyTransactionCompleted(Transaction transaction) {
    for (TransactionObserver observer : observers) {
      observer.listenToTransactionCompleted(transaction);
    }
  }
}
