package ca.ulaval.glo4003.ws.domain.assembly.order;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderQueueTest {

  @Mock private Order anOrder;
  @Mock private Order anotherOrder;

  private OrderQueue orderQueue;

  @BeforeEach
  public void setUp() {
    orderQueue = new OrderQueue();
  }

  @Test
  public void givenAnOrderInQueue_whenGetNextInLine_thenReturnTheQueuedOrder() {
    // given
    orderQueue.addOrder(anOrder);

    // when
    Order order = orderQueue.getNextInLine();

    // then
    assertThat(order).isEqualTo(anOrder);
  }

  @Test
  public void givenTwoOrders_whenGetNextInLine_thenReturnTheOrderInTheAddedOrder() {
    // given
    orderQueue.addOrder(anOrder);
    orderQueue.addOrder(anotherOrder);

    // when
    Order firstOrder = orderQueue.getNextInLine();
    Order secondOrder = orderQueue.getNextInLine();

    // then
    assertThat(firstOrder).isEqualTo(anOrder);
    assertThat(secondOrder).isEqualTo(anotherOrder);
  }

  @Test
  public void givenAnEmptyQueue_whenIsEmpty_thenReturnTrue() {
    // when
    boolean isEmpty = orderQueue.isEmpty();

    // then
    assertThat(isEmpty).isTrue();
  }

  @Test
  public void givenAnOrderInQueue_whenIsEmpty_thenReturnFalse() {
    // then
    orderQueue.addOrder(anOrder);

    // when
    boolean isEmpty = orderQueue.isEmpty();

    // then
    assertThat(isEmpty).isFalse();
  }
}
