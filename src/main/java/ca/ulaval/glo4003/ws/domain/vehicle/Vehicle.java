package ca.ulaval.glo4003.ws.domain.vehicle;

import ca.ulaval.glo4003.ws.domain.battery.Battery;
import java.math.BigDecimal;

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
    return (baseBatteryRange.multiply(model.getEfficiency())).divide(BigDecimal.valueOf(100));
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
}
