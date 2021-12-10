package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.transaction.request.ConfigureVehicleRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigureVehicleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class ConfigureVehicleDtoAssemblerTest {
  private static final String MODEL = "model";
  private static final String COLOR = "color";

  private ConfigureVehicleDtoAssembler configureVehicleDtoAssembler;

  @BeforeEach
  public void setUp() {
    configureVehicleDtoAssembler = new ConfigureVehicleDtoAssembler();
  }

  @Test
  public void
      givenAConfigureVehicleRequest_whenAssemble_thenConfigureVehicleDtoIsCorrectlyAsssembled() {
    // given
    ConfigureVehicleRequest configureVehicleRequest = new ConfigureVehicleRequest(MODEL, COLOR);

    // when
    ConfigureVehicleDto configureVehicleDto =
        configureVehicleDtoAssembler.assemble(configureVehicleRequest);

    // then
    assertThat(configureVehicleDto.getModelName()).isEqualTo(MODEL);
    assertThat(configureVehicleDto.getColor()).isEqualTo(COLOR);
  }
}
