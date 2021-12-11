package ca.ulaval.glo4003.ws.domain.vehicle;

import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import ca.ulaval.glo4003.ws.domain.vehicle.model.ModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class VehicleFactoryTest {
  private static final String A_MODEL = "model";
  private static final Color A_COLOR = Color.WHITE;

  @Mock private ModelRepository modelRepository;
  @Mock private Model model;

  private VehicleFactory vehicleFactory;

  @BeforeEach
  public void setUp() {
    vehicleFactory = new VehicleFactory(modelRepository);
  }

  @Test
  public void givenValidModelAndColor_whenCreate_thenReturnVehicleWithRightModelAndColor() {
    // given
    given(modelRepository.findByModel(A_MODEL)).willReturn(model);

    // when
    Vehicle actualVehicle = vehicleFactory.create(A_MODEL, A_COLOR.toString());

    // then
    assertThat(actualVehicle.getModel()).isEqualTo(model);
    assertThat(actualVehicle.getColor()).isEquivalentAccordingToCompareTo(A_COLOR);
  }
}
