package ca.ulaval.glo4003.ws.domain.manufacturer.vehicle;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.shared.RandomProvider;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleAssemblyPlannerTest {
  private static final VehicleAssemblyProductionTime A_NORMAL_ASSEMBLY_TIME =
      VehicleAssemblyProductionTime.NORMAL;
  private static final VehicleAssemblyProductionTime A_DELAYED_ASSEMBLY_TIME =
      VehicleAssemblyProductionTime.DELAYED;

  @Mock private Order order;
  @Mock private Order delayedOrder;
  @Mock private RandomProvider randomProvider;

  private VehicleAssemblyPlanner vehicleAssemblyPlanner;

  @BeforeEach
  void setUp() {
    vehicleAssemblyPlanner = new VehicleAssemblyPlanner(randomProvider);
  }

  @Test
  public void givenDelayInProduction_whenGetProductionTime_shouldReturnDelayed() {
    // given
    given(randomProvider.nextBoolean()).willReturn(true);

    // when
    var productionTime = vehicleAssemblyPlanner.getAssemblyTime(delayedOrder);

    // then
    assertThat(productionTime).isEqualTo(A_DELAYED_ASSEMBLY_TIME.getAssemblyTime());
  }

  @Test
  public void givenNoDelayInProduction_whenGetProductionTime_shouldReturnNormalProductionTime() {
    // given
    given(randomProvider.nextBoolean()).willReturn(false);

    // when
    var productionTime = vehicleAssemblyPlanner.getAssemblyTime(order);

    // then
    assertThat(productionTime).isEqualTo(A_NORMAL_ASSEMBLY_TIME.getAssemblyTime());
  }

  @Test
  public void givenDelayInProduction_whenGetProductionTime_shouldAddDelayToOrder() {
    // given
    given(randomProvider.nextBoolean()).willReturn(true);
    AssemblyTime expectedDelay =
        A_DELAYED_ASSEMBLY_TIME
            .getAssemblyTime()
            .subtract(A_NORMAL_ASSEMBLY_TIME.getAssemblyTime());

    // when
    vehicleAssemblyPlanner.getAssemblyTime(delayedOrder);

    // then
    verify(delayedOrder).addAssemblyDelay(expectedDelay);
  }

  @Test
  public void givenNoDelayInProduction_whenGetProductionTime_shouldNotAddDelayToOrder() {
    // given
    given(randomProvider.nextBoolean()).willReturn(false);

    // when
    vehicleAssemblyPlanner.getAssemblyTime(delayedOrder);

    // then
    verify(delayedOrder, never()).addAssemblyDelay(any());
  }

  @Test
  public void whenGetNormalAssemblyTime_thenReturnNormalAssemblyTime() {
    // when
    AssemblyTime assemblyTime = vehicleAssemblyPlanner.getNormalAssemblyTime();

    // then
    assertThat(assemblyTime).isEqualTo(VehicleAssemblyProductionTime.NORMAL.getAssemblyTime());
  }
}
