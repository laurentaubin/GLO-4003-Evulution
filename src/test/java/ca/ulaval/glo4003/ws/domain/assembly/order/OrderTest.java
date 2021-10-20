package ca.ulaval.glo4003.ws.domain.assembly.order;

import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderTest {
  private static final OrderId AN_ID = new OrderId("id");
  private static final ProductionTime A_PRODUCTION_TIME = new ProductionTime(10);

  @Mock private Battery battery;
  @Mock private Model model;
  @Mock private ProductionTime aProductionTime;

  private Order order;

  @BeforeEach
  void setUp() {
    order = new Order(AN_ID, model, battery);
    order.setRemainingProductionTime(A_PRODUCTION_TIME);
  }

  @Test
  public void whenAdvance_thenRemainingProductionTimeDecreasedByOne() {
    // given
    ProductionTime expectedProductionTime = A_PRODUCTION_TIME.subtractWeeks(1);

    // when
    order.advance();

    // then
    assertThat(order.getRemainingProductionTime()).isEqualTo(expectedProductionTime);
  }

  @Test
  public void givenProductionTimeOver_whenIsOver_thenReturnTrue() {
    // given
    given(aProductionTime.isOver()).willReturn(true);
    order.setRemainingProductionTime(aProductionTime);

    // when
    boolean isOrderOver = order.isOver();

    // then
    assertThat(isOrderOver).isTrue();
  }

  @Test
  public void givenProductionTimeOver_whenIsOver_thenReturnFalse() {
    // given
    given(aProductionTime.isOver()).willReturn(false);
    order.setRemainingProductionTime(aProductionTime);

    // when
    boolean isOrderOver = order.isOver();

    // then
    assertThat(isOrderOver).isFalse();
  }
}
