package ca.ulaval.glo4003.ws.api.transaction;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.transaction.dto.VehicleRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleRequestAssemblerTest {
  private static final String A_MODEL = "Vandry";
  private static final String A_COLOR = "Color";

  private VehicleRequestAssembler vehicleRequestAssembler;

  @BeforeEach
  void setUp() {
    vehicleRequestAssembler = new VehicleRequestAssembler();
  }

  @Test
  void givenVehicleRequest_whenCreate_thenReturnVehicleWithSameFields() {
    // given
    var vehicleRequest = new VehicleRequest();
    vehicleRequest.setModel(A_MODEL);
    vehicleRequest.setColor(A_COLOR);

    // when
    var actual = vehicleRequestAssembler.create(vehicleRequest);

    // then
    assertThat(actual.getModel().getModel()).matches(A_MODEL);
    assertThat(actual.getColor().getColor()).matches(A_COLOR);
  }
}
