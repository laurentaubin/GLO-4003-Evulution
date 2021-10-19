package ca.ulaval.glo4003.ws.domain.assembly.strategy.linear;

import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.VehicleAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LinearAssemblyStrategyTest {
  @Mock private ModelAssemblyLineStrategy modelAssemblyLineStrategy;
  @Mock private BatteryAssemblyLineStrategy batteryAssemblyLineStrategy;
  @Mock private VehicleAssemblyLineStrategy vehicleAssemblyLineStrategy;
  @Mock private Order anOrder;

  private LinearAssemblyStrategy linearAssemblyStrategy;

  @BeforeEach
  public void setUp() {
    linearAssemblyStrategy =
        new LinearAssemblyStrategy(
            modelAssemblyLineStrategy, batteryAssemblyLineStrategy, vehicleAssemblyLineStrategy);
  }

  @Test
  public void givenAnOrder_whenAddOrder_thenOrderIsSentToTheModelAssemblyLine() {
    // when
    linearAssemblyStrategy.addOrder(anOrder);

    // then
    verify(modelAssemblyLineStrategy).addOrder(anOrder);
  }

  @Test
  public void whenListenToModelAssembled_thenOrderedBatteryIsSentToBeAssembled() {
    // when
    linearAssemblyStrategy.listenToModelAssembled(anOrder);

    // then
    verify(batteryAssemblyLineStrategy).addOrder(anOrder);
  }

  @Test
  public void whenAdvance_thenAdvanceAllAssemblyLines() {
    // when
    linearAssemblyStrategy.advance();

    // then
    verify(modelAssemblyLineStrategy).advance();
    verify(batteryAssemblyLineStrategy).advance();
    verify(vehicleAssemblyLineStrategy).advance();
  }

  @Test
  public void givenOrder_whenListenToModelAssembled_thenAssembleBatteryForOrder() {
    // when
    linearAssemblyStrategy.listenToModelAssembled(anOrder);

    // then
    verify(batteryAssemblyLineStrategy).addOrder(anOrder);
  }

  @Test
  public void givenOrder_whenListenToBatteryAssembled_thenAssembleVehicleForOrder() {
    // when
    linearAssemblyStrategy.listenToBatteryAssembled(anOrder);

    // then
    verify(vehicleAssemblyLineStrategy).assembleVehicle(anOrder);
  }
}
