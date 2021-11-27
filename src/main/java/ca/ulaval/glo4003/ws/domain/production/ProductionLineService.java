package ca.ulaval.glo4003.ws.domain.production;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;

import java.util.List;

public class ProductionLineService extends ProductionLineShutdownObservable {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final AssemblyLine assemblyLine;

  public ProductionLineService() {
    this(serviceLocator.resolve(AssemblyLine.class));
  }

  public ProductionLineService(AssemblyLine assemblyLine) {
    this.assemblyLine = assemblyLine;
  }

  public void shutdown() {
    List<Order> orders = assemblyLine.getActiveOrders();
    assemblyLine.shutdown();
    notifyProductionShutdown(orders);
  }

  public void reactivate() {
    assemblyLine.activate();
  }
}
