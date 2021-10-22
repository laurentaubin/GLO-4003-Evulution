package ca.ulaval.glo4003.ws.domain.transaction;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.domain.transaction.exception.InvalidBankAccountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class BankAccountValidatorTest {

  private static final String A_VALID_BANK_NUMBER = "003";
  private static final String A_VALID_ACCOUNT_NUMBER = "0099999";
  private static final String AN_INVALID_BANK_NUMBER = "10";
  private static final String AN_INVALID_ACCOUNT_NUMBER = "9999";

  private BankAccountValidator bankAccountValidator;

  @BeforeEach
  void setUp() {
    bankAccountValidator = new BankAccountValidator();
  }

  @Test
  void
      givenInvalidBankAccountInformation_whenValidateBankAccountInformation_thenThrowInvalidBankAccount() {
    // when
    Executable action =
        () ->
            bankAccountValidator.validateBankAccountInformation(
                AN_INVALID_BANK_NUMBER, AN_INVALID_ACCOUNT_NUMBER);

    // then
    assertThrows(InvalidBankAccountException.class, action);
  }

  @Test
  void givenValidBankAccountInformation_whenValidateBankAccountInformation_thenDoesntThrow() {
    // when
    Executable action =
        () ->
            bankAccountValidator.validateBankAccountInformation(
                A_VALID_BANK_NUMBER, A_VALID_ACCOUNT_NUMBER);

    // then
    assertDoesNotThrow(action);
  }
}
