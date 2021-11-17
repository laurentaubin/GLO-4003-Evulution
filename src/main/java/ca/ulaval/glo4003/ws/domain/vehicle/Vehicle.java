package ca.ulaval.glo4003.ws.domain.vehicle;

import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.exception.IncompleteVehicleException;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Vehicle {

  private final Model model;
  private final Color color;
  private Battery battery;

  public Vehicle(Model model, Color color) {
    this.model = model;
    this.color = color;
  }

  public void addBattery(Battery battery) {
    this.battery = battery;
  }

  public BigDecimal computeRange() {
    BigDecimal baseBatteryRange = BigDecimal.valueOf(battery.getBaseNRCANRange());
    return (baseBatteryRange.multiply(model.getEfficiency()))
        .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
  }

  public boolean hasBattery() {
    return battery != null;
  }

  public Model getModel() {
    return model;
  }

  public Color getColor() {
    return color;
  }

  public Battery getBattery() {
    return battery;
  }

  public Integer getVehiclePrice() {
    if (hasBattery() && model != null) {
      return battery.getPrice() + model.getPrice();
    }
    throw new IncompleteVehicleException("Vehicle must be complete to calculate price.");
  }
}
