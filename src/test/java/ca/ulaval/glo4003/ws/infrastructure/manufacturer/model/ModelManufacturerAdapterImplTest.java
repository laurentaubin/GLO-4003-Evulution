package ca.ulaval.glo4003.ws.infrastructure.manufacturer.model;

import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.evulution.car_manufacture.VehicleAssemblyLine;
import ca.ulaval.glo4003.ws.domain.warehouse.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.fixture.ModelOrderBuilder;
import ca.ulaval.glo4003.ws.infrastructure.warehouse.CommandIdFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModelManufacturerAdapterImplTest {
  private final BuildStatus ASSEMBLED_BUILD_STATUS = BuildStatus.ASSEMBLED;
  private final BuildStatus AN_INVALID_BUILD_STATUS = null;

  @Mock private CommandID commandId;
  @Mock private OrderId orderId;
  @Mock private VehicleAssemblyLine vehicleAssemblyLine;
  @Mock private CommandIdFactory commandIdFactory;

  private ModelAssemblyLineAdapterImpl modelAssemblyLineAdapter;

  @BeforeEach
  void setUp() {
    modelAssemblyLineAdapter =
        new ModelAssemblyLineAdapterImpl(vehicleAssemblyLine, commandIdFactory);
  }

  @Test
  void whenGetAssemblyStatus_thenVehicleAssemblyLineGetsStatus() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(orderId)).willReturn(commandId);

    // when
    modelAssemblyLineAdapter.getAssemblyStatus(orderId);

    // then
    verify(vehicleAssemblyLine).getBuildStatus(any(CommandID.class));
  }

  @Test
  void givenAnInvalidStatus_whenGetAssemblyStatus_thenReturnNonExistingStatus() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(orderId)).willReturn(commandId);
    given(vehicleAssemblyLine.getBuildStatus(commandId)).willReturn(AN_INVALID_BUILD_STATUS);

    // when
    AssemblyStatus assemblyStatus = modelAssemblyLineAdapter.getAssemblyStatus(orderId);

    // then
    assertThat(assemblyStatus).isEquivalentAccordingToCompareTo(AssemblyStatus.DOES_NOT_EXIST);
  }

  @Test
  void givenAValidStatus_whenGetAssemblyStatus_thenReturnExpectedAssemblyStatus() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(orderId)).willReturn(commandId);
    given(vehicleAssemblyLine.getBuildStatus(commandId)).willReturn(ASSEMBLED_BUILD_STATUS);

    // when
    AssemblyStatus assemblyStatus = modelAssemblyLineAdapter.getAssemblyStatus(orderId);

    // then
    assertThat(assemblyStatus).isEquivalentAccordingToCompareTo(AssemblyStatus.ASSEMBLED);
  }

  @Test
  void whenAddModelOrder_thenCallVehicleAssemblyLineForNewCarCommand() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(orderId)).willReturn(commandId);
    ModelOrder modelOrder = createAModelOrder();

    // when
    modelAssemblyLineAdapter.addOrder(modelOrder);

    // then
    verify(vehicleAssemblyLine).newCarCommand(any(CommandID.class), any(String.class));
  }

  @Test
  void whenAdvance_thenVehicleAssemblyLineAdvances() {
    // when
    modelAssemblyLineAdapter.advance();

    // then
    verify(vehicleAssemblyLine).advance();
  }

  private ModelOrder createAModelOrder() {
    return new ModelOrderBuilder().withOrderId(orderId).build();
  }
}
