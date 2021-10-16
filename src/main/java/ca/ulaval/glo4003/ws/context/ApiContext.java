package ca.ulaval.glo4003.ws.context;

public class ApiContext {

  public void applyContext() {
    new UserContext().registerContext();
    new InventoryContext().registerContext();
    new AssemblyContext().registerContext();
    new DeliveryContext().registerContext();
    new SalesContext().registerContext();
    new NotificationContext().registerContext();
  }
}
