package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.api.delivery.dto.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.api.delivery.dto.validator.DeliveryRequestValidator;
import ca.ulaval.glo4003.ws.api.shared.TokenExtractor;
import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.service.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.service.delivery.dto.CompletedDeliveryDto;
import ca.ulaval.glo4003.ws.service.delivery.dto.DeliveryLocationDto;
import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeliveryResourceImplTest {
  private static final DeliveryId AN_ID = new DeliveryId("id");
  private static final String A_MODE = "At campus";
  private static final String A_LOCATION = "Vachon";
  private static final String INVALID_MODE = "invalid mode";

  @Mock private DeliveryService deliveryService;
  @Mock private DeliveryRequestValidator deliveryRequestValidator;
  @Mock private DeliveryDtoAssembler deliveryDtoAssembler;
  @Mock private ContainerRequestContext containerRequestContext;
  @Mock private TokenExtractor tokenExtractor;
  @Mock private TokenDto tokenDto;
  @Mock private CompletedDeliveryDto completedDeliveryDto;

  private DeliveryResource deliveryResource;

  @BeforeEach
  void setUp() {
    deliveryResource =
        new DeliveryResourceImpl(
            deliveryService, deliveryRequestValidator, deliveryDtoAssembler, tokenExtractor);
  }

  @Test
  public void givenDeliveryLocationRequest_whenAddLocation_thenValidateRequest() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();

    // when
    deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID, request);

    // then
    verify(deliveryRequestValidator).validate(request);
  }

  @Test
  public void givenDeliveryLocationRequest_whenAddLocation_thenServiceAddsDeliveryLocation() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();
    DeliveryLocationDto requestDto = createDeliveryLocationRequestDto();
    given(deliveryDtoAssembler.assemble(request)).willReturn(requestDto);
    given(tokenExtractor.extract(containerRequestContext)).willReturn(tokenDto);

    // when
    deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID, request);

    // then
    verify(deliveryService).addDeliveryLocation(tokenDto, AN_ID, requestDto);
  }

  @Test
  public void
      givenInvalidDeliveryLocationRequest_whenAddLocation_thenThrowInvalidFormatException() {
    // given
    DeliveryLocationRequest invalidRequest = createInvalidDeliveryLocationRequest();
    doThrow(InvalidFormatException.class).when(deliveryRequestValidator).validate(invalidRequest);

    // when
    Executable addingLocation =
        () -> deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID, invalidRequest);

    // then
    assertThrows(InvalidFormatException.class, addingLocation);
  }

  @Test
  public void givenValidDeliveryLocationRequest_whenAddDeliveryLocation_thenReturn202() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();

    // when
    Response response =
        deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID, request);

    // then
    assertThat(response.getStatus()).isEqualTo(Status.ACCEPTED.getStatusCode());
  }

  @Test public void whenAddDeliveryLocation_thenExtractToken() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();

    // when
    deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID, request);

    // then
    verify(tokenExtractor).extract(containerRequestContext);
  }

  @Test public void whenCompleteDelivery_thenExtractToken() {
    // when
    deliveryResource.completeDelivery(containerRequestContext, AN_ID);

    // then
    verify(tokenExtractor).extract(containerRequestContext);
  }

  @Test public void whenCompleteDelivery_thenCompleteDelivery() {
    // given
    given(tokenExtractor.extract(containerRequestContext)).willReturn(tokenDto);

    // when
    deliveryResource.completeDelivery(containerRequestContext, AN_ID);

    // then
    verify(deliveryService).completeDelivery(tokenDto, AN_ID);
  }

  @Test public void whenCompleteDelivery_thenAssembleResponse() {
    // given
    given(tokenExtractor.extract(containerRequestContext)).willReturn(tokenDto);
    given(deliveryService.completeDelivery(tokenDto, AN_ID)).willReturn(completedDeliveryDto);

    // when
    deliveryResource.completeDelivery(containerRequestContext, AN_ID);

    // then
    verify(deliveryDtoAssembler).assemble(completedDeliveryDto);
  }

  @Test public void givenValidCompleteDeliveryRequest_whenCompleteDelivery_thenReturn200() {
    // when
    Response response = deliveryResource.completeDelivery(containerRequestContext, AN_ID);

    // then
    assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
  }

  private DeliveryLocationRequest createDeliveryLocationRequest() {
    DeliveryLocationRequest request = new DeliveryLocationRequest();
    request.setMode(A_MODE);
    request.setLocation(A_LOCATION);
    return request;
  }

  private DeliveryLocationDto createDeliveryLocationRequestDto() {
    return new DeliveryLocationDto(A_MODE, A_LOCATION);
  }

  private DeliveryLocationRequest createInvalidDeliveryLocationRequest() {
    DeliveryLocationRequest request = new DeliveryLocationRequest();
    request.setMode(INVALID_MODE);
    request.setLocation(A_LOCATION);
    return request;
  }
}
