package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.api.delivery.dto.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryDestination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;

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
  void givenDeliveryLocationRequest_whenCreate_thenReturnCorrectDeliveryLocation() {
    // given
    DeliveryLocationRequest request = new DeliveryLocationRequest();
    request.setMode(A_MODE);
    request.setLocation(A_LOCATION);

    // when
    DeliveryDestination deliveryDestination = deliveryDestinationAssembler.assemble(request);

    // then
    assertThat(deliveryDestination.getMode().getDeliveryMode()).matches(A_MODE);
    assertThat(deliveryDestination.getLocation().getCampusLocation()).matches(A_LOCATION);
  }
}
