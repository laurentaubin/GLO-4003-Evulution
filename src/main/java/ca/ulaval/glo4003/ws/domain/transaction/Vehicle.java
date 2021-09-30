package ca.ulaval.glo4003.ws.domain.transaction;

import ca.ulaval.glo4003.ws.domain.battery.Battery;
import java.math.BigDecimal;

public class Vehicle {

  // TODO use battery and efficiency in the refactor from models.json
  private Model model;
  private Color color;
  private Battery battery;
  private BigDecimal efficiency;

  public Vehicle(Model model, Color color) {
    this.model = model;
    this.color = color;
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

  public void setBattery(Battery battery) {
    this.battery = battery;
  }

  public Integer computeRange() {
    //     TODO uncomment once refactor of vehicles is done
    //    BigDecimal baseBatteryRange = BigDecimal.valueOf(battery.getBaseNRCANRange());
    //    BigDecimal actualRange =
    //        (baseBatteryRange.multiply(efficiency)).divide(BigDecimal.valueOf(100));
    //    int numberOfDecimalsToKeep = 0;
    //    return actualRange;

    return this.battery.getBaseNRCANRange();
  }
}
