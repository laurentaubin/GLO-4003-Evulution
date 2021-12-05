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
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.domain.delivery.Location;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DeliveryNotFoundException;
import ca.ulaval.glo4003.ws.domain.exception.WrongOwnerException;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Frequency;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Receipt;
import ca.ulaval.glo4003.ws.domain.user.OwnershipHandler;
import ca.ulaval.glo4003.ws.domain.user.Role;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.math.BigDecimal;
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
  private static final Integer AMOUNT_OF_YEARS_TO_PAY_OVER = 6;
  private static final Price A_PRICE = new Price(new BigDecimal(1200));
  private static final Receipt A_RECEIPT =
      new Receipt(A_PRICE, Frequency.MONTHLY, AMOUNT_OF_YEARS_TO_PAY_OVER);

  @Mock private DeliveryService deliveryService;
  @Mock private DeliveryDestinationAssembler deliveryDestinationAssembler;
  @Mock private DeliveryRequestValidator deliveryRequestValidator;
  @Mock private OwnershipHandler ownershipHandler;
  @Mock private RoleHandler roleHandler;
  @Mock ContainerRequestContext containerRequestContext;
  @Mock CompletedDeliveryResponseAssembler completedDeliveryResponseAssembler;
  @Mock private Session aSession;

  private DeliveryResource deliveryResource;

  @BeforeEach
  void setUp() {
    deliveryResource =
        new DeliveryResourceImpl(
            deliveryService,
            deliveryRequestValidator,
            deliveryDestinationAssembler,
            completedDeliveryResponseAssembler,
            ownershipHandler,
            roleHandler);
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
    DeliveryDestination deliveryDestination = createDeliveryLocation();
    given(deliveryDestinationAssembler.assemble(request)).willReturn(deliveryDestination);

    // when
    deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID, request);

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
        () -> deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID, invalidRequest);

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
        deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID, request);

    // then
    assertThat(response.getStatus()).isEqualTo(Status.ACCEPTED.getStatusCode());
    assertThat(response.getEntity()).isEqualTo(EXPECTED_DELIVERY_LOCATION_ADDED_MESSAGE);
  }

  @Test
  public void whenAddDeliveryLocation_thenRolesAreValidated() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();

    // when
    deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID, request);

    // then
    verify(roleHandler)
        .retrieveSession(containerRequestContext, new ArrayList<>(List.of(Role.BASE, Role.ADMIN)));
  }

  @Test
  public void whenAddDeliveryLocation_thenValidateDeliveryOwnership() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();
    given(roleHandler.retrieveSession(any(), any())).willReturn(aSession);

    // when
    deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID, request);

    // then
    verify(ownershipHandler).validateDeliveryOwnership(aSession, AN_ID);
  }

  @Test
  public void
      givenWrongOwnerException_whenAddDeliveryLocation_thenThrowDeliveryNotFoundException() {
    // given
    DeliveryLocationRequest request = createDeliveryLocationRequest();
    given(roleHandler.retrieveSession(any(), any())).willReturn(aSession);
    doThrow(WrongOwnerException.class)
        .when(ownershipHandler)
        .validateDeliveryOwnership(any(), any());

    // when
    Executable addingDeliveryLocation =
        () -> deliveryResource.addDeliveryLocation(containerRequestContext, AN_ID, request);

    // then
    assertThrows(DeliveryNotFoundException.class, addingDeliveryLocation);
  }

  @Test
  void whenCompleteDelivery_thenReturnCompletedDeliveryResponse() {
    // given
    given(deliveryService.generateTransactionReceipt(any())).willReturn(A_RECEIPT);

    // when
    deliveryResource.completeDelivery(containerRequestContext, AN_ID);

    // then
    verify(completedDeliveryResponseAssembler).assemble(A_RECEIPT);
  }

  @Test
  void whenCompleteDelivery_thenRolesAreValidated() {
    // given
    given(deliveryService.generateTransactionReceipt(any())).willReturn(A_RECEIPT);

    // when
    deliveryResource.completeDelivery(containerRequestContext, AN_ID);

    // then
    verify(roleHandler)
        .retrieveSession(containerRequestContext, new ArrayList<>(List.of(Role.BASE, Role.ADMIN)));
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
