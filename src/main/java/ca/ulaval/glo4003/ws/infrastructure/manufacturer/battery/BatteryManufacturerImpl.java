package ca.ulaval.glo4003.ws.infrastructure.manufacturer.battery;

import ca.ulaval.glo4003.ws.domain.manufacturer.PeriodicManufacturer;
import ca.ulaval.glo4003.ws.domain.manufacturer.battery.BatteryAssembledObservable;
import ca.ulaval.glo4003.ws.domain.manufacturer.battery.BatteryAssemblyLineAdapter;
import ca.ulaval.glo4003.ws.domain.manufacturer.battery.BatteryManufacturer;
import ca.ulaval.glo4003.ws.domain.warehouse.AssemblyStatus;
import ca.ulaval.glo4003.ws.domain.warehouse.battery.BatteryOrder;
import ca.ulaval.glo4003.ws.domain.warehouse.time.AssemblyTime;
import java.util.ArrayList;
import java.util.List;

public class BatteryManufacturerImpl extends BatteryAssembledObservable
    implements BatteryManufacturer, PeriodicManufacturer {
  private static final AssemblyTime ONE_WEEK = new AssemblyTime(1);

  private final List<BatteryOrder> batteryOrders = new ArrayList<>();

  private BatteryOrder currentBatteryOrder;
  private AssemblyTime currentBatteryRemainingAssemblyTime;
  private final BatteryAssemblyLineAdapter batteryAssemblyLineAdapter;

  public BatteryManufacturerImpl(BatteryAssemblyLineAdapter batteryAssemblyLineAdapter) {
    this.batteryAssemblyLineAdapter = batteryAssemblyLineAdapter;
  }

  @Override
  public void advanceTime() {
    batteryAssemblyLineAdapter.advance();
    if (currentBatteryOrder != null) {
      processCurrentOrder();
    }
  }

  @Override
  public void stop() {
    currentBatteryOrder = null;
    currentBatteryRemainingAssemblyTime = null;
    batteryOrders.clear();
  }

  @Override
  public void addOrder(BatteryOrder batteryOrder) {
    if (currentBatteryOrder == null) {
      currentBatteryOrder = batteryOrder;
      currentBatteryRemainingAssemblyTime = new AssemblyTime(batteryOrder.getAssemblyTime());
      batteryAssemblyLineAdapter.addOrder(currentBatteryOrder);
    } else {
      batteryOrders.add(batteryOrder);
    }
  }

  @Override
  public AssemblyTime computeRemainingTimeToProduceNextBatteryType(String batteryType) {
    if (currentBatteryRemainingAssemblyTime == null) {
      return new AssemblyTime(0);
    }
    AssemblyTime remainingAssemblyTime = new AssemblyTime(currentBatteryRemainingAssemblyTime);
    System.out.println(batteryType);
    System.out.println(currentBatteryOrder.getBatteryType());
    if (currentBatteryOrder.getBatteryType().equals(batteryType)) {
      return remainingAssemblyTime;
    }
    for (BatteryOrder batteryOrder : batteryOrders) {
      remainingAssemblyTime = remainingAssemblyTime.add(batteryOrder.getAssemblyTime());
      if (batteryOrder.getBatteryType().equals(batteryType)) {
        break;
      }
    }
    return remainingAssemblyTime;
  }

  private void processCurrentOrder() {
    currentBatteryRemainingAssemblyTime = currentBatteryRemainingAssemblyTime.subtract(ONE_WEEK);
    if (isCurrentOrderAssembled()) {
      notifyBatteryAssembled(currentBatteryOrder);
      sendNextOrder();
    }
  }

  private void sendNextOrder() {
    if (!batteryOrders.isEmpty()) {
      currentBatteryOrder = batteryOrders.remove(0);
      currentBatteryRemainingAssemblyTime = new AssemblyTime(currentBatteryOrder.getAssemblyTime());
      batteryAssemblyLineAdapter.addOrder(currentBatteryOrder);
    } else {
      currentBatteryOrder = null;
      currentBatteryRemainingAssemblyTime = null;
    }
  }

  private boolean isCurrentOrderAssembled() {
    return batteryAssemblyLineAdapter.getAssemblyStatus(currentBatteryOrder.getOrderId())
        == AssemblyStatus.ASSEMBLED;
  }
}
