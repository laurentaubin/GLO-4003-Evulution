package ca.ulaval.glo4003.ws.api.transaction;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.api.transaction.dto.BatteryRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.PaymentRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.VehicleRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.BatteryRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.PaymentRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.VehicleRequestValidator;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.domain.delivery.Delivery;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryService;
import ca.ulaval.glo4003.ws.domain.transaction.Payment;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionService;
import ca.ulaval.glo4003.ws.domain.transaction.exception.DuplicateTransactionException;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.TransactionOwnershipHandler;
import ca.ulaval.glo4003.ws.domain.user.exception.WrongOwnerException;
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
  private static final TransactionId AN_ID = new TransactionId("id");
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
  @Mock private Battery A_BATTERY;
  @Mock private TransactionOwnershipHandler transactionOwnershipHandler;
  @Mock private Session aSession;

  private Transaction transaction;
  private Delivery delivery;
  private TransactionResource transactionResource;

  @BeforeEach
  void setUp() {
    transaction = createTransaction(AN_ID);
    delivery = createDelivery(A_DELIVERY_ID);
    transactionResource =
        new TransactionResourceImpl(
            transactionService,
            deliveryService,
            transactionOwnershipHandler,
            createdTransactionResponseAssembler,
            vehicleRequestValidator,
            roleHandler,
            batteryRequestValidator,
            paymentRequestAssembler,
            paymentRequestValidator);
  }

  @Test
  public void givenTransaction_whenCreateTransaction_thenCreateTransactionResponse() {
    // given
    when(transactionService.createTransaction()).thenReturn(transaction);
    when(deliveryService.createDelivery()).thenReturn(delivery);

    // when
    transactionResource.createTransaction(containerRequestContext);

    // then
    verify(createdTransactionResponseAssembler).assemble(transaction, delivery);
  }

  @Test
  public void whenCreateTransaction_thenRolesAreValidated() {
    // when
    transactionResource.addVehicle(containerRequestContext, AN_ID.toString(), vehicleRequest);

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

    // when
    transactionResource.createTransaction(containerRequestContext);

    // then
    verify(transactionOwnershipHandler).addTransactionOwnership(aSession, AN_ID);
  }

  @Test
  public void
      givenTransactionNotCreatedSuccessfully_whenCreateTransaction_thenDoNotAddTransactionOwnershipToUser() {
    // given
    given(roleHandler.retrieveSession(any(), any())).willReturn(aSession);
    given(transactionService.createTransaction())
        .willThrow(new DuplicateTransactionException(AN_ID));

    // when
    Executable creatingTransaction =
        () -> transactionResource.createTransaction(containerRequestContext);

    // then
    assertThrows(DuplicateTransactionException.class, creatingTransaction);
    verify(transactionOwnershipHandler, times(0)).addTransactionOwnership(aSession, AN_ID);
  }

  @Test
  public void whenAddVehicle_thenRolesAreValidated() {
    // when
    transactionResource.addVehicle(containerRequestContext, AN_ID.toString(), vehicleRequest);

    // then
    verify(roleHandler)
        .retrieveSession(containerRequestContext, new ArrayList<>(List.of(Role.BASE, Role.ADMIN)));
  }

  @Test
  public void givenVehicleRequest_whenAddVehicle_thenValidateRequest() {
    // when
    transactionResource.addVehicle(containerRequestContext, AN_ID.toString(), vehicleRequest);

    // then
    verify(vehicleRequestValidator).validate(vehicleRequest);
  }

  @Test
  public void givenTransactionIsOwnedByUser_whenAddVehicle_thenAddVehicle() {
    // when
    transactionResource.addVehicle(containerRequestContext, AN_ID.toString(), vehicleRequest);

    // then
    verify(transactionService).addVehicle(AN_ID, vehicleRequest);
  }

  @Test
  public void givenTransactionIsNotOwnedByUser_whenAddBattery_thenDoNotAddVehicle() {
    // given
    doThrow(new WrongOwnerException())
        .when(transactionOwnershipHandler)
        .validateOwnership(any(), any());

    // when
    Executable addingBattery =
        () ->
            transactionResource.addVehicle(
                containerRequestContext, AN_ID.toString(), vehicleRequest);

    // then
    assertThrows(WrongOwnerException.class, addingBattery);
    verify(transactionService, times(0)).addVehicle(any(), any());
  }

  @Test
  public void whenAddVehicle_thenValidateTransactionOwnership() {
    // given
    given(roleHandler.retrieveSession(any(), any())).willReturn(aSession);

    // when
    transactionResource.addVehicle(containerRequestContext, AN_ID.toString(), vehicleRequest);

    // then
    verify(transactionOwnershipHandler).validateOwnership(aSession, AN_ID);
  }

  @Test
  public void givenTransactionIsOwnedByUser_whenAddBattery_thenBatteryIsAdded() {
    // when
    transactionResource.addBattery(containerRequestContext, AN_ID.toString(), batteryRequest);

    // then
    verify(batteryRequestValidator).validate(batteryRequest);
  }

  @Test
  public void givenTransactionIsNotOwnedByUser_whenAddBattery_thenDoNotAddBattery() {
    // given
    doThrow(new WrongOwnerException())
        .when(transactionOwnershipHandler)
        .validateOwnership(any(), any());

    // when
    Executable addingBattery =
        () ->
            transactionResource.addBattery(
                containerRequestContext, AN_ID.toString(), batteryRequest);

    // then
    assertThrows(WrongOwnerException.class, addingBattery);
    verify(transactionService, times(0)).addBattery(any(), any());
  }

  @Test
  public void whenAddBattery_thenRolesAreValidated() {
    // when
    transactionResource.addBattery(containerRequestContext, AN_ID.toString(), batteryRequest);

    // then
    verify(roleHandler)
        .retrieveSession(containerRequestContext, new ArrayList<>(List.of(Role.BASE, Role.ADMIN)));
  }

  @Test
  public void whenAddBattery_thenValidateTransactionOwnership() {
    // given
    given(roleHandler.retrieveSession(any(), any())).willReturn(aSession);

    // when
    transactionResource.addBattery(containerRequestContext, AN_ID.toString(), batteryRequest);

    // then
    verify(transactionOwnershipHandler).validateOwnership(aSession, AN_ID);
  }

  @Test
  void whenAddPayment_thenValidatePaymentRequest() {
    // when
    transactionResource.completeTransaction(
        containerRequestContext, AN_ID.toString(), paymentRequest);

    // then
    verify(paymentRequestValidator).validate(paymentRequest);
  }

  @Test
  public void givenTransactionIsOwnedByUser_whenCompletePayment_thenAddPayment() {
    // given
    when(paymentRequestAssembler.create(paymentRequest)).thenReturn(payment);

    // when
    transactionResource.completeTransaction(
        containerRequestContext, AN_ID.toString(), paymentRequest);

    // then
    verify(transactionService).addPayment(AN_ID, payment);
  }

  @Test
  public void givenTransactionIsNotOwnedByUser_whenCompletePayment_thenDoNotAddPayment() {
    // given
    doThrow(new WrongOwnerException())
        .when(transactionOwnershipHandler)
        .validateOwnership(any(), any());

    // when
    Executable completingTransaction =
        () ->
            transactionResource.completeTransaction(
                containerRequestContext, AN_ID.toString(), paymentRequest);

    // then
    assertThrows(WrongOwnerException.class, completingTransaction);
    verify(transactionService, times(0)).addPayment(any(), any());
  }

  @Test
  public void whenCompleteTransaction_thenRolesAreValidated() {
    // when
    transactionResource.completeTransaction(
        containerRequestContext, AN_ID.toString(), paymentRequest);

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
        containerRequestContext, AN_ID.toString(), paymentRequest);

    // then
    verify(transactionOwnershipHandler).validateOwnership(aSession, AN_ID);
  }

  private Transaction createTransaction(TransactionId id) {
    return new Transaction(id);
  }

  private Delivery createDelivery(DeliveryId id) {
    return new Delivery(id);
  }
}
