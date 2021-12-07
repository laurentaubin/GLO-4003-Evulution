package ca.ulaval.glo4003.ws.service.delivery;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.delivery.DeliveryDestination;
import ca.ulaval.glo4003.ws.service.delivery.dto.DeliveryLocationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeliveryDestinationAssemblerTest {
  private static final String A_MODE = "At campus";
  private static final String A_LOCATION = "Vachon";

  private DeliveryDestinationAssembler deliveryDestinationAssembler;

  @BeforeEach
  void setUp() {
    deliveryDestinationAssembler = new DeliveryDestinationAssembler();
  }

  @Test
  void givenDeliveryLocationDto_whenCreate_thenReturnCorrectDeliveryLocation() {
    // given
    DeliveryLocationDto request = createDeliveryLocationDto();

    // when
    DeliveryDestination deliveryDestination = deliveryDestinationAssembler.assemble(request);

    // then
    assertThat(deliveryDestination.getMode().getDeliveryMode()).matches(A_MODE);
    assertThat(deliveryDestination.getLocation().getCampusLocation()).matches(A_LOCATION);
  }

  private DeliveryLocationDto createDeliveryLocationDto() {
    DeliveryLocationDto request = new DeliveryLocationDto();
    request.mode = A_MODE;
    request.location = A_LOCATION;
    return request;
  }
}
