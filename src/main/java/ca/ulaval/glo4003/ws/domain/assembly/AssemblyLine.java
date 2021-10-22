package ca.ulaval.glo4003.ws.domain.assembly;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategy;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;

public class AssemblyLine implements TransactionObserver {

  private final AssemblyStrategy assemblyStrategy;
  private final OrderFactory orderFactory;

  public AssemblyLine(OrderFactory orderFactory, AssemblyStrategy defaultAssemblyStrategy) {
    this.assemblyStrategy = defaultAssemblyStrategy;
    this.orderFactory = orderFactory;
  }

  public void advance() {
    assemblyStrategy.advance();
  }

  @Override
  public void listenToTransactionCompleted(Transaction transaction) {
    Order order = orderFactory.create(transaction);
    assemblyStrategy.addOrder(order);
  }
}
