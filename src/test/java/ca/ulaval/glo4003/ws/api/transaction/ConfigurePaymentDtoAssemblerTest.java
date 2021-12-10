package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.transaction.request.ConfigurePaymentRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigurePaymentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class ConfigurePaymentDtoAssemblerTest {
  private static final String BANK_NUMBER = "43242";
  private static final String ACCOUNT_NUMBER = "65321";
  private static final String FREQUENCY = "monthly";

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
        new ConfigurePaymentRequest(BANK_NUMBER, ACCOUNT_NUMBER, FREQUENCY);

    // when
    ConfigurePaymentDto configurePaymentDto =
        configurePaymentDtoAssembler.assemble(configurePaymentRequest);

    // then
    assertThat(configurePaymentDto.getBankNumber()).isEqualTo(BANK_NUMBER);
    assertThat(configurePaymentDto.getAccountNumber()).isEqualTo(ACCOUNT_NUMBER);
    assertThat(configurePaymentDto.getFrequency()).isEqualTo(FREQUENCY);
  }
}
