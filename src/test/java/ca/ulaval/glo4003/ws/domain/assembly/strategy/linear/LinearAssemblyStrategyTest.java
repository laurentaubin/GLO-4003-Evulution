package ca.ulaval.glo4003.ws.domain.assembly.strategy.linear;

import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.ModelAssemblyLineStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LinearAssemblyStrategyTest {

  @Mock ModelAssemblyLineStrategy modelAssemblyLineStrategy;
  @Mock BatteryAssemblyLineStrategy batteryAssemblyLineStrategy;
  @Mock Order anOrder;
  @Mock Model aModel;
  @Mock Battery aBattery;

  private LinearAssemblyStrategy linearAssemblyStrategy;

  @BeforeEach
  public void setUp() {
    linearAssemblyStrategy =
        new LinearAssemblyStrategy(modelAssemblyLineStrategy, batteryAssemblyLineStrategy);
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
    verify(batteryAssemblyLineStrategy).assembleBattery(anOrder);
  }
}
