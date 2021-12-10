package ca.ulaval.glo4003.ws.infrastructure.warehouse.order;

import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderRepository;
import ca.ulaval.glo4003.ws.domain.warehouse.order.exception.OrderNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// TODO Use DTO instead of Order
public class InMemoryOrderRepository implements OrderRepository {
  private static final Logger logger = Logger.getLogger(InMemoryOrderRepository.class.getName());
  private final Map<OrderId, Order> orders = new HashMap<>();

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
    return orders.values().stream().filter(order -> order.getId().equals(orderId)).findFirst();
  }
}
