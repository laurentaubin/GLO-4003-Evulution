package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.api.delivery.DeliveryResource;
import ca.ulaval.glo4003.ws.api.transaction.TransactionResource;
import ca.ulaval.glo4003.ws.api.user.UserResource;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class ApplicationBinder extends AbstractBinder {
  public static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  @Override
  protected void configure() {
    bind(serviceLocator.resolve(UserResource.class)).to(UserResource.class);
    bind(serviceLocator.resolve(TransactionResource.class)).to(TransactionResource.class);
    bind(serviceLocator.resolve(DeliveryResource.class)).to(DeliveryResource.class);
  }
}
