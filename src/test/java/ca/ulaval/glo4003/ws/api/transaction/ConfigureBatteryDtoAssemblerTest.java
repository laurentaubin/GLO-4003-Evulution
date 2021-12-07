package ca.ulaval.glo4003.ws.api.transaction;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.transaction.request.ConfigureBatteryRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigureBatteryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConfigureBatteryDtoAssemblerTest {
  private static final String A_TYPE = "a type";

  private ConfigureBatteryDtoAssembler configureBatteryDtoAssembler;

  @BeforeEach
  public void setUp() {
    configureBatteryDtoAssembler = new ConfigureBatteryDtoAssembler();
  }

  @Test
  public void
      givenAConfigureBatteryRequest_whenAssemble_thenConfigureBatteryDtoIsCorrectlyAssembled() {
    // given
    ConfigureBatteryRequest configureBatteryRequest = new ConfigureBatteryRequest(A_TYPE);

    // when
    ConfigureBatteryDto configureBatteryDto =
        configureBatteryDtoAssembler.assemble(configureBatteryRequest);

    // then
    assertThat(configureBatteryDto.getTypeName()).isEqualTo(A_TYPE);
  }
}
