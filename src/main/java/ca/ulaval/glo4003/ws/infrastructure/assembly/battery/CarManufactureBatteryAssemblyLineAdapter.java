package ca.ulaval.glo4003.ws.infrastructure.assembly.battery;

import ca.ulaval.glo4003.evulution.car_manufacture.BatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.BatteryAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.infrastructure.assembly.CommandIdFactory;

public class CarManufactureBatteryAssemblyLineAdapter implements BatteryAssemblyLineAdapter {
  private final BatteryAssemblyLine batteryAssemblyLine;
  private final CommandIdFactory commandIdFactory;

  public CarManufactureBatteryAssemblyLineAdapter(
      BatteryAssemblyLine batteryAssemblyLine, CommandIdFactory commandIdFactory) {
    this.batteryAssemblyLine = batteryAssemblyLine;
    this.commandIdFactory = commandIdFactory;
  }

  @Override
  public AssemblyStatus getAssemblyStatus(OrderId orderId) {
    try {
      CommandID commandId = commandIdFactory.getOrCreateFromOrderId(orderId);
      BuildStatus buildStatus = batteryAssemblyLine.getBuildStatus(commandId);
      return AssemblyStatus.valueOf(buildStatus.name());
    } catch (NullPointerException exception) {
      return AssemblyStatus.DOES_NOT_EXIST;
    }
  }

  @Override
  public void addOrder(Order order) {
    CommandID commandId = commandIdFactory.getOrCreateFromOrderId(order.getId());
    batteryAssemblyLine.newBatteryCommand(commandId, order.getBattery().getType());
  }

  @Override
  public void advance() {
    batteryAssemblyLine.advance();
  }
}
