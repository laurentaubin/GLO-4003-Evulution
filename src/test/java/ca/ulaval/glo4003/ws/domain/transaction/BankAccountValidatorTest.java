package ca.ulaval.glo4003.ws.domain.transaction;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class BankAccountValidatorTest {

  private static final int A_VALID_BANK_NUMBER = 100;
  private static final int A_VALID_ACCOUNT_NUMBER = 9999999;
  private static final int AN_INVALID_BANK_NUMBER = 10;
  private static final int AN_INVALID_ACCOUNT_NUMBER = 9999;

  private BankAccountValidator bankAccountValidator;

  @BeforeEach
  void setUp() {
    bankAccountValidator = new BankAccountValidator();
  }

  @Test
  void
      givenInvalidBankAccountInformation_ValidateBankAccountInformation_thenThrowInvalidBankAccount() {
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

  @Test
  void
      givenInvalidBankNumber_whenValidateBankAccountInformation_thenExceptionHasRightDescription() {
    // given
    String expectedMessage = "Bank number must contain three digits.";

    // when
    Executable action =
        () ->
            bankAccountValidator.validateBankAccountInformation(
                AN_INVALID_BANK_NUMBER, A_VALID_ACCOUNT_NUMBER);

    InvalidBankAccountException invalidBankAccountException =
        assertThrows(InvalidBankAccountException.class, action);
    // then
    assertThat(invalidBankAccountException.description).matches(expectedMessage);
  }

  @Test
  void
      givenInvalidAccountNumber_whenValidateBankAccountInformation_thenExceptionHasRightDescription() {
    // given
    String expectedMessage = "Account number must contain seven digits.";

    // when
    Executable action =
        () ->
            bankAccountValidator.validateBankAccountInformation(
                A_VALID_BANK_NUMBER, AN_INVALID_ACCOUNT_NUMBER);

    InvalidBankAccountException invalidBankAccountException =
        assertThrows(InvalidBankAccountException.class, action);
    // then
    assertThat(invalidBankAccountException.description).matches(expectedMessage);
  }
}
