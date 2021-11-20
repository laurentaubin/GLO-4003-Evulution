package ca.ulaval.glo4003.ws.infrastructure.assembly.model;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.evulution.car_manufacture.VehicleAssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import ca.ulaval.glo4003.ws.infrastructure.assembly.CommandIdFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModelAssemblyLineAdapterTest {
  private static final String A_MODEL_NAME = "model name";

  @Mock private OrderId anOrderId;
  @Mock private Order anOrder;
  @Mock private Model aModel;
  @Mock private CommandID aCommandId;
  @Mock private VehicleAssemblyLine vehicleAssemblyLine;
  @Mock private CommandIdFactory commandIdFactory;

  private CarManufactureModelAssemblyLineAdapter modelAssemblyLineAdapter;

  @BeforeEach
  public void setUp() {
    modelAssemblyLineAdapter =
        new CarManufactureModelAssemblyLineAdapter(vehicleAssemblyLine, commandIdFactory);
  }

  @Test
  public void givenAnAssembledOrder_whenGetAssemblyStatus_thenReturnTheOrderAssemblyStatus() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(anOrderId)).willReturn(aCommandId);
    given(vehicleAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.ASSEMBLED);

    // when
    AssemblyStatus assemblyStatus = modelAssemblyLineAdapter.getAssemblyStatus(anOrderId);

    // then
    assertThat(assemblyStatus).isEquivalentAccordingToCompareTo(AssemblyStatus.ASSEMBLED);
  }

  @Test
  public void givenAnInProgressOrder_whenGetAssemblyStatus_thenReturnTheOrderAssemblyStatus() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(anOrderId)).willReturn(aCommandId);
    given(vehicleAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.IN_PROGRESS);

    // when
    AssemblyStatus assemblyStatus = modelAssemblyLineAdapter.getAssemblyStatus(anOrderId);

    // then
    assertThat(assemblyStatus).isEquivalentAccordingToCompareTo(AssemblyStatus.IN_PROGRESS);
  }

  @Test
  public void givenAReceivedOrder_whenGetAssemblyStatus_thenReturnTheOrderAssemblyStatus() {
    // given
    given(commandIdFactory.getOrCreateFromOrderId(anOrderId)).willReturn(aCommandId);
    given(vehicleAssemblyLine.getBuildStatus(aCommandId)).willReturn(BuildStatus.RECEIVED);

    // when
    AssemblyStatus assemblyStatus = modelAssemblyLineAdapter.getAssemblyStatus(anOrderId);

    // then
    assertThat(assemblyStatus).isEquivalentAccordingToCompareTo(AssemblyStatus.RECEIVED);
  }

  @Test
  public void givenAnOrder_whenAddOrder_thenOrderIsSentToBeAssembled() {
    // given
    given(anOrder.getId()).willReturn(anOrderId);
    given(commandIdFactory.getOrCreateFromOrderId(anOrderId)).willReturn(aCommandId);
    given(anOrder.getModel()).willReturn(aModel);
    given(aModel.getName()).willReturn(A_MODEL_NAME);

    // when
    modelAssemblyLineAdapter.addOrder(anOrder);

    // then
    verify(vehicleAssemblyLine).newCarCommand(aCommandId, A_MODEL_NAME);
  }

  @Test
  public void whenAdvance_thenBatteryAssemblyLineIsAdvanced() {
    // when
    modelAssemblyLineAdapter.advance();

    // then
    verify(vehicleAssemblyLine).advance();
  }
}
