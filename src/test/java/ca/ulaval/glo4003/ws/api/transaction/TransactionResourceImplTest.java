package ca.ulaval.glo4003.ws.api.transaction;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.api.transaction.dto.BatteryRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.PaymentRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.VehicleRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.BatteryRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.PaymentRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.VehicleRequestValidator;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.delivery.Delivery;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DuplicateDeliveryException;
import ca.ulaval.glo4003.ws.domain.exception.WrongOwnerException;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionService;
import ca.ulaval.glo4003.ws.domain.transaction.exception.DuplicateTransactionException;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import ca.ulaval.glo4003.ws.domain.transaction.payment.Payment;
import ca.ulaval.glo4003.ws.domain.user.OwnershipHandler;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.vehicle.Vehicle;
import ca.ulaval.glo4003.ws.domain.vehicle.VehicleFactory;
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
  private static final TransactionId A_TRANSACTION_ID = new TransactionId("id");
  private static final DeliveryId A_DELIVERY_ID = new DeliveryId("id");

  private final TransactionService transactionService =
      mock(TransactionService.class, RETURNS_DEEP_STUBS);
  @Mock private CreatedTransactionResponseAssembler createdTransactionResponseAssembler;
  @Mock private VehicleRequestValidator vehicleRequestValidator;
  @Mock private RoleHandler roleHandler;
  @Mock private ContainerRequestContext containerRequestContext;
  @Mock private BatteryRequestValidator batteryRequestValidator;
  @Mock private PaymentRequestAssembler paymentRequestAssembler;
  @Mock private PaymentRequestValidator paymentRequestValidator;
  @Mock private VehicleRequest vehicleRequest;
  @Mock private BatteryRequest batteryRequest;
  @Mock private PaymentRequest paymentRequest;
  @Mock private Payment payment;
  @Mock private DeliveryService deliveryService;
  @Mock private OwnershipHandler ownershipHandler;
  @Mock private Session aSession;
  @Mock private VehicleFactory vehicleFactory;
  @Mock private Vehicle aVehicle;
  @Mock private BatteryResponseAssembler batteryResponseAssembler;

  private Transaction transaction;
  private Delivery delivery;
  private TransactionResource transactionResource;

  @BeforeEach
  void setUp() {
    transaction = createTransaction(A_TRANSACTION_ID);
    delivery = createDelivery(A_DELIVERY_ID);
    transactionResource =
        new TransactionResourceImpl(
            transactionService,
            deliveryService,
            ownershipHandler,
            createdTransactionResponseAssembler,
            vehicleRequestValidator,
            roleHandler,
            batteryRequestValidator,
            paymentRequestAssembler,
            paymentRequestValidator,
            vehicleFactory,
            batteryResponseAssembler);
  }

  @Test
  public void givenTransaction_whenCreateTransaction_thenCreateTransactionResponse() {
    // given
    given(transactionService.createTransaction()).willReturn(transaction);
    given(deliveryService.createDelivery()).willReturn(delivery);

    // when
    transactionResource.createTransaction(containerRequestContext);

    // then
    verify(createdTransactionResponseAssembler).assemble(transaction, delivery);
  }

  @Test
  public void whenCreateTransaction_thenRolesAreValidated() {
    // when
    transactionResource.addVehicle(containerRequestContext, A_TRANSACTION_ID, vehicleRequest);

    // then
    verify(roleHandler)
        .retrieveSession(containerRequestContext, new ArrayList<>(List.of(Role.BASE, Role.ADMIN)));
  }

  @Test
  public void
      givenTransactionCreatedSuccessfully_whenCreateTransaction_thenAddTransactionOwnershipToUser() {
    // given
    given(roleHandler.retrieveSession(any(), any())).willReturn(aSession);
    given(transactionService.createTransaction()).willReturn(transaction);
    given(deliveryService.createDelivery()).willReturn(delivery);

    // when
    transactionResource.createTransaction(containerRequestContext);

    // then
    verify(ownershipHandler)
        .mapDeliveryIdToTransactionId(aSession, A_TRANSACTION_ID, A_DELIVERY_ID);
  }

  @Test
  public void
      givenTransactionNotCreatedSuccessfully_whenCreateTransaction_thenDoNotAddTransactionOwnershipToUser() {
    // given
    given(roleHandler.retrieveSession(any(), any())).willReturn(aSession);
    given(transactionService.createTransaction())
        .willThrow(new DuplicateTransactionException(A_TRANSACTION_ID));

    // when
    Executable creatingTransaction =
        () -> transactionResource.createTransaction(containerRequestContext);

    // then
    assertThrows(DuplicateTransactionException.class, creatingTransaction);
    verify(ownershipHandler, times(0))
        .mapDeliveryIdToTransactionId(aSession, A_TRANSACTION_ID, A_DELIVERY_ID);
  }

  @Test
  public void
      givenDeliveryNotCreatedSuccessfully_whenCreateTransaction_thenDoNotAddTransactionOwnershipToUser() {
    // given
    given(roleHandler.retrieveSession(any(), any())).willReturn(aSession);
    given(transactionService.createTransaction()).willReturn(transaction);
    given(deliveryService.createDelivery())
        .willThrow(new DuplicateDeliveryException(A_DELIVERY_ID));

    // when
    Executable creatingTransaction =
        () -> transactionResource.createTransaction(containerRequestContext);

    // then
    assertThrows(DuplicateDeliveryException.class, creatingTransaction);
    verify(ownershipHandler, times(0))
        .mapDeliveryIdToTransactionId(aSession, A_TRANSACTION_ID, A_DELIVERY_ID);
  }

  @Test
  public void whenAddVehicle_thenRolesAreValidated() {
    // when
    transactionResource.addVehicle(containerRequestContext, A_TRANSACTION_ID, vehicleRequest);

    // then
    verify(roleHandler)
        .retrieveSession(containerRequestContext, new ArrayList<>(List.of(Role.BASE, Role.ADMIN)));
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
    // given
    given(vehicleFactory.create(vehicleRequest.getModel(), vehicleRequest.getColor()))
        .willReturn(aVehicle);

    // when
    transactionResource.addVehicle(containerRequestContext, A_TRANSACTION_ID, vehicleRequest);

    // then
    verify(transactionService).addVehicle(A_TRANSACTION_ID, aVehicle);
  }

  @Test
  public void givenTransactionIsNotOwnedByUser_whenAddBattery_thenDoNotAddVehicle() {
    // given
    doThrow(new WrongOwnerException())
        .when(ownershipHandler)
        .validateTransactionOwnership(any(), any());

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
    // given
    given(roleHandler.retrieveSession(any(), any())).willReturn(aSession);

    // when
    transactionResource.addVehicle(containerRequestContext, A_TRANSACTION_ID, vehicleRequest);

    // then
    verify(ownershipHandler).validateTransactionOwnership(aSession, A_TRANSACTION_ID);
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
    doThrow(new WrongOwnerException())
        .when(ownershipHandler)
        .validateTransactionOwnership(any(), any());

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
  public void whenAddBattery_thenRolesAreValidated() {
    // when
    transactionResource.addBattery(containerRequestContext, A_TRANSACTION_ID, batteryRequest);

    // then
    verify(roleHandler)
        .retrieveSession(containerRequestContext, new ArrayList<>(List.of(Role.BASE, Role.ADMIN)));
  }

  @Test
  public void whenAddBattery_thenValidateTransactionOwnership() {
    // given
    given(roleHandler.retrieveSession(any(), any())).willReturn(aSession);

    // when
    transactionResource.addBattery(containerRequestContext, A_TRANSACTION_ID, batteryRequest);

    // then
    verify(ownershipHandler).validateTransactionOwnership(aSession, A_TRANSACTION_ID);
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
  public void givenTransactionIsOwnedByUser_whenCompletePayment_thenAddPayment() {
    // given
    given(paymentRequestAssembler.create(paymentRequest)).willReturn(payment);

    // when
    transactionResource.completeTransaction(
        containerRequestContext, A_TRANSACTION_ID, paymentRequest);

    // then
    verify(transactionService).addPayment(A_TRANSACTION_ID, payment);
  }

  @Test
  public void givenTransactionIsNotOwnedByUser_whenCompletePayment_thenDoNotAddPayment() {
    // given
    doThrow(new WrongOwnerException())
        .when(ownershipHandler)
        .validateTransactionOwnership(any(), any());

    // when
    Executable completingTransaction =
        () ->
            transactionResource.completeTransaction(
                containerRequestContext, A_TRANSACTION_ID, paymentRequest);

    // then
    assertThrows(TransactionNotFoundException.class, completingTransaction);
    verify(transactionService, times(0)).addPayment(any(), any());
  }

  @Test
  public void whenCompleteTransaction_thenRolesAreValidated() {
    // when
    transactionResource.completeTransaction(
        containerRequestContext, A_TRANSACTION_ID, paymentRequest);

    // then
    verify(roleHandler)
        .retrieveSession(containerRequestContext, new ArrayList<>(List.of(Role.BASE, Role.ADMIN)));
  }

  @Test
  public void whenCompleteTransaction_thenValidateTransactionOwnership() {
    // given
    given(roleHandler.retrieveSession(any(), any())).willReturn(aSession);

    // when
    transactionResource.completeTransaction(
        containerRequestContext, A_TRANSACTION_ID, paymentRequest);

    // then
    verify(ownershipHandler).validateTransactionOwnership(aSession, A_TRANSACTION_ID);
  }

  private Transaction createTransaction(TransactionId id) {
    return new Transaction(id);
  }

  private Delivery createDelivery(DeliveryId id) {
    return new Delivery(id);
  }
}
