package ca.ulaval.glo4003.ws.api.transaction;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.api.shared.RequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.request.ConfigureBatteryRequest;
import ca.ulaval.glo4003.ws.api.transaction.request.ConfigurePaymentRequest;
import ca.ulaval.glo4003.ws.api.transaction.request.ConfigureVehicleRequest;
import ca.ulaval.glo4003.ws.api.transaction.response.BatteryConfigurationResponseAssembler;
import ca.ulaval.glo4003.ws.api.transaction.response.TransactionCreationResponse;
import ca.ulaval.glo4003.ws.api.transaction.response.TransactionCreationResponseAssembler;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DuplicateDeliveryException;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.exception.DuplicateTransactionException;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.service.authentication.AuthenticationService;
import ca.ulaval.glo4003.ws.service.transaction.TransactionService;
import ca.ulaval.glo4003.ws.service.transaction.dto.BatteryConfigurationDto;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigureBatteryDto;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigurePaymentDto;
import ca.ulaval.glo4003.ws.service.transaction.dto.ConfigureVehicleDto;
import ca.ulaval.glo4003.ws.service.transaction.dto.TransactionCreationDto;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
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
class TransactionResourceImplTest {
  private static final TransactionId A_TRANSACTION_ID = new TransactionId("id");
  private static final DeliveryId A_DELIVERY_ID = new DeliveryId("id");
  private static final List<Role> ROLES = new ArrayList<>(List.of(Role.BASE, Role.ADMIN));
  private static final BigDecimal A_RANGE = BigDecimal.valueOf(424332);

  @Mock private TransactionService transactionService;
  @Mock private AuthenticationService authenticationService;
  @Mock private ContainerRequestContext containerRequestContext;
  @Mock private ConfigureVehicleRequest configureVehicleRequest;
  @Mock private ConfigureBatteryRequest configureBatteryRequest;
  @Mock private ConfigurePaymentRequest configurePaymentRequest;
  @Mock private RequestValidator requestValidator;
  @Mock private ConfigureVehicleDtoAssembler configureVehicleDtoAssembler;
  @Mock private ConfigureBatteryDtoAssembler configureBatteryDtoAssembler;
  @Mock private ConfigurePaymentDtoAssembler configurePaymentDtoAssembler;
  @Mock private BatteryConfigurationResponseAssembler configureBatteryResponseAssembler;
  @Mock private TransactionCreationResponseAssembler transactionCreationResponseAssembler;

  @Mock private Session aSession;
  @Mock private ConfigureVehicleDto configureVehicleDto;
  @Mock private ConfigureBatteryDto configureBatteryDto;
  @Mock private ConfigurePaymentDto configurePaymentDto;
  @Mock private TransactionCreationDto transactionCreationDto;
  @Mock private BatteryConfigurationDto batteryConfigurationDto;
  @Mock private TransactionCreationResponse transactionCreationResponse;

  private TransactionResource transactionResource;

  @BeforeEach
  void setUp() {
    transactionResource =
        new TransactionResourceImpl(
            transactionService,
            authenticationService,
            configureVehicleDtoAssembler,
            configureBatteryDtoAssembler,
            configureBatteryResponseAssembler,
            configurePaymentDtoAssembler,
            requestValidator,
            transactionCreationResponseAssembler);
  }

  @Test
  public void whenCreateTransaction_thenReturnValidResponse() {
    // given
    given(transactionCreationDto.getTransactionId()).willReturn(A_TRANSACTION_ID.getId());
    given(transactionCreationDto.getDeliveryId()).willReturn(A_DELIVERY_ID.getDeliveryId());
    given(authenticationService.retrieveSession(any(), any())).willReturn(aSession);
    given(transactionService.createTransaction()).willReturn(transactionCreationDto);
    TransactionCreationResponse transactionCreationResponse =
        new TransactionCreationResponse(A_TRANSACTION_ID.getId(), A_DELIVERY_ID.getDeliveryId());
    given(transactionCreationResponseAssembler.assemble(transactionCreationDto))
        .willReturn(transactionCreationResponse);

    // when
    Response response = transactionResource.createTransaction(containerRequestContext);

    // then
    assertThat(response.getEntity()).isEqualTo(transactionCreationResponse);
  }

  @Test
  public void
      givenTransactionCreatedSuccessfully_whenCreateTransaction_thenAddTransactionOwnershipToUser() {
    // given
    given(transactionCreationDto.getTransactionId()).willReturn(A_TRANSACTION_ID.getId());
    given(transactionCreationDto.getDeliveryId()).willReturn(A_DELIVERY_ID.getDeliveryId());
    given(authenticationService.retrieveSession(any(), any())).willReturn(aSession);
    given(transactionService.createTransaction()).willReturn(transactionCreationDto);

    // when
    transactionResource.createTransaction(containerRequestContext);

    // then
    verify(authenticationService)
        .mapDeliveryIdToTransactionId(
            aSession, A_TRANSACTION_ID.getId(), A_DELIVERY_ID.getDeliveryId());
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
  public void givenConfigureVehicleRequest_whenConfigureVehicle_thenValidateRequest() {
    // when
    transactionResource.configureVehicle(
        containerRequestContext, A_TRANSACTION_ID, configureVehicleRequest);

    // then
    verify(requestValidator).validate(configureVehicleRequest);
  }

  @Test
  public void givenTransactionIsOwnedByUser_whenConfigureVehicle_thenVehicleIsConfigured() {
    // given
    given(configureVehicleDtoAssembler.assemble(configureVehicleRequest))
        .willReturn(configureVehicleDto);

    // when
    transactionResource.configureVehicle(
        containerRequestContext, A_TRANSACTION_ID, configureVehicleRequest);

    // then
    verify(transactionService).configureVehicle(A_TRANSACTION_ID, configureVehicleDto);
  }

  @Test
  public void givenTransactionIsNotOwnedByUser_whenConfigureVehicle_thenVehicleIsNotConfigured() {
    // given
    doThrow(new TransactionNotFoundException(A_TRANSACTION_ID))
        .when(authenticationService)
        .validateTransactionOwnership(any(), any(), any());

    // when
    Executable addingBattery =
        () ->
            transactionResource.configureVehicle(
                containerRequestContext, A_TRANSACTION_ID, configureVehicleRequest);

    // then
    assertThrows(TransactionNotFoundException.class, addingBattery);
    verify(transactionService, times(0)).configureVehicle(any(), any());
  }

  @Test
  public void whenConfigureVehicle_thenValidateTransactionOwnership() {
    // when
    transactionResource.configureVehicle(
        containerRequestContext, A_TRANSACTION_ID, configureVehicleRequest);

    // then
    verify(authenticationService)
        .validateTransactionOwnership(containerRequestContext, A_TRANSACTION_ID, ROLES);
  }

  @Test
  public void givenAConfigureBatteryRequest_whenConfigureBattery_thenRequestIsValidated() {
    // given
    given(configureBatteryDtoAssembler.assemble(configureBatteryRequest))
        .willReturn(configureBatteryDto);
    given(batteryConfigurationDto.getEstimatedRange()).willReturn(A_RANGE);
    given(transactionService.configureBattery(A_TRANSACTION_ID, configureBatteryDto))
        .willReturn(batteryConfigurationDto);

    // when
    transactionResource.configureBattery(
        containerRequestContext, A_TRANSACTION_ID, configureBatteryRequest);

    // then
    verify(requestValidator).validate(configureBatteryRequest);
  }

  @Test
  public void givenTransactionIsNotOwnedByUser_whenConfigureBattery_thenBatteryIsNotConfigured() {
    // given
    doThrow(new TransactionNotFoundException(A_TRANSACTION_ID))
        .when(authenticationService)
        .validateTransactionOwnership(any(), any(), any());

    // when
    Executable configuringBattery =
        () ->
            transactionResource.configureBattery(
                containerRequestContext, A_TRANSACTION_ID, configureBatteryRequest);

    // then
    assertThrows(TransactionNotFoundException.class, configuringBattery);
    verify(transactionService, times(0)).configureBattery(any(), any());
  }

  @Test
  public void whenConfigureBattery_thenValidateTransactionOwnership() {
    // given
    given(configureBatteryDtoAssembler.assemble(configureBatteryRequest))
        .willReturn(configureBatteryDto);
    given(batteryConfigurationDto.getEstimatedRange()).willReturn(A_RANGE);
    given(transactionService.configureBattery(A_TRANSACTION_ID, configureBatteryDto))
        .willReturn(batteryConfigurationDto);

    // when
    transactionResource.configureBattery(
        containerRequestContext, A_TRANSACTION_ID, configureBatteryRequest);

    // then
    verify(authenticationService)
        .validateTransactionOwnership(containerRequestContext, A_TRANSACTION_ID, ROLES);
  }

  @Test
  void whenCompleteTransaction_thenValidatePaymentRequest() {
    // when
    transactionResource.completeTransaction(
        containerRequestContext, A_TRANSACTION_ID, configurePaymentRequest);

    // then
    verify(requestValidator).validate(configurePaymentRequest);
  }

  @Test
  public void whenCompleteTransaction_thenCompleteTransactionCalled() {
    // given
    given(configurePaymentDtoAssembler.assemble(configurePaymentRequest))
        .willReturn(configurePaymentDto);

    // when
    transactionResource.completeTransaction(
        containerRequestContext, A_TRANSACTION_ID, configurePaymentRequest);

    // then
    verify(transactionService).completeTransaction(A_TRANSACTION_ID, configurePaymentDto);
  }

  @Test
  public void
      givenTransactionIsNotOwnedByUser_whenCompleteTransaction_thenDoNotCompleteTransaction() {
    // given
    doThrow(new TransactionNotFoundException(A_TRANSACTION_ID))
        .when(authenticationService)
        .validateTransactionOwnership(any(), any(), any());

    // when
    Executable completingTransaction =
        () ->
            transactionResource.completeTransaction(
                containerRequestContext, A_TRANSACTION_ID, configurePaymentRequest);

    // then
    assertThrows(TransactionNotFoundException.class, completingTransaction);
    verify(transactionService, times(0)).completeTransaction(any(), any());
  }

  @Test
  public void whenCompleteTransaction_thenValidateOwnership() {
    // when
    transactionResource.completeTransaction(
        containerRequestContext, A_TRANSACTION_ID, configurePaymentRequest);

    // then
    verify(authenticationService)
        .validateTransactionOwnership(containerRequestContext, A_TRANSACTION_ID, ROLES);
  }

  @Test
  public void whenCompleteTransaction_thenValidateTransactionOwnership() {
    // when
    transactionResource.completeTransaction(
        containerRequestContext, A_TRANSACTION_ID, configurePaymentRequest);

    // then
    verify(authenticationService)
        .validateTransactionOwnership(containerRequestContext, A_TRANSACTION_ID, ROLES);
  }
}
