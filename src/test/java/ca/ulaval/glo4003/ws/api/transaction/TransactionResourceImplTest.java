package ca.ulaval.glo4003.ws.api.transaction;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4003.ws.api.transaction.dto.VehicleRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.VehicleRequestValidator;
import ca.ulaval.glo4003.ws.domain.transaction.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionResourceImplTest {
  private static final TransactionId AN_ID = new TransactionId("id");
  private static final String A_MODEL = "Vandry";
  private static final String A_COLOR = "Color";

  @Mock private TransactionService transactionService;
  @Mock private CreatedTransactionResponseAssembler createdTransactionResponseAssembler;
  @Mock private VehicleRequestAssembler vehicleRequestAssembler;
  @Mock private VehicleRequestValidator vehicleRequestValidator;

  private Transaction transaction;
  private TransactionResource transactionResource;

  @BeforeEach
  void setUp() {
    transaction = createTransactionGivenId(AN_ID);
    transactionResource =
        new TransactionResourceImpl(
            transactionService,
            createdTransactionResponseAssembler,
            vehicleRequestAssembler,
            vehicleRequestValidator);
  }

  @Test
  void givenTransaction_whenCreateTransaction_thenCreateTransactionResponse() {
    // given
    when(transactionService.createTransaction()).thenReturn(transaction);

    // when
    transactionResource.createTransaction();

    // then
    verify(createdTransactionResponseAssembler).create(transaction);
  }

  @Test
  void givenVehicleRequest_whenAddVehicle_thenValidateRequest() {
    // given
    var vehicleRequest = createVehicleRequest();

    // when
    transactionResource.addVehicle(AN_ID.toString(), vehicleRequest);

    // then
    verify(vehicleRequestValidator).validate(vehicleRequest);
  }

  @Test
  void givenVehicleRequest_whenAddVehicle_thenAddVehicle() {
    // given
    var vehicleRequest = createVehicleRequest();
    var vehicle = createVehicle();
    when(vehicleRequestAssembler.create(vehicleRequest)).thenReturn(vehicle);

    // when
    transactionResource.addVehicle(AN_ID.toString(), vehicleRequest);

    // then
    verify(transactionService).addVehicle(AN_ID, vehicle);
  }

  private Transaction createTransactionGivenId(TransactionId id) {
    return new Transaction(id);
  }

  private VehicleRequest createVehicleRequest() {
    var vehicleRequest = new VehicleRequest();
    vehicleRequest.setModel(A_MODEL);
    vehicleRequest.setColor(A_COLOR);

    return vehicleRequest;
  }

  private Vehicle createVehicle() {
    return new Vehicle(Model.fromString(A_MODEL), new Color(A_COLOR));
  }
}
