package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.api.production.ProductionLineResource;
import ca.ulaval.glo4003.ws.api.production.ProductionLineResourceImpl;
import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import ca.ulaval.glo4003.ws.domain.notification.NotificationService;
import ca.ulaval.glo4003.ws.domain.production.ProductionLineService;

public class ProductionLineContext implements Context {
    public static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

    @Override
    public void registerContext() {
        registerServices();
        registerResources();
        registerObservers();
    }

    private void registerServices() {
        serviceLocator.register(ProductionLineService.class, new ProductionLineService(
                serviceLocator.resolve(AssemblyLine.class)
        ));
    }

    private void registerResources() {
        serviceLocator.register(ProductionLineResource.class, new ProductionLineResourceImpl(
                serviceLocator.resolve(ProductionLineService.class),
                serviceLocator.resolve(RoleHandler.class)
        ));
    }

    private void registerObservers() {
        var productionLineService = serviceLocator.resolve(ProductionLineService.class);
        productionLineService.register(serviceLocator.resolve(NotificationService.class));
    }
}
