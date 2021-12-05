package ca.ulaval.glo4003.ws.domain.assembly.order;

import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.assembly.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.assembly.vehicle.VehicleAssemblyPlanner;

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
