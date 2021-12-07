package ca.ulaval.glo4003.ws.api.delivery;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.api.delivery.dto.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.api.delivery.dto.validator.DeliveryRequestValidator;
import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DeliveryNotFoundException;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.service.authentication.AuthenticationService;
import ca.ulaval.glo4003.ws.service.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.service.delivery.dto.DeliveryLocationDto;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeliveryResourceImplTest {
  private static final DeliveryId AN_ID = new DeliveryId("id");
  private static final String A_MODE = "At campus";
  private static final String A_LOCATION = "Vachon";
  private static final String INVALID_MODE = "invalid mode";
  private static final List<Role> ROLES = new ArrayList<>(List.of(Role.BASE, Role.ADMIN));

  @Mock private DeliveryService deliveryService;
  @Mock private AuthenticationService authenticationService;
  @Mock private DeliveryRequestValidator deliveryRequestValidator;
  @Mock private DeliveryDtoAssembler deliveryDtoAssembler;
  @Mock private ContainerRequestContext containerRequestContext;
  @Mock private Session aSession;

  private DeliveryResource deliveryResource;

  @BeforeEach
  void setUp() {
    deliveryResource =
        new DeliveryResourceImpl(
            deliveryService, authenticationService, deliveryRequestValidator, deliveryDtoAssembler);
  }

  @Test
  void givenDeliveryLocationRequest_whenAddLocation_thenValidateRequest() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();

    // when
    deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID, request);

    // then
    verify(deliveryRequestValidator).validate(request);
  }

  @Test
  void givenDeliveryLocationRequest_whenAddLocation_thenServiceAddsDeliveryLocation() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();
    DeliveryLocationDto requestDto = createDeliveryLocationRequestDto();
    given(deliveryDtoAssembler.assemble(request)).willReturn(requestDto);

    // when
    deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID, request);

    // then
    verify(deliveryService).addDeliveryLocation(AN_ID, requestDto);
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

  @Test
  public void whenAddDeliveryLocation_thenRolesAreValidated() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();

    // when
    deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID, request);

    // then
    verify(authenticationService)
        .validateDeliveryOwnership(
            containerRequestContext, AN_ID, new ArrayList<>(List.of(Role.BASE, Role.ADMIN)));
  }

  @Test
  public void whenAddDeliveryLocation_thenValidateDeliveryOwnership() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();

    // when
    deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID, request);

    // then
    verify(authenticationService).validateDeliveryOwnership(containerRequestContext, AN_ID, ROLES);
  }

  @Test
  public void
      givenWrongOwnerException_whenAddDeliveryLocation_thenThrowDeliveryNotFoundException() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();
    doThrow(DeliveryNotFoundException.class)
        .when(authenticationService)
        .validateDeliveryOwnership(any(), any(), any());

    // when
    Executable addingDeliveryLocation =
        () -> deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID, request);

    // then
    assertThrows(DeliveryNotFoundException.class, addingDeliveryLocation);
  }

  @Test
  void givenSession_whenCompleteDelivery_thenRetrieveTransactionIdFromSessionCalled() {
    // given
    given(authenticationService.retrieveSession(any(), any())).willReturn(aSession);

    // when
    deliveryResource.completeDelivery(containerRequestContext, AN_ID);

    // then
    verify(authenticationService).retrieveTransactionIdFromSession(aSession, AN_ID);
  }

  @Test
  void whenCompleteDelivery_thenRolesAreValidated() {
    // when
    deliveryResource.completeDelivery(containerRequestContext, AN_ID);

    // then
    verify(authenticationService)
        .retrieveSession(containerRequestContext, new ArrayList<>(List.of(Role.BASE, Role.ADMIN)));
  }

  private DeliveryLocationRequest createDeliveryLocationRequest() {
    DeliveryLocationRequest request = new DeliveryLocationRequest();
    request.setMode(A_MODE);
    request.setLocation(A_LOCATION);
    return request;
  }

  private DeliveryLocationDto createDeliveryLocationRequestDto() {
    DeliveryLocationDto request = new DeliveryLocationDto();
    request.mode = A_MODE;
    request.location = A_LOCATION;
    return request;
  }

  private DeliveryLocationRequest createInvalidDeliveryLocationRequest() {
    DeliveryLocationRequest request = new DeliveryLocationRequest();
    request.setMode(INVALID_MODE);
    request.setLocation(A_LOCATION);
    return request;
  }
}
