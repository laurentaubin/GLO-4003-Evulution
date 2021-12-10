package ca.ulaval.glo4003.ws.domain.warehouse.order;

import ca.ulaval.glo4003.ws.domain.manufacturer.vehicle.VehicleAssemblyPlanner;
import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;

public class OrderFactory {
  private final LocalDateProvider localDateProvider;
  private final VehicleAssemblyPlanner vehicleAssemblyPlanner;

  public OrderFactory(
      LocalDateProvider localDateProvider, VehicleAssemblyPlanner vehicleAssemblyPlanner) {
    this.localDateProvider = localDateProvider;
    this.vehicleAssemblyPlanner = vehicleAssemblyPlanner;
  }

  public Order create(String id, ModelOrder modelOrder, BatteryOrder batteryOrder) {
    OrderId orderId = new OrderId(id);
    return new Order(
        orderId,
        modelOrder,
        batteryOrder,
        localDateProvider.today(),
        vehicleAssemblyPlanner.getNormalAssemblyTime());
  }
}
