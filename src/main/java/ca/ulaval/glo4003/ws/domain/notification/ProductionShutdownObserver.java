package ca.ulaval.glo4003.ws.domain.notification;

import ca.ulaval.glo4003.ws.domain.assembly.order.Order;

import java.util.List;

public interface ProductionShutdownObserver {
    void listenProductionLineShutdown(List<Order> orders);
}
