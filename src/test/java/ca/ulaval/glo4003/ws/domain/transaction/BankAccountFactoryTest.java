package ca.ulaval.glo4003.ws.domain.transaction;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BankAccountFactoryTest {

  private static final int A_BANK_NUMBER = 100;
  private static final int AN_ACCOUNT_NUMBER = 9999999;

  private BankAccountFactory bankAccountFactory;

  @BeforeEach
  public void setUp() {
    bankAccountFactory = new BankAccountFactory();
  }

  @Test
  public void whenCreate_thenCreateBankAccount() {
    // when
    Object bankAccount = bankAccountFactory.create(A_BANK_NUMBER, AN_ACCOUNT_NUMBER);

    // then
    assertThat(bankAccount).isInstanceOf(BankAccount.class);
  }
}
