package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.domain.notification.NotificationService;
import ca.ulaval.glo4003.ws.domain.production.ProductionLineService;
import ca.ulaval.glo4003.ws.service.AssemblyLineService;

public class ProductionLineContext implements Context {

  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  @Override
  public void registerContext() {
    registerServices();
    registerObservers();
  }

  private void registerServices() {
    serviceLocator.register(
        ProductionLineService.class,
        new ProductionLineService(serviceLocator.resolve(AssemblyLineService.class)));
  }

  private void registerObservers() {
    var productionLineService = serviceLocator.resolve(ProductionLineService.class);
    productionLineService.register(serviceLocator.resolve(NotificationService.class));
  }
}
