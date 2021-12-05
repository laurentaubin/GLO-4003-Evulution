package ca.ulaval.glo4003.ws.domain.transaction;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.transaction.payment.PaymentService;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Receipt;
import ca.ulaval.glo4003.ws.domain.transaction.payment.ReceiptFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
  private PaymentService paymentService;
  @Mock private TransactionRepository transactionRepository;
  @Mock private ReceiptFactory receiptFactory;
  @Mock private Transaction aTransaction;
  @Mock private Receipt aReceipt;

  @BeforeEach
  void setUp() {
    paymentService = new PaymentService(transactionRepository, receiptFactory);
  }

  @Test
  void givenTransactionId_whenGetReceipt_thenTransactionIsFetched() {
    // given
    TransactionId transactionId = new TransactionId("lol");
    given(transactionRepository.find(transactionId)).willReturn(aTransaction);
    given(aTransaction.generateReceipt(receiptFactory)).willReturn(aReceipt);

    // when
    paymentService.generateReceipt(transactionId);

    // then
    verify(transactionRepository).find(transactionId);
  }
}
