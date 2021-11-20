package ca.ulaval.glo4003.ws.domain.assembly.strategy.accumulate.battery;

public interface BatteryInventory {
  void addOne(String batteryType);

  boolean isInStock(String batteryType);

  void removeOne(String batteryType);
}
