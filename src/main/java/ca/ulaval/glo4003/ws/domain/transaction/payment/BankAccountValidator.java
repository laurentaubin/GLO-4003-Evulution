package ca.ulaval.glo4003.ws.domain.transaction.payment;

import ca.ulaval.glo4003.ws.domain.transaction.exception.InvalidBankAccountException;

import java.util.regex.Pattern;

public class BankAccountValidator {

  public void validateBankAccountInformation(String bankNumber, String accountNumber) {
    validateBankNumber(bankNumber);
    validateAccountNumber(accountNumber);
  }

  private void validateBankNumber(String bankNumber) {
    if (!(Pattern.matches("[0-9]{3}", bankNumber))) {
      throw new InvalidBankAccountException();
    }
  }

  private void validateAccountNumber(String accountNumber) {
    if (!(Pattern.matches("[0-9]{7}", accountNumber))) {
      throw new InvalidBankAccountException();
    }
  }
}
