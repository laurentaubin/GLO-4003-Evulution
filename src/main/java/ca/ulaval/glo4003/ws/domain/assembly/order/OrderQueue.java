package ca.ulaval.glo4003.ws.domain.assembly.order;

import java.util.LinkedList;
import java.util.Queue;

public class OrderQueue {
  private Queue<Order> queue = new LinkedList<>();

  public void addOrder(Order order) {
    queue.add(order);
  }

  public boolean isEmpty() {
    return queue.isEmpty();
  }

  public Order getNextInLine() {
    return queue.remove();
  }
}
