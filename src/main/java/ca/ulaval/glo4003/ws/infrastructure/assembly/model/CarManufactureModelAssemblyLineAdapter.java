package ca.ulaval.glo4003.ws.infrastructure.assembly.model;

import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.evulution.car_manufacture.VehicleAssemblyLine;
import ca.ulaval.glo4003.ws.domain.assembly.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.assembly.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.assembly.order.Order;
import ca.ulaval.glo4003.ws.domain.assembly.order.OrderId;
import ca.ulaval.glo4003.ws.infrastructure.assembly.CommandIdFactory;

public class CarManufactureModelAssemblyLineAdapter implements ModelAssemblyLineAdapter {
  private final VehicleAssemblyLine vehicleAssemblyLine;
  private final CommandIdFactory commandIdFactory;

  public CarManufactureModelAssemblyLineAdapter(
      VehicleAssemblyLine vehicleAssemblyLine, CommandIdFactory commandIdFactory) {
    this.vehicleAssemblyLine = vehicleAssemblyLine;
    this.commandIdFactory = commandIdFactory;
  }

  @Override
  public AssemblyStatus getAssemblyStatus(OrderId orderId) {
    CommandID commandId = commandIdFactory.getOrCreateFromOrderId(orderId);
    try {
      BuildStatus buildStatus = vehicleAssemblyLine.getBuildStatus(commandId);
      return AssemblyStatus.valueOf(buildStatus.name());
    } catch (NullPointerException exception) {
      return AssemblyStatus.DOES_NOT_EXIST;
    }
  }

  @Override
  public void addOrder(Order order) {
    CommandID commandId = commandIdFactory.getOrCreateFromOrderId(order.getId());
    vehicleAssemblyLine.newCarCommand(commandId, order.getModelOrder().getModelType());
  }

  @Override
  public void addOrder(ModelOrder modelOrder) {
    CommandID commandID = commandIdFactory.getOrCreateFromOrderId(modelOrder.getOrderId());
    vehicleAssemblyLine.newCarCommand(commandID, modelOrder.getModelType());
  }

  @Override
  public void advance() {
    vehicleAssemblyLine.advance();
  }
}
