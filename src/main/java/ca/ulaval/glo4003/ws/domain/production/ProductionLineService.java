package ca.ulaval.glo4003.ws.domain.production;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.service.AssemblyLineService;
import java.util.List;

public class ProductionLineService extends ProductionLineShutdownObservable {
  private final AssemblyLineService assemblyLine;

  public ProductionLineService(AssemblyLineService assemblyLine) {
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
