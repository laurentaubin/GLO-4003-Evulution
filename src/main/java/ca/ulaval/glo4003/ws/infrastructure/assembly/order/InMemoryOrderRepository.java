package ca.ulaval.glo4003.ws.infrastructure.assembly.order;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderRepository;
import ca.ulaval.glo4003.ws.domain.assembly.order.exception.OrderNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// TODO Use DTO instead of Order
public class InMemoryOrderRepository implements OrderRepository {
  private static Logger logger = Logger.getLogger(InMemoryOrderRepository.class.getName());
  private Map<OrderId, Order> orders = new HashMap<>();

  @Override
  public void save(Order order) {
    orders.put(order.getId(), order);
  }

  @Override
  public void remove(Order order) {
    try {
      if (findById(order.getId()).isEmpty()) {
        throw new OrderNotFoundException(
            String.format("Order with id %s doesnt exist", order.getId()));
      }
      orders.remove(order.getId());
    } catch (OrderNotFoundException e) {
      logger.warning(e.getMessage());
      throw e;
    }
  }

  @Override
  public List<Order> findAllCompletedOrders() {
    return orders.values().stream().filter(Order::isReadyForDelivery).collect(Collectors.toList());
  }

  private Optional<Order> findById(OrderId orderId) {
    return orders.values().stream().filter(order -> orderId.equals(orderId)).findFirst();
  }
}
