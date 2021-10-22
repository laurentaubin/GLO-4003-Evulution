package ca.ulaval.glo4003.ws.domain.transaction;

public class BankAccountFactory {
  public BankAccount create(String bankNumber, String accountNumber) {
    BankAccountValidator bankAccountValidator = new BankAccountValidator();
    bankAccountValidator.validateBankAccountInformation(bankNumber, accountNumber);
    return new BankAccount(bankNumber, accountNumber);
  }
}
