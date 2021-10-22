package ca.ulaval.glo4003.ws.domain.assembly.order;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderTest {
  private static final OrderId AN_ID = new OrderId("id");
  private static final LocalDate A_LOCAL_DATE = LocalDate.of(23, 1, 1);
  private static final ProductionTime AN_INITIAL_ASSEMBLY_TIME = new ProductionTime(1);
  private static final ProductionTime A_PRODUCTION_TIME = new ProductionTime(10);

  @Mock private Battery battery;
  @Mock private Model model;
  @Mock private ProductionTime aProductionTime;

  private Order order;

  @BeforeEach
  void setUp() {
    given(battery.getProductionTime()).willReturn(new ProductionTime(1));
    given(model.getProductionTime()).willReturn(new ProductionTime(2));

    order = new Order(AN_ID, model, battery, A_LOCAL_DATE, AN_INITIAL_ASSEMBLY_TIME);
    order.setRemainingAssemblyTime(A_PRODUCTION_TIME);
  }

  @Test
  public void whenAdvance_thenRemainingProductionTimeDecreasedByOne() {
    // given
    ProductionTime expectedProductionTime = A_PRODUCTION_TIME.subtractWeeks(1);

    // when
    order.advance();

    // then
    assertThat(order.getRemainingAssemblyTime()).isEqualTo(expectedProductionTime);
  }

  @Test
  public void givenProductionTimeOver_whenIsOver_thenReturnTrue() {
    // given
    given(aProductionTime.isOver()).willReturn(true);
    order.setRemainingAssemblyTime(aProductionTime);

    // when
    boolean isOrderOver = order.isOver();

    // then
    assertThat(isOrderOver).isTrue();
  }

  @Test
  public void givenProductionTimeOver_whenIsOver_thenReturnFalse() {
    // given
    given(aProductionTime.isOver()).willReturn(false);
    order.setRemainingAssemblyTime(aProductionTime);

    // when
    boolean isOrderOver = order.isOver();

    // then
    assertThat(isOrderOver).isFalse();
  }

  @Test
  public void whenAddDelay_thenDelayIsAdded() {
    // given
    ProductionTime aDelay = new ProductionTime(1);

    // when
    order.addAssemblyDelay(aDelay);

    // then
    assertThat(order.getAssemblyDelay()).isEqualTo(aDelay);
  }

  @Test
  public void
      givenNoDelay_whenComputeDeliveryDate_thenReturnCreatedAtPlusSumOfModelBatteryAndAssemblyProductionTime() {
    // given
    LocalDate createdAt = LocalDate.of(10, 10, 10);
    Order order = new Order(AN_ID, model, battery, createdAt, AN_INITIAL_ASSEMBLY_TIME);
    LocalDate expectedDeliveryDate = LocalDate.of(10, 11, 7);

    // when
    LocalDate actualDeliveryDate = order.computeDeliveryDate();

    // then
    assertThat(actualDeliveryDate).isEquivalentAccordingToCompareTo(expectedDeliveryDate);
  }

  @Test
  public void
      givenTwoWeeksDelay_whenComputeDeliveryDate_thenReturnCreatedAtPlusInitialProductionTimePlusTwoWeeks() {
    // given
    LocalDate createdAt = LocalDate.of(10, 10, 10);
    Order order = new Order(AN_ID, model, battery, createdAt, AN_INITIAL_ASSEMBLY_TIME);
    ProductionTime aDelay = new ProductionTime(2);
    order.addAssemblyDelay(aDelay);
    LocalDate expectedDeliveryDate = LocalDate.of(10, 11, 21);

    // when
    LocalDate deliveryDate = order.computeDeliveryDate();

    // then
    assertThat(deliveryDate).isEquivalentAccordingToCompareTo(expectedDeliveryDate);
  }
}
