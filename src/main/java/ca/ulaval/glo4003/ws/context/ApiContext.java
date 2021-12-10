package ca.ulaval.glo4003.ws.context;

public class ApiContext {

  public void applyContext() {
    new UserContext().registerContext();
    new InventoryContext().registerContext();
    new NotificationContext().registerContext();
    new WarehouseContext().registerContext();
    new DeliveryContext().registerContext();
    new SalesContext().registerContext();
    new ManufacturerContext().registerContext();
    new TimeContext().registerContext();
  }
}
