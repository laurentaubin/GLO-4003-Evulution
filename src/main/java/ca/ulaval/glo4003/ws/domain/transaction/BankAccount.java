package ca.ulaval.glo4003.ws.domain.transaction;

public class BankAccount {

  private final String bankNumber;
  private final String accountNumber;

  public BankAccount(String bankNumber, String accountNumber) {
    this.bankNumber = bankNumber;
    this.accountNumber = accountNumber;
  }

  public String getBankNumber() {
    return bankNumber;
  }

  public String getAccountNumber() {
    return accountNumber;
  }
}
