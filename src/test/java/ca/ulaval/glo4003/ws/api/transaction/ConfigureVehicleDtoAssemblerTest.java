package ca.ulaval.glo4003.ws.api.transaction;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.transaction.request.ConfigureVehicleRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigureVehicleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConfigureVehicleDtoAssemblerTest {
  private static final String A_MODEL = "model";
  private static final String A_COLOR = "color";

  private ConfigureVehicleDtoAssembler configureVehicleDtoAssembler;

  @BeforeEach
  public void setUp() {
    configureVehicleDtoAssembler = new ConfigureVehicleDtoAssembler();
  }

  @Test
  public void
      givenAConfigureVehicleRequest_whenAssemble_thenConfigureVehicleDtoIsCorrectlyAssembled() {
    // given
    ConfigureVehicleRequest configureVehicleRequest = new ConfigureVehicleRequest(A_MODEL, A_COLOR);

    // when
    ConfigureVehicleDto configureVehicleDto =
        configureVehicleDtoAssembler.assemble(configureVehicleRequest);

    // then
    assertThat(configureVehicleDto.getModelName()).isEqualTo(A_MODEL);
    assertThat(configureVehicleDto.getColor()).isEqualTo(A_COLOR);
  }
}
