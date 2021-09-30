package ca.ulaval.glo4003.ws.api.transaction.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequest {
  @NotNull
  @JsonProperty(value = "bank_no", required = true)
  private int bankNumber;

  @NotNull
  @JsonProperty(value = "account_no", required = true)
  private int accountNumber;

  @NotNull
  @JsonProperty(value = "frequency", required = true)
  private String frequency;

  public int getBankNumber() {
    return bankNumber;
  }

  public void setBankNumber(int bankNumber) {
    this.bankNumber = bankNumber;
  }

  public int getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(int accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getFrequency() {
    return frequency;
  }

  public void setFrequency(String frequency) {
    this.frequency = frequency;
  }
}
