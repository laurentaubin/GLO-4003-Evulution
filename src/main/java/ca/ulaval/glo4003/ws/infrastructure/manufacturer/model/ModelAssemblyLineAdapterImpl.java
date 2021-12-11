package ca.ulaval.glo4003.ws.infrastructure.manufacturer.model;

import ca.ulaval.glo4003.evulution.car_manufacture.BuildStatus;
import ca.ulaval.glo4003.evulution.car_manufacture.CommandID;
import ca.ulaval.glo4003.evulution.car_manufacture.VehicleAssemblyLine;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.manufacturer.model.ModelAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.warehouse.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.warehouse.model.ModelOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.order.OrderId;
import ca.ulaval.glo4003.ws.infrastructure.warehouse.CommandIdFactory;

public class ModelAssemblyLineAdapterImpl implements ModelAssemblyLineAdapter {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final VehicleAssemblyLine vehicleAssemblyLine;
  private final CommandIdFactory commandIdFactory;

  public ModelAssemblyLineAdapterImpl() {
    this(serviceLocator.resolve(VehicleAssemblyLine.class), new CommandIdFactory());
  }

  public ModelAssemblyLineAdapterImpl(
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
  public void addOrder(ModelOrder modelOrder) {
    CommandID commandID = commandIdFactory.getOrCreateFromOrderId(modelOrder.getOrderId());
    vehicleAssemblyLine.newCarCommand(commandID, modelOrder.getModelType());
  }

  @Override
  public void advance() {
    vehicleAssemblyLine.advance();
  }
}
