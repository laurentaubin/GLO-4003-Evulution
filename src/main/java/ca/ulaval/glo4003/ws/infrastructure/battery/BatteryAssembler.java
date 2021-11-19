package ca.ulaval.glo4003.ws.infrastructure.battery;

import ca.ulaval.glo4003.ws.domain.transaction.payment.Price;
import ca.ulaval.glo4003.ws.domain.vehicle.ProductionTime;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;

public class BatteryAssembler {
  public Battery assembleBattery(BatteryDto batteryDto) {
    return new Battery(
        batteryDto.type,
        Integer.valueOf(batteryDto.base_NRCAN_range),
        batteryDto.capacity,
        new Price(batteryDto.price),
        new ProductionTime(Integer.parseInt(batteryDto.time_to_produce)));
  }
}
