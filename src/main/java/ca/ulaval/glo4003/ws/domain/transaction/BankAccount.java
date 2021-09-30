package ca.ulaval.glo4003.ws.domain.transaction;

public class BankAccount {

  private final int bankNumber;
  private final int accountNumber;

  public BankAccount(int bankNumber, int accountNumber) {
    this.bankNumber = bankNumber;
    this.accountNumber = accountNumber;
  }

  public int getBankNumber() {
    return bankNumber;
  }

  public int getAccountNumber() {
    return accountNumber;
  }
}
