package ca.ulaval.glo4003.ws.infrastructure.assembly.model;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.evulution.car_manufacture.VehicleAssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.accumulate.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import ca.ulaval.glo4003.ws.infrastructure.assembly.CommandIdFactory;
import ca.ulaval.glo4003.ws.testUtil.BatteryBuilder;
import ca.ulaval.glo4003.ws.testUtil.ModelBuilder;
import ca.ulaval.glo4003.ws.testUtil.ModelOrderBuilder;
import ca.ulaval.glo4003.ws.testUtil.OrderBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarManufactureModelAssemblyLineAdapterTest {
  private final BuildStatus ASSEMBLED_BUILD_STATUS = BuildStatus.ASSEMBLED;
  private final BuildStatus AN_INVALID_BUILD_STATUS = null;

  @Mock private CommandID commandId;
  @Mock private OrderId orderId;
  @Mock private VehicleAssemblyLine vehicleAssemblyLine;
  @Mock private CommandIdFactory commandIdFactory;

  private CarManufactureModelAssemblyLineAdapter carManufactureModelAssemblyLineAdapter;

  @BeforeEach
  void setUp() {
    carManufactureModelAssemblyLineAdapter =
        new CarManufactureModelAssemblyLineAdapter(vehicleAssemblyLine, commandIdFactory);
  }

  @Test
  void whenGetAssemblyStatus_thenVehicleAssemblyLineGetsStatus() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(orderId)).willReturn(commandId);

    // when
    carManufactureModelAssemblyLineAdapter.getAssemblyStatus(orderId);

    // then
    verify(vehicleAssemblyLine).getBuildStatus(any(CommandID.class));
  }

  @Test
  void givenAnInvalidStatus_whenGetAssemblyStatus_thenReturnNonExistingStatus() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(orderId)).willReturn(commandId);
    given(vehicleAssemblyLine.getBuildStatus(commandId)).willReturn(AN_INVALID_BUILD_STATUS);

    // when
    AssemblyStatus assemblyStatus =
        carManufactureModelAssemblyLineAdapter.getAssemblyStatus(orderId);

    // then
    assertThat(assemblyStatus).isEquivalentAccordingToCompareTo(AssemblyStatus.DOES_NOT_EXIST);
  }

  @Test
  void givenAValidStatus_whenGetAssemblyStatus_thenReturnExpectedAssemblyStatus() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(orderId)).willReturn(commandId);
    given(vehicleAssemblyLine.getBuildStatus(commandId)).willReturn(ASSEMBLED_BUILD_STATUS);

    // when
    AssemblyStatus assemblyStatus =
        carManufactureModelAssemblyLineAdapter.getAssemblyStatus(orderId);

    // then
    assertThat(assemblyStatus).isEquivalentAccordingToCompareTo(AssemblyStatus.ASSEMBLED);
  }

  @Test
  void givenAnOrder_whenAddOrder_thenCallVehicleAssemblyLineForNewCarCommand() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(orderId)).willReturn(commandId);
    Order anOrder = createAnOrder();

    // when
    carManufactureModelAssemblyLineAdapter.addOrder(anOrder);

    // then
    verify(vehicleAssemblyLine).newCarCommand(any(CommandID.class), any(String.class));
  }

  @Test
  void whenAddModelOrder_thenCallVehicleAssemblyLineForNewCarCommand() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(orderId)).willReturn(commandId);
    ModelOrder modelOrder = createAModelOrder();

    // when
    carManufactureModelAssemblyLineAdapter.addOrder(modelOrder);

    // then
    verify(vehicleAssemblyLine).newCarCommand(any(CommandID.class), any(String.class));
  }

  @Test
  void whenAdvance_thenVehicleAssemblyLineAdvances() {
    // when
    carManufactureModelAssemblyLineAdapter.advance();

    // then
    verify(vehicleAssemblyLine).advance();
  }

  private Order createAnOrder() {
    Model model = new ModelBuilder().build();
    Battery battery = new BatteryBuilder().build();
    return new OrderBuilder().withOrderId(orderId).withModel(model).withBattery(battery).build();
  }

  private ModelOrder createAModelOrder() {
    return new ModelOrderBuilder().withOrderId(orderId).build();
  }
}
