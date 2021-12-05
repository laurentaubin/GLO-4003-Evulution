package ca.ulaval.glo4003.ws.domain.transaction;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.transaction.payment.BankAccount;
import ca.ulaval.glo4003.ws.domain.transaction.payment.BankAccountFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BankAccountFactoryTest {

  private static final String A_VALID_BANK_NUMBER = "003";
  private static final String A_VALID_ACCOUNT_NUMBER = "0099999";

  private BankAccountFactory bankAccountFactory;

  @BeforeEach
  public void setUp() {
    bankAccountFactory = new BankAccountFactory();
  }

  @Test
  public void whenCreate_thenCreateBankAccount() {
    // when
    Object bankAccount = bankAccountFactory.create(A_VALID_BANK_NUMBER, A_VALID_ACCOUNT_NUMBER);

    // then
    assertThat(bankAccount).isInstanceOf(BankAccount.class);
  }
}
