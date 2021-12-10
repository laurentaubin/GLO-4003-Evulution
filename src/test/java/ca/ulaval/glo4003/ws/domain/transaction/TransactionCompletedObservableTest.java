package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.warehouse.TransactionCompletedObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionCompletedObservableTest {

  @Mock private TransactionCompletedObserver anObserver;
  @Mock private TransactionCompletedObserver anotherObserver;
  @Mock private Transaction transaction;

  private TransactionCompletedObservable transactionCompletedObservable;

  @BeforeEach
  public void setUp() {
    transactionCompletedObservable = new TransactionCompletedObservable();
  }

  @Test
  void
      givenAnObservers_whenNotifyTransactionCompleted_thenObserversAreNotifiedWithTransactionInformation() {
    // given
    transactionCompletedObservable.register(anObserver);
    transactionCompletedObservable.register(anotherObserver);

    // when
    transactionCompletedObservable.notifyTransactionCompleted(transaction);

    // then
    verify(anObserver).listenToTransactionCompleted(transaction);
    verify(anotherObserver).listenToTransactionCompleted(transaction);
  }
}
