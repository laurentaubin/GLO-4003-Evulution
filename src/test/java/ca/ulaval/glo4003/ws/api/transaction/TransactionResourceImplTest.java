package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.shared.RequestValidator;
import ca.ulaval.glo4003.ws.api.shared.TokenExtractor;
import ca.ulaval.glo4003.ws.api.transaction.request.ConfigureBatteryRequest;
import ca.ulaval.glo4003.ws.api.transaction.request.ConfigurePaymentRequest;
import ca.ulaval.glo4003.ws.api.transaction.request.ConfigureVehicleRequest;
import ca.ulaval.glo4003.ws.api.transaction.response.BatteryConfigurationResponseAssembler;
import ca.ulaval.glo4003.ws.api.transaction.response.TransactionCreationResponse;
import ca.ulaval.glo4003.ws.api.transaction.response.TransactionCreationResponseAssembler;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.service.transaction.TransactionService;
import ca.ulaval.glo4003.ws.service.transaction.dto.*;
import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionResourceImplTest {
  private static final TransactionId A_TRANSACTION_ID = new TransactionId("id");
  private static final DeliveryId A_DELIVERY_ID = new DeliveryId("id");
  private static final BigDecimal A_RANGE = BigDecimal.valueOf(424332);

  @Mock private TransactionService transactionService;
  @Mock private ContainerRequestContext containerRequestContext;
  @Mock private ConfigureVehicleRequest configureVehicleRequest;
  @Mock private ConfigureBatteryRequest configureBatteryRequest;
  @Mock private ConfigurePaymentRequest configurePaymentRequest;
  @Mock private RequestValidator requestValidator;
  @Mock private TokenExtractor tokenExtractor;
  @Mock private ConfigureVehicleDtoAssembler configureVehicleDtoAssembler;
  @Mock private ConfigureBatteryDtoAssembler configureBatteryDtoAssembler;
  @Mock private ConfigurePaymentDtoAssembler configurePaymentDtoAssembler;
  @Mock private BatteryConfigurationResponseAssembler configureBatteryResponseAssembler;
  @Mock private TransactionCreationResponseAssembler transactionCreationResponseAssembler;

  @Mock private ConfigureVehicleDto configureVehicleDto;
  @Mock private ConfigureBatteryDto configureBatteryDto;
  @Mock private ConfigurePaymentDto configurePaymentDto;
  @Mock private TransactionCreationDto transactionCreationDto;
  @Mock private BatteryConfigurationDto batteryConfigurationDto;
  @Mock private TokenDto tokenDto;

  private TransactionResource transactionResource;

  @BeforeEach
  void setUp() {
    transactionResource =
        new TransactionResourceImpl(
            transactionService,
            configureVehicleDtoAssembler,
            configureBatteryDtoAssembler,
            configureBatteryResponseAssembler,
            configurePaymentDtoAssembler,
            requestValidator,
            transactionCreationResponseAssembler,
                tokenExtractor);
  }

  @Test
  public void whenCreateTransaction_thenReturnValidResponse() {
    // given
    given(tokenExtractor.extract(containerRequestContext)).willReturn(tokenDto);
    given(transactionService.createTransaction(tokenDto)).willReturn(transactionCreationDto);
    TransactionCreationResponse transactionCreationResponse =
        new TransactionCreationResponse(A_TRANSACTION_ID.getId(), A_DELIVERY_ID.getDeliveryId());
    given(transactionCreationResponseAssembler.assemble(transactionCreationDto))
        .willReturn(transactionCreationResponse);

    // when
    Response response = transactionResource.createTransaction(containerRequestContext);

    // then
    assertThat(response.getEntity()).isEqualTo(transactionCreationResponse);
  }

  @Test public void whenCreateTransaction_thenTransactionServiceIsCalledCorrectly() {
    // given
    given(tokenExtractor.extract(containerRequestContext)).willReturn(tokenDto);
    given(transactionService.createTransaction(tokenDto)).willReturn(transactionCreationDto);

    // when
    transactionResource.createTransaction(containerRequestContext);

    // then
    verify(transactionService).createTransaction(tokenDto);
  }

  @Test public void whenCreateTransaction_thenTokenIsExtracted() {
    // given
    given(tokenExtractor.extract(containerRequestContext)).willReturn(tokenDto);
    given(transactionService.createTransaction(tokenDto)).willReturn(transactionCreationDto);

    // when
    transactionResource.createTransaction(containerRequestContext);

    // then
    verify(tokenExtractor).extract(containerRequestContext);
  }

  @Test
  public void givenConfigureVehicleRequest_whenConfigureVehicle_thenValidateRequest() {
    // when
    transactionResource.configureVehicle(
        containerRequestContext, A_TRANSACTION_ID, configureVehicleRequest);

    // then
    verify(requestValidator).validate(configureVehicleRequest);
  }

  @Test public void whenConfigureVehicle_thenTokenIsExtracted() {
    // given
    given(tokenExtractor.extract(containerRequestContext)).willReturn(tokenDto);

    // when
    transactionResource.configureVehicle(containerRequestContext, A_TRANSACTION_ID, configureVehicleRequest);

    // then
    verify(tokenExtractor).extract(containerRequestContext);
  }

  @Test
  public void givenTransactionIsOwnedByUser_whenConfigureVehicle_thenVehicleIsConfigured() {
    // given
    given(configureVehicleDtoAssembler.assemble(configureVehicleRequest))
        .willReturn(configureVehicleDto);
    given(tokenExtractor.extract(containerRequestContext)).willReturn(tokenDto);

    // when
    transactionResource.configureVehicle(
        containerRequestContext, A_TRANSACTION_ID, configureVehicleRequest);

    // then
    verify(transactionService).configureVehicle(A_TRANSACTION_ID, configureVehicleDto, tokenDto);
  }

  @Test
  public void givenAConfigureBatteryRequest_whenConfigureBattery_thenRequestIsValidated() {
    // given
    given(configureBatteryDtoAssembler.assemble(configureBatteryRequest))
        .willReturn(configureBatteryDto);
    given(batteryConfigurationDto.getEstimatedRange()).willReturn(A_RANGE);
    given(transactionService.configureBattery(A_TRANSACTION_ID, configureBatteryDto, tokenDto))
        .willReturn(batteryConfigurationDto);
    given(tokenExtractor.extract(containerRequestContext)).willReturn(tokenDto);

    // when
    transactionResource.configureBattery(
        containerRequestContext, A_TRANSACTION_ID, configureBatteryRequest);

    // then
    verify(requestValidator).validate(configureBatteryRequest);
  }

  @Test public void whenConfigureBattery_thenTokenIsExtracted() {
    // given
    given(configureBatteryDtoAssembler.assemble(configureBatteryRequest))
            .willReturn(configureBatteryDto);
    given(batteryConfigurationDto.getEstimatedRange()).willReturn(A_RANGE);
    given(transactionService.configureBattery(A_TRANSACTION_ID, configureBatteryDto, tokenDto))
            .willReturn(batteryConfigurationDto);
    given(tokenExtractor.extract(containerRequestContext)).willReturn(tokenDto);

    // when
    transactionResource.configureBattery(containerRequestContext, A_TRANSACTION_ID, configureBatteryRequest);

    // then
    verify(tokenExtractor).extract(containerRequestContext);
  }

  @Test
  void whenCompleteTransaction_thenValidatePaymentRequest() {
    // when
    given(tokenExtractor.extract(containerRequestContext)).willReturn(tokenDto);

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
    given(tokenExtractor.extract(containerRequestContext)).willReturn(tokenDto);

    // when
    transactionResource.completeTransaction(
        containerRequestContext, A_TRANSACTION_ID, configurePaymentRequest);

    // then
    verify(transactionService).completeTransaction(A_TRANSACTION_ID, configurePaymentDto, tokenDto);
  }

  @Test public void whenCompleteTransaction_thenTokenIsExtracted() {
    // given
    given(tokenExtractor.extract(containerRequestContext)).willReturn(tokenDto);

    // when
    transactionResource.completeTransaction(containerRequestContext, A_TRANSACTION_ID, configurePaymentRequest);

    // then
    verify(tokenExtractor).extract(containerRequestContext);
  }
}
