package ca.ulaval.glo4003.ws.infrastructure.battery;

import ca.ulaval.glo4003.ws.domain.battery.Battery;
import java.util.ArrayList;
import java.util.List;

public class BatteryDtoAssembler {
  public List<Battery> assembleBatteries(List<BatteryDto> batteryDtos) {
    List<Battery> batteries = new ArrayList<>();
    for (BatteryDto batteryInventory : batteryDtos) {
      batteries.add(assembleBattery(batteryInventory));
    }
    return batteries;
  }

  private Battery assembleBattery(BatteryDto batteryDto) {
    return new Battery(
        batteryDto.type.toUpperCase(),
        Integer.valueOf(batteryDto.base_NRCAN_range),
        batteryDto.capacity,
        batteryDto.price,
        Integer.valueOf(batteryDto.time_to_produce));
  }
}
