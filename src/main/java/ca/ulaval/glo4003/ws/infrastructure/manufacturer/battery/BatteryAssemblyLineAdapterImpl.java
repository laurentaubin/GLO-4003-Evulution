package ca.ulaval.glo4003.ws.infrastructure.manufacturer.battery;

import ca.ulaval.glo4003.evulution.car_manufacture.BatteryAssemblyLine;
import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.ws.domain.manufacturer.battery.BatteryAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.warehouse.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.infrastructure.warehouse.CommandIdFactory;

public class BatteryAssemblyLineAdapterImpl implements BatteryAssemblyLineAdapter {
  private final BatteryAssemblyLine batteryAssemblyLine;
  private final CommandIdFactory commandIdFactory;

  public BatteryAssemblyLineAdapterImpl(
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
  public void addOrder(BatteryOrder batteryOrder) {
    CommandID commandID = commandIdFactory.getOrCreateFromOrderId(batteryOrder.getOrderId());
    batteryAssemblyLine.newBatteryCommand(commandID, batteryOrder.getBatteryType());
  }

  @Override
  public void advance() {
    batteryAssemblyLine.advance();
  }
}
