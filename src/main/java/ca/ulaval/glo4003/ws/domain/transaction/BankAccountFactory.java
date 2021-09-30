package ca.ulaval.glo4003.ws.domain.transaction;

public class BankAccountFactory {
  public BankAccount create(int bankNumber, int accountNumber) {
    BankAccountValidator bankAccountValidator = new BankAccountValidator();
    bankAccountValidator.validateBankAccountInformation(bankNumber, accountNumber);
    return new BankAccount(bankNumber, accountNumber);
  }
}
