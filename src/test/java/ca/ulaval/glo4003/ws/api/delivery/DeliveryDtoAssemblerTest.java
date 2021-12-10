package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.api.delivery.dto.CompletedDeliveryResponse;
import ca.ulaval.glo4003.ws.api.delivery.dto.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.service.delivery.dto.CompletedDeliveryDto;
import ca.ulaval.glo4003.ws.service.delivery.dto.DeliveryLocationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class DeliveryDtoAssemblerTest {
  private static final Integer PAYMENTS_TAKEN = 6;
  private static final Integer PAYMENTS_LEFT = 1;
  private static final String A_MODE = "At campus";
  private static final String A_LOCATION = "Vachon";

  private DeliveryDtoAssembler deliveryDtoAssembler;

  @BeforeEach
  void setUp() {
    deliveryDtoAssembler = new DeliveryDtoAssembler();
  }

  @Test
  void givenDeliveryLocationRequest_whenAssemble_thenReturnCorrectDeliveryLocationDto() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();

    // when
    DeliveryLocationDto result = deliveryDtoAssembler.assemble(request);

    // then
    assertThat(result.getMode()).matches(A_MODE);
    assertThat(result.getLocation()).matches(A_LOCATION);
  }

  @Test
  void givenDeliveryCompletedDto_whenAssemble_thenReturnCorrectDeliveryCompletedResponse() {
    // given
    CompletedDeliveryDto responseDto = createCompletedDeliveryDto();

    // when
    CompletedDeliveryResponse result = deliveryDtoAssembler.assemble(responseDto);

    // then
    assertThat(result.paymentsLeft).isEqualTo(PAYMENTS_LEFT);
    assertThat(result.paymentTaken).isEqualTo(PAYMENTS_TAKEN);
  }

  private DeliveryLocationRequest createDeliveryLocationRequest() {
    DeliveryLocationRequest request = new DeliveryLocationRequest();
    request.setMode(A_MODE);
    request.setLocation(A_LOCATION);
    return request;
  }

  private CompletedDeliveryDto createCompletedDeliveryDto() {
    return new CompletedDeliveryDto(PAYMENTS_TAKEN, PAYMENTS_LEFT);
  }
}
