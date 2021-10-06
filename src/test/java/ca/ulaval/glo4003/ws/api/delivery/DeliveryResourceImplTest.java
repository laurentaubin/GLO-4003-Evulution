package ca.ulaval.glo4003.ws.api.delivery;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.api.delivery.dto.DeliveryLocationRequest;
import ca.ulaval.glo4003.ws.api.delivery.dto.validator.DeliveryRequestValidator;
import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryDestination;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryMode;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryOwnershipHandler;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.domain.delivery.Location;
import ca.ulaval.glo4003.ws.domain.user.Role;
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
  private static final String EXPECTED_DELIVERY_LOCATION_ADDED_MESSAGE =
      "Delivery location successfully added";

  @Mock private DeliveryService deliveryService;
  @Mock private DeliveryDestinationAssembler deliveryDestinationAssembler;
  @Mock private DeliveryRequestValidator deliveryRequestValidator;
  @Mock private DeliveryOwnershipHandler deliveryOwnershipHandler;
  @Mock private RoleHandler roleHandler;
  @Mock ContainerRequestContext containerRequestContext;
  @Mock private Session aSession;

  private DeliveryResource deliveryResource;

  @BeforeEach
  void setUp() {
    deliveryResource =
        new DeliveryResourceImpl(
            deliveryService,
            deliveryRequestValidator,
            deliveryDestinationAssembler,
            deliveryOwnershipHandler,
            roleHandler);
  }

  @Test
  void givenDeliveryLocationRequest_whenAddLocation_thenValidateRequest() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();

    // when
    deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID.toString(), request);

    // then
    verify(deliveryRequestValidator).validate(request);
  }

  @Test
  void givenDeliveryLocationRequest_whenAddLocation_thenServiceAddsDeliveryLocation() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();
    DeliveryDestination deliveryDestination = createDeliveryLocation();
    given(deliveryDestinationAssembler.assemble(request)).willReturn(deliveryDestination);

    // when
    deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID.toString(), request);

    // then
    verify(deliveryService).addDeliveryDestination(AN_ID, deliveryDestination);
  }

  @Test
  public void
      givenInvalidDeliveryLocationRequest_whenAddLocation_thenThrowInvalidFormatException() {
    // given
    DeliveryLocationRequest invalidRequest = createInvalidDeliveryLocationRequest();
    doThrow(InvalidFormatException.class).when(deliveryRequestValidator).validate(invalidRequest);

    // when
    Executable addingLocation =
        () ->
            deliveryResource.addDeliveryLocation(
                containerRequestContext, AN_ID.toString(), invalidRequest);

    // then
    assertThrows(InvalidFormatException.class, addingLocation);
  }

  @Test
  public void
      givenValidDeliveryLocationRequest_whenAddLocation_thenReturn202AcceptedWithDeliveryAddedMessage() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();
    DeliveryDestination deliveryDestination = createDeliveryLocation();
    given(deliveryDestinationAssembler.assemble(request)).willReturn(deliveryDestination);

    // when
    Response response =
        deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID.toString(), request);

    // then
    assertThat(response.getStatus()).isEqualTo(Status.ACCEPTED.getStatusCode());
    assertThat(response.getEntity()).isEqualTo(EXPECTED_DELIVERY_LOCATION_ADDED_MESSAGE);
  }

  @Test
  public void whenAddDeliveryLocation_thenRolesAreValidated() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();

    // when
    deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID.toString(), request);

    // then
    verify(roleHandler)
        .retrieveSession(containerRequestContext, new ArrayList<>(List.of(Role.BASE, Role.ADMIN)));
  }

  @Test
  public void whenAddDeliveryLocation_thenValidateTransactionOwnership() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();
    given(roleHandler.retrieveSession(any(), any())).willReturn(aSession);

    // when
    deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID.toString(), request);

    // then
    verify(deliveryOwnershipHandler).validateOwnership(aSession, AN_ID);
  }

  private DeliveryLocationRequest createDeliveryLocationRequest() {
    DeliveryLocationRequest request = new DeliveryLocationRequest();
    request.setMode(A_MODE);
    request.setLocation(A_LOCATION);
    return request;
  }

  private DeliveryLocationRequest createInvalidDeliveryLocationRequest() {
    DeliveryLocationRequest request = new DeliveryLocationRequest();
    request.setMode(INVALID_MODE);
    request.setLocation(A_LOCATION);
    return request;
  }

  private DeliveryDestination createDeliveryLocation() {
    return new DeliveryDestination(DeliveryMode.CAMPUS, Location.VACHON);
  }
}
