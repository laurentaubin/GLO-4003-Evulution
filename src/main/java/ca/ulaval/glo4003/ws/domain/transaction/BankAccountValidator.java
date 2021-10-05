package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.transaction.exception.InvalidBankAccountException;

public class BankAccountValidator {

  public void validateBankAccountInformation(int bankNumber, int accountNumber) {
    validateBankNumber(bankNumber);
    validateAccountNumber(accountNumber);
  }

  private void validateBankNumber(int bankNumber) {
    if (String.valueOf(bankNumber).length() != 3) {
      throw new InvalidBankAccountException();
    }
  }

  private void validateAccountNumber(int accountNumber) {
    if (String.valueOf(accountNumber).length() != 7) {
      throw new InvalidBankAccountException();
    }
  }
}
