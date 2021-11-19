package ca.ulaval.glo4003.ws.domain.assembly;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategy;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AssemblyLine implements TransactionObserver {
  private static final Logger LOGGER = LogManager.getLogger();

  private final AssemblyStrategy assemblyStrategy;
  private final OrderFactory orderFactory;

  private boolean isActivated = true;

  public AssemblyLine(OrderFactory orderFactory, AssemblyStrategy defaultAssemblyStrategy) {
    this.assemblyStrategy = defaultAssemblyStrategy;
    this.orderFactory = orderFactory;
  }

  public void advance() {
    LOGGER.info("Advancing time: +1 week");
    if (isActivated()) assemblyStrategy.advance();
  }

  @Override
  public void listenToTransactionCompleted(Transaction transaction) {
    Order order = orderFactory.create(transaction);
    assemblyStrategy.addOrder(order);
  }

  public void activate() {
    this.isActivated = true;
    this.assemblyStrategy.reactivate();
  }

  public void shutdown() {
    this.isActivated = false;
    this.assemblyStrategy.shutdown();
  }

  public boolean isActivated() {
    return isActivated;
  }

  public List<Order> getActiveOrders() {
    return assemblyStrategy.getActiveOrders();
  }
}
