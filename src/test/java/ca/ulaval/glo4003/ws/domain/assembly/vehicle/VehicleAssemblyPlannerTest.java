package ca.ulaval.glo4003.ws.domain.assembly.vehicle;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.notification.VehicleAssemblyDelayObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleAssemblyPlannerTest {
  private static final VehicleAssemblyProductionTime NORMAL = VehicleAssemblyProductionTime.NORMAL;
  private static final VehicleAssemblyProductionTime DELAYED =
      VehicleAssemblyProductionTime.DELAYED;

  @Mock private VehicleAssemblyDelayObserver observer;
  @Mock private Order order;
  @Mock private Order delayedOrder;
  @Mock private Random random;

  private VehicleAssemblyPlanner vehicleAssemblyPlanner;

  @BeforeEach
  void setUp() {
    vehicleAssemblyPlanner = new VehicleAssemblyPlanner(random);
    vehicleAssemblyPlanner.register(observer);
  }

  @Test
  public void givenDelayInProduction_whenGetProductionTime_shouldReturnDelayed() {
    // given
    when(random.nextBoolean()).thenReturn(true);

    // when
    var productionTime = vehicleAssemblyPlanner.getProductionTime(delayedOrder);

    // then
    assertThat(productionTime).isEqualTo(DELAYED.getProductionTime());
  }

  @Test
  public void givenDelayInProduction_whenGetProductionTime_shouldListenToDelay() {
    // given
    when(random.nextBoolean()).thenReturn(true);

    // when
    vehicleAssemblyPlanner.getProductionTime(delayedOrder);

    // then
    verify(observer).listenVehicleAssemblyDelay(delayedOrder);
  }

  @Test
  public void givenNoDelayInProduction_whenGetProductionTime_shouldReturnNormalProductionTime() {
    // given
    when(random.nextBoolean()).thenReturn(false);

    // when
    var productionTime = vehicleAssemblyPlanner.getProductionTime(order);

    // then
    assertThat(productionTime).isEqualTo(NORMAL.getProductionTime());
  }
}
