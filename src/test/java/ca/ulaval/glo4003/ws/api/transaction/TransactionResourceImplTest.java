package ca.ulaval.glo4003.ws.api.transaction;

import static org.mockito.Mockito.*;

import ca.ulaval.glo4003.ws.api.transaction.dto.BatteryRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.VehicleRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.BatteryRequestValidator;
import ca.ulaval.glo4003.ws.api.transaction.dto.validators.VehicleRequestValidator;
import ca.ulaval.glo4003.ws.api.validator.RoleValidator;
import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.domain.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.transaction.*;
import ca.ulaval.glo4003.ws.domain.transaction.Color;
import ca.ulaval.glo4003.ws.domain.transaction.Model;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionService;
import ca.ulaval.glo4003.ws.domain.transaction.Vehicle;
import ca.ulaval.glo4003.ws.domain.user.Role;
import jakarta.ws.rs.container.ContainerRequestContext;
import java.util.ArrayList;
import java.util.List;
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

  private static final String A_BATTERY_TYPE = "AType";
  @Mock private Battery A_BATTERY;

  private final TransactionService transactionService =
      mock(TransactionService.class, RETURNS_DEEP_STUBS);
  @Mock private CreatedTransactionResponseAssembler createdTransactionResponseAssembler;
  @Mock private VehicleRequestAssembler vehicleRequestAssembler;
  @Mock private VehicleRequestValidator vehicleRequestValidator;
  @Mock private RoleValidator roleValidator;
  @Mock private ContainerRequestContext containerRequestContext;
  @Mock private BatteryRequestValidator batteryRequestValidator;
  @Mock private BatteryRepository batteryRepository;

  private Transaction transaction;
  private TransactionResource transactionResource;

  @BeforeEach
  void setUp() {
    transaction = createTransaction(AN_ID);
    transactionResource =
        new TransactionResourceImpl(
            transactionService,
            createdTransactionResponseAssembler,
            vehicleRequestAssembler,
            vehicleRequestValidator,
            roleValidator,
            batteryRequestValidator);
  }

  @Test
  void givenTransaction_whenCreateTransaction_thenCreateTransactionResponse() {
    // given
    when(transactionService.createTransaction()).thenReturn(transaction);

    // when
    transactionResource.createTransaction(containerRequestContext);

    // then
    verify(createdTransactionResponseAssembler).create(transaction);
  }

  @Test
  void whenCreateTransaction_thenRolesAreValidated() {
    // given
    var vehicleRequest = createVehicleRequest();

    // when
    transactionResource.addVehicle(containerRequestContext, AN_ID.toString(), vehicleRequest);

    // then
    verify(roleValidator)
        .validate(containerRequestContext, new ArrayList<>(List.of(Role.BASE, Role.ADMIN)));
  }

  @Test
  void whenVehicleRequest_thenRolesAreValidated() {
    // given
    var vehicleRequest = createVehicleRequest();

    // when
    transactionResource.addVehicle(containerRequestContext, AN_ID.toString(), vehicleRequest);

    // then
    verify(roleValidator)
        .validate(containerRequestContext, new ArrayList<>(List.of(Role.BASE, Role.ADMIN)));
  }

  @Test
  void givenVehicleRequest_whenAddVehicle_thenValidateRequest() {
    // given
    var vehicleRequest = createVehicleRequest();

    // when
    transactionResource.addVehicle(containerRequestContext, AN_ID.toString(), vehicleRequest);

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
    transactionResource.addVehicle(containerRequestContext, AN_ID.toString(), vehicleRequest);

    // then
    verify(transactionService).addVehicle(AN_ID, vehicle);
  }

  @Test
  void givenBatteryRequest_whenAddBattery_thenBatteryIsAdded() {
    // given
    BatteryRequest batteryRequest = createBatteryRequest();
    // when
    transactionResource.addBattery(AN_ID.toString(), batteryRequest);

    // then
    verify(transactionService).addBattery(AN_ID, A_BATTERY.getType());
  }

  private Transaction createTransaction(TransactionId id) {
    return new Transaction(id);
  }

  private VehicleRequest createVehicleRequest() {
    var vehicleRequest = new VehicleRequest();
    vehicleRequest.setModel(A_MODEL);
    vehicleRequest.setColor(A_COLOR);

    return vehicleRequest;
  }

  private BatteryRequest createBatteryRequest() {
    when(A_BATTERY.getType()).thenReturn(A_BATTERY_TYPE);
    BatteryRequest batteryRequest = new BatteryRequest();
    batteryRequest.setType(A_BATTERY.getType());

    return batteryRequest;
  }

  private Vehicle createVehicle() {
    return new Vehicle(Model.fromString(A_MODEL), new Color(A_COLOR));
  }
}
