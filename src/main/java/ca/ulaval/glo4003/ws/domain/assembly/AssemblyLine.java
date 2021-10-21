package ca.ulaval.glo4003.ws.domain.assembly;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategyFactory;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;

public class AssemblyLine implements TransactionObserver {

  private final AssemblyStrategy assemblyStrategy;
  private final AssemblyStrategyFactory assemblyStrategyFactory;
  private final OrderFactory orderFactory;

  public AssemblyLine(
      AssemblyStrategyFactory assemblyStrategyFactory,
      OrderFactory orderFactory,
      AssemblyStrategy defaultAssemblyStrategy) {
    this.assemblyStrategy = defaultAssemblyStrategy;
    this.orderFactory = orderFactory;
    this.assemblyStrategyFactory = assemblyStrategyFactory;
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
