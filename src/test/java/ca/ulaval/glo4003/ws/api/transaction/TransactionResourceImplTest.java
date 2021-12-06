package ca.ulaval.glo4003.ws.api.transaction;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DuplicateDeliveryException;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.exception.DuplicateTransactionException;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.service.authentication.AuthenticationService;
import ca.ulaval.glo4003.ws.service.transaction.TransactionService;
import ca.ulaval.glo4003.ws.service.transaction.dto.BatteryRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.CreatedTransactionResponse;
import ca.ulaval.glo4003.ws.service.transaction.dto.PaymentRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.VehicleRequest;
import ca.ulaval.glo4003.ws.service.transaction.dto.validators.BatteryRequestValidator;
import ca.ulaval.glo4003.ws.service.transaction.dto.validators.PaymentRequestValidator;
import ca.ulaval.glo4003.ws.service.transaction.dto.validators.VehicleRequestValidator;
import jakarta.ws.rs.container.ContainerRequestContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionResourceImplTest {
  private static final String AN_ID = "ID";
  private static final TransactionId A_TRANSACTION_ID = new TransactionId("id");
  private static final DeliveryId A_DELIVERY_ID = new DeliveryId("id");
  private static final List<Role> ROLES = new ArrayList<>(List.of(Role.BASE, Role.ADMIN));

  @Mock private TransactionService transactionService;
  @Mock private AuthenticationService authenticationService;
  @Mock private VehicleRequestValidator vehicleRequestValidator;
  @Mock private ContainerRequestContext containerRequestContext;
  @Mock private BatteryRequestValidator batteryRequestValidator;
  @Mock private PaymentRequestValidator paymentRequestValidator;
  @Mock private VehicleRequest vehicleRequest;
  @Mock private BatteryRequest batteryRequest;
  @Mock private PaymentRequest paymentRequest;
  @Mock private Session aSession;

  private TransactionResource transactionResource;

  @BeforeEach
  void setUp() {
    transactionResource =
        new TransactionResourceImpl(
            transactionService,
            authenticationService,
            vehicleRequestValidator,
            batteryRequestValidator,
            paymentRequestValidator);
  }

  @Test
  public void
      givenTransactionCreatedSuccessfully_whenCreateTransaction_thenAddTransactionOwnershipToUser() {
    // given
    var transactionResponse = createdTransactionResponse(AN_ID, AN_ID);
    given(authenticationService.retrieveSession(any(), any())).willReturn(aSession);
    given(transactionService.createTransaction()).willReturn(transactionResponse);

    // when
    transactionResource.createTransaction(containerRequestContext);

    // then
    verify(authenticationService).mapDeliveryIdToTransactionId(aSession, AN_ID, AN_ID);
  }

  @Test
  public void
      givenTransactionNotCreatedSuccessfully_whenCreateTransaction_thenDoNotAddTransactionOwnershipToUser() {
    // given
    given(transactionService.createTransaction())
        .willThrow(new DuplicateTransactionException(A_TRANSACTION_ID));

    // when
    Executable creatingTransaction =
        () -> transactionResource.createTransaction(containerRequestContext);

    // then
    assertThrows(DuplicateTransactionException.class, creatingTransaction);
    verify(authenticationService, times(0)).mapDeliveryIdToTransactionId(any(), any(), any());
  }

  @Test
  public void
      givenDeliveryNotCreatedSuccessfully_whenCreateTransaction_thenDoNotAddTransactionOwnershipToUser() {
    // given
    given(transactionService.createTransaction())
        .willThrow(new DuplicateDeliveryException(A_DELIVERY_ID));

    // when
    Executable creatingTransaction =
        () -> transactionResource.createTransaction(containerRequestContext);

    // then
    assertThrows(DuplicateDeliveryException.class, creatingTransaction);
    verify(authenticationService, times(0)).mapDeliveryIdToTransactionId(any(), any(), any());
  }

  @Test
  public void givenVehicleRequest_whenAddVehicle_thenValidateRequest() {
    // when
    transactionResource.addVehicle(containerRequestContext, A_TRANSACTION_ID, vehicleRequest);

    // then
    verify(vehicleRequestValidator).validate(vehicleRequest);
  }

  @Test
  public void givenTransactionIsOwnedByUser_whenAddVehicle_thenAddVehicle() {
    // when
    transactionResource.addVehicle(containerRequestContext, A_TRANSACTION_ID, vehicleRequest);

    // then
    verify(transactionService).addVehicle(A_TRANSACTION_ID, vehicleRequest);
  }

  @Test
  public void givenTransactionIsNotOwnedByUser_whenAddBattery_thenDoNotAddVehicle() {
    // given
    doThrow(new TransactionNotFoundException(A_TRANSACTION_ID))
        .when(authenticationService)
        .validateTransactionOwnership(any(), any(), any());

    // when
    Executable addingBattery =
        () ->
            transactionResource.addVehicle(
                containerRequestContext, A_TRANSACTION_ID, vehicleRequest);

    // then
    assertThrows(TransactionNotFoundException.class, addingBattery);
    verify(transactionService, times(0)).addVehicle(any(), any());
  }

  @Test
  public void whenAddVehicle_thenValidateTransactionOwnership() {
    // when
    transactionResource.addVehicle(containerRequestContext, A_TRANSACTION_ID, vehicleRequest);

    // then
    verify(authenticationService)
        .validateTransactionOwnership(containerRequestContext, A_TRANSACTION_ID, ROLES);
  }

  @Test
  public void givenTransactionIsOwnedByUser_whenAddBattery_thenBatteryIsAdded() {
    // when
    transactionResource.addBattery(containerRequestContext, A_TRANSACTION_ID, batteryRequest);

    // then
    verify(batteryRequestValidator).validate(batteryRequest);
  }

  @Test
  public void givenTransactionIsNotOwnedByUser_whenAddBattery_thenDoNotAddBattery() {
    // given
    doThrow(new TransactionNotFoundException(A_TRANSACTION_ID))
        .when(authenticationService)
        .validateTransactionOwnership(any(), any(), any());

    // when
    Executable addingBattery =
        () ->
            transactionResource.addBattery(
                containerRequestContext, A_TRANSACTION_ID, batteryRequest);

    // then
    assertThrows(TransactionNotFoundException.class, addingBattery);
    verify(transactionService, times(0)).addBattery(any(), any());
  }

  @Test
  public void whenAddBattery_thenValidateTransactionOwnership() {
    // when
    transactionResource.addBattery(containerRequestContext, A_TRANSACTION_ID, batteryRequest);

    // then
    verify(authenticationService)
        .validateTransactionOwnership(containerRequestContext, A_TRANSACTION_ID, ROLES);
  }

  @Test
  void whenAddPayment_thenValidatePaymentRequest() {
    // when
    transactionResource.completeTransaction(
        containerRequestContext, A_TRANSACTION_ID, paymentRequest);

    // then
    verify(paymentRequestValidator).validate(paymentRequest);
  }

  @Test
  public void whenCompleteTransaction_thenCompleteTransactionCalled() {
    // when
    transactionResource.completeTransaction(
        containerRequestContext, A_TRANSACTION_ID, paymentRequest);

    // then
    verify(transactionService).completeTransaction(A_TRANSACTION_ID, paymentRequest);
  }

  @Test
  public void givenTransactionIsNotOwnedByUser_whenCompleteTransaction_thenDoNotAddPayment() {
    // given
    doThrow(new TransactionNotFoundException(A_TRANSACTION_ID))
        .when(authenticationService)
        .validateTransactionOwnership(any(), any(), any());

    // when
    Executable completingTransaction =
        () ->
            transactionResource.completeTransaction(
                containerRequestContext, A_TRANSACTION_ID, paymentRequest);

    // then
    assertThrows(TransactionNotFoundException.class, completingTransaction);
    verify(transactionService, times(0)).completeTransaction(any(), any());
  }

  @Test
  public void whenCompleteTransaction_thenValidateOwnership() {
    // when
    transactionResource.completeTransaction(
        containerRequestContext, A_TRANSACTION_ID, paymentRequest);

    // then
    verify(authenticationService)
        .validateTransactionOwnership(containerRequestContext, A_TRANSACTION_ID, ROLES);
  }

  @Test
  public void whenCompleteTransaction_thenValidateTransactionOwnership() {
    // when
    transactionResource.completeTransaction(
        containerRequestContext, A_TRANSACTION_ID, paymentRequest);

    // then
    verify(authenticationService)
        .validateTransactionOwnership(containerRequestContext, A_TRANSACTION_ID, ROLES);
  }

  private CreatedTransactionResponse createdTransactionResponse(
      String transactionId, String deliveryId) {
    var transactionResponse = new CreatedTransactionResponse();
    transactionResponse.transactionId = transactionId;
    transactionResponse.deliveryId = deliveryId;
    return transactionResponse;
  }
}
