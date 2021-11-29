package ca.ulaval.glo4003.ws.domain.transaction.payment;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionRepository;

public class PaymentService {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final TransactionRepository transactionRepository;
  private final ReceiptFactory receiptFactory;

  public PaymentService() {
    this(
        serviceLocator.resolve(TransactionRepository.class),
        serviceLocator.resolve(ReceiptFactory.class));
  }

  public PaymentService(
      TransactionRepository transactionRepository, ReceiptFactory receiptFactory) {
    this.transactionRepository = transactionRepository;
    this.receiptFactory = receiptFactory;
  }

  public Receipt generateReceipt(TransactionId transactionId) {
    Transaction transaction = transactionRepository.find(transactionId);
    return transaction.generateReceipt(receiptFactory);
  }
}
