package ca.ulaval.glo4003.ws.service.transaction.dto;

public class ConfigurePaymentDto {
  private final String bankNumber;
  private final String accountNumber;
  private final String frequency;

  public ConfigurePaymentDto(String bankNumber, String accountNumber, String frequency) {
    this.bankNumber = bankNumber;
    this.accountNumber = accountNumber;
    this.frequency = frequency;
  }

  public String getBankNumber() {
    return bankNumber;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public String getFrequency() {
    return frequency;
  }
}
