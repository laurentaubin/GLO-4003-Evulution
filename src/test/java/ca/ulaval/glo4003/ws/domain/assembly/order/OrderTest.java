package ca.ulaval.glo4003.ws.domain.assembly.order;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderTest {
  private static final OrderId AN_ID = new OrderId("id");
  private static final int A_PRODUCTION_TIME = 10;

  @Mock private Battery battery;
  @Mock private Model model;

  private Order order;

  @BeforeEach
  void setUp() {
    order = new Order(AN_ID, model, battery);
    order.setRemainingProductionTime(A_PRODUCTION_TIME);
  }

  @Test
  public void whenAdvance_thenRemainingProductionTimeDecreasedByOne() {
    // when
    order.advance();

    // then
    assertThat(order.getRemainingProductionTime()).isEqualTo(A_PRODUCTION_TIME - 1);
  }
}
