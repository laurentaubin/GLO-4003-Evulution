package ca.ulaval.glo4003.ws.domain.vehicle;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleFactoryTest {
  private static final String A_MODEL = "model";
  private static final Color A_COLOR = Color.WHITE;

  @Mock private ModelRepository modelRepository;
  @Mock private Model aModel;

  private VehicleFactory vehicleFactory;

  @BeforeEach
  public void setUp() {
    vehicleFactory = new VehicleFactory(modelRepository);
  }

  @Test
  public void givenValidModelAndColor_whenCreate_thenReturnVehicleWithRightModelAndColor() {
    // given
    given(modelRepository.findByModel(A_MODEL)).willReturn(aModel);

    // when
    Vehicle actualVehicle = vehicleFactory.create(A_MODEL, A_COLOR.toString());

    // then
    assertThat(actualVehicle.getModel()).isEqualTo(aModel);
    assertThat(actualVehicle.getColor()).isEquivalentAccordingToCompareTo(A_COLOR);
  }
}
