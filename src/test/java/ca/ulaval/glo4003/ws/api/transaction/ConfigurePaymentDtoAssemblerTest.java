package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.transaction.request.ConfigurePaymentRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigurePaymentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class ConfigurePaymentDtoAssemblerTest {
  private static final String A_BANK_NUMBER = "43242";
  private static final String AN_ACCOUNT_NUMBER = "65321";
  private static final String A_FREQUENCY = "monthly";

  private ConfigurePaymentDtoAssembler configurePaymentDtoAssembler;

  @BeforeEach
  public void setUp() {
    configurePaymentDtoAssembler = new ConfigurePaymentDtoAssembler();
  }

  @Test
  public void
      givenAConfigurePaymentRequest_whenAssemble_thenConfigurePaymentDtoIsCorrectlyAssembled() {
    // given
    ConfigurePaymentRequest configurePaymentRequest =
        new ConfigurePaymentRequest(A_BANK_NUMBER, AN_ACCOUNT_NUMBER, A_FREQUENCY);

    // when
    ConfigurePaymentDto configurePaymentDto =
        configurePaymentDtoAssembler.assemble(configurePaymentRequest);

    // then
    assertThat(configurePaymentDto.getBankNumber()).isEqualTo(A_BANK_NUMBER);
    assertThat(configurePaymentDto.getAccountNumber()).isEqualTo(AN_ACCOUNT_NUMBER);
    assertThat(configurePaymentDto.getFrequency()).isEqualTo(A_FREQUENCY);
  }
}
