package ca.ulaval.glo4003.ws.domain.transaction;

public class BankAccountValidator {

  public void validateBankAccountInformation(int bankNumber, int accountNumber) {
    validateBankNumber(bankNumber);
    validateAccountNumber(accountNumber);
  }

  private void validateBankNumber(int bankNumber) {
    if (String.valueOf(bankNumber).length() != 3) {
      throw new InvalidBankAccountException("Bank number must contain three digits.");
    }
  }

  private void validateAccountNumber(int accountNumber) {
    if (String.valueOf(accountNumber).length() != 7) {
      throw new InvalidBankAccountException("Account number must contain seven digits.");
    }
  }
}
