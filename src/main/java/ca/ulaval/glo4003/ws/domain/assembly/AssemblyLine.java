package ca.ulaval.glo4003.ws.domain.assembly;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderFactory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategy;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategyFactory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.AssemblyStrategyType;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;

// TODO remove from exclude and write tests
public class AssemblyLine implements TransactionObserver {

  private final AssemblyStrategyFactory assemblyStrategyFactory;
  private final AssemblyStrategy assemblyStrategy;
  private final OrderFactory orderFactory;

  public AssemblyLine(AssemblyStrategyFactory assemblyStrategyFactory, OrderFactory orderFactory) {
    this.assemblyStrategyFactory = assemblyStrategyFactory;
    this.assemblyStrategy = this.assemblyStrategyFactory.create(AssemblyStrategyType.LINEAR);
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
