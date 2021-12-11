package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.transaction.payment.PaymentService;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Receipt;
import ca.ulaval.glo4003.ws.domain.transaction.payment.ReceiptFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
  @Mock private TransactionRepository transactionRepository;
  @Mock private ReceiptFactory receiptFactory;
  @Mock private Transaction transaction;
  @Mock private Receipt receipt;

  private PaymentService paymentService;

  @BeforeEach
  void setUp() {
    paymentService = new PaymentService(transactionRepository, receiptFactory);
  }

  @Test
  void givenTransactionId_whenGetReceipt_thenTransactionIsFetched() {
    // given
    TransactionId transactionId = new TransactionId("lol");
    given(transactionRepository.find(transactionId)).willReturn(transaction);
    given(transaction.generateReceipt(receiptFactory)).willReturn(receipt);

    // when
    paymentService.generateReceipt(transactionId);

    // then
    verify(transactionRepository).find(transactionId);
  }
}
