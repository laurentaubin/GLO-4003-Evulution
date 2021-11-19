package ca.ulaval.glo4003.ws.domain.production;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;

import java.util.List;

public class ProductionLineService extends ProductionLineShutdownObservable {
    private final AssemblyLine assemblyLine;

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
