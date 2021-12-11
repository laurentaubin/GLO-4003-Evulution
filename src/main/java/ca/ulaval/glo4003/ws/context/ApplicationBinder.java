package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.api.delivery.DeliveryResource;
import ca.ulaval.glo4003.ws.api.delivery.DeliveryResourceImpl;
import ca.ulaval.glo4003.ws.api.manufacturer.ManufacturerResource;
import ca.ulaval.glo4003.ws.api.manufacturer.ManufacturerResourceImpl;
import ca.ulaval.glo4003.ws.api.transaction.TransactionResource;
import ca.ulaval.glo4003.ws.api.transaction.TransactionResourceImpl;
import ca.ulaval.glo4003.ws.api.user.UserResource;
import ca.ulaval.glo4003.ws.api.user.UserResourceImpl;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class ApplicationBinder extends AbstractBinder {
  @Override
  protected void configure() {
    bind(new UserResourceImpl()).to(UserResource.class);
    bind(new TransactionResourceImpl()).to(TransactionResource.class);
    bind(new DeliveryResourceImpl()).to(DeliveryResource.class);
    bind(new ManufacturerResourceImpl()).to(ManufacturerResource.class);
  }
}
