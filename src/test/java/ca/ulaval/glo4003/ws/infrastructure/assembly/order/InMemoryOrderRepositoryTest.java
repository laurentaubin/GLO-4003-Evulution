package ca.ulaval.glo4003.ws.infrastructure.assembly.order;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.exception.OrderNotFoundException;
import ca.ulaval.glo4003.ws.testUtil.OrderBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class InMemoryOrderRepositoryTest {
  private InMemoryOrderRepository inMemoryOrderRepository;
  private Order completedOrder;

  @BeforeEach
  void setUp() {
    completedOrder = createCompletedOrder();
    this.inMemoryOrderRepository = new InMemoryOrderRepository();
  }

  @Test
  void whenSave_thenOrderIsPersisted() {
    // when
    inMemoryOrderRepository.save(completedOrder);

    // then
    List<Order> expected = List.of(completedOrder);
    assertThat(inMemoryOrderRepository.findAllCompletedOrders()).isEqualTo(expected);
  }

  @Test
  void givenExistingOrder_whenRemove_thenOrderIsRemoved() {
    // given
    inMemoryOrderRepository.save(completedOrder);

    // when
    inMemoryOrderRepository.remove(completedOrder);

    // then
    assertThat(inMemoryOrderRepository.findAllCompletedOrders()).isEqualTo(new ArrayList<>());
  }

  @Test
  void givenNonExistingOrder_whenRemove_thenOrderNotFoundExceptionIsThrown() {

    // when/then
    assertThrows(
        OrderNotFoundException.class, () -> inMemoryOrderRepository.remove(completedOrder));
  }

  @Test
  void whenFindAllCompletedOrders_thenReturnAllCompletedOrders() {
    // given
    Order order = createCompletedOrder();
    inMemoryOrderRepository.save(order);

    // when
    List<Order> completedOrders = inMemoryOrderRepository.findAllCompletedOrders();
    List<Order> expected = List.of(order);

    // then
    assertThat(completedOrders).isEqualTo(expected);
  }

  private Order createCompletedOrder() {
    Order order = new OrderBuilder().build();
    order.setIsReadyForDelivery(true);
    return order;
  }
}
