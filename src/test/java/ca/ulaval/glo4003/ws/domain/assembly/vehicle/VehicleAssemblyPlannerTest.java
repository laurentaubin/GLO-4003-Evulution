package ca.ulaval.glo4003.ws.domain.assembly.vehicle;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.notification.VehicleAssemblyDelayObserver;
import ca.ulaval.glo4003.ws.domain.shared.RandomProvider;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleAssemblyPlannerTest {
  private static final VehicleAssemblyProductionTime NORMAL = VehicleAssemblyProductionTime.NORMAL;
  private static final VehicleAssemblyProductionTime DELAYED =
      VehicleAssemblyProductionTime.DELAYED;

  @Mock private VehicleAssemblyDelayObserver observer;
  @Mock private Order order;
  @Mock private Order delayedOrder;
  @Mock private RandomProvider randomProvider;

  private VehicleAssemblyPlanner vehicleAssemblyPlanner;

  @BeforeEach
  void setUp() {
    vehicleAssemblyPlanner = new VehicleAssemblyPlanner(randomProvider);
    vehicleAssemblyPlanner.register(observer);
  }

  @Test
  public void givenDelayInProduction_whenGetProductionTime_shouldReturnDelayed() {
    // given
    given(randomProvider.nextBoolean()).willReturn(true);

    // when
    var productionTime = vehicleAssemblyPlanner.getProductionTime(delayedOrder);

    // then
    assertThat(productionTime).isEqualTo(DELAYED.getProductionTime());
  }

  @Test
  public void givenDelayInProduction_whenGetProductionTime_shouldListenToDelay() {
    // given
    given(randomProvider.nextBoolean()).willReturn(true);

    // when
    vehicleAssemblyPlanner.getProductionTime(delayedOrder);

    // then
    verify(observer).listenVehicleAssemblyDelay(delayedOrder);
  }

  @Test
  public void givenNoDelayInProduction_whenGetProductionTime_shouldReturnNormalProductionTime() {
    // given
    given(randomProvider.nextBoolean()).willReturn(false);

    // when
    var productionTime = vehicleAssemblyPlanner.getProductionTime(order);

    // then
    assertThat(productionTime).isEqualTo(NORMAL.getProductionTime());
  }

  @Test
  public void givenDelayInProduction_whenGetProductionTime_shouldAddDelayToOrder() {
    // given
    given(randomProvider.nextBoolean()).willReturn(true);
    ProductionTime expectedDelay = DELAYED.getProductionTime().subtract(NORMAL.getProductionTime());

    // when
    vehicleAssemblyPlanner.getProductionTime(delayedOrder);

    // then
    verify(delayedOrder).addAssemblyDelay(expectedDelay);
  }

  @Test
  public void givenNoDelayInProduction_whenGetProductionTime_shouldNotAddDelayToOrder() {
    // given
    given(randomProvider.nextBoolean()).willReturn(false);

    // when
    vehicleAssemblyPlanner.getProductionTime(delayedOrder);

    // then
    verify(delayedOrder, never()).addAssemblyDelay(any());
  }

  @Test
  public void whenGetNormalAssemblyTime_thenReturnNormalAssemblyTime() {
    // when
    ProductionTime actualProductionTime = vehicleAssemblyPlanner.getNormalAssemblyTime();

    // then
    assertThat(actualProductionTime)
        .isEqualTo(VehicleAssemblyProductionTime.NORMAL.getProductionTime());
  }
}
