package ca.ulaval.glo4003.ws.domain.assembly.order;

import ca.ulaval.glo4003.ws.api.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.VehicleAssemblyPlanner;
import ca.ulaval.glo4003.ws.domain.transaction.Transaction;

public class OrderFactory {
  private final LocalDateProvider localDateProvider;
  private final VehicleAssemblyPlanner vehicleAssemblyPlanner;

  public OrderFactory(
      LocalDateProvider localDateProvider, VehicleAssemblyPlanner vehicleAssemblyPlanner) {
    this.localDateProvider = localDateProvider;
    this.vehicleAssemblyPlanner = vehicleAssemblyPlanner;
  }

  public Order create(Transaction transaction) {
    OrderId orderId = new OrderId(transaction.getId().toString());
    return new Order(
        orderId,
        transaction.getVehicle().getModel(),
        transaction.getVehicle().getBattery(),
        localDateProvider.today(),
        vehicleAssemblyPlanner.getNormalAssemblyTime());
  }
}
