package ca.ulaval.glo4003.ws.api.transaction.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigurePaymentRequest {
  @NotNull
  @JsonProperty(value = "bank_no", required = true)
  private String bankNumber;

  @NotNull
  @JsonProperty(value = "account_no", required = true)
  private String accountNumber;

  @NotNull
  @JsonProperty(value = "frequency", required = true)
  private String frequency;

  public ConfigurePaymentRequest() {}

  public ConfigurePaymentRequest(String bankNumber, String accountNumber, String frequency) {
    this.bankNumber = bankNumber;
    this.accountNumber = accountNumber;
    this.frequency = frequency;
  }

  public String getBankNumber() {
    return bankNumber;
  }

  public void setBankNumber(String bankNumber) {
    this.bankNumber = bankNumber;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getFrequency() {
    return frequency;
  }

  public void setFrequency(String frequency) {
    this.frequency = frequency;
  }
}
