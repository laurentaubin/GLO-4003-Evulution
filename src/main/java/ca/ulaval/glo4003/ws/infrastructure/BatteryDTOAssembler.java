package ca.ulaval.glo4003.ws.infrastructure;

import ca.ulaval.glo4003.ws.domain.battery.Battery;
import java.util.ArrayList;
import java.util.List;

public class BatteryDTOAssembler {

  public Battery assembleBattery(BatteryDTO batteryDTO) {
    return new Battery(
        batteryDTO.type,
        Integer.valueOf(batteryDTO.base_NRCAN_range),
        batteryDTO.capacity,
        batteryDTO.price,
        Integer.valueOf(batteryDTO.time_to_produce));
  }

  public List<Battery> assembleBatteries(List<BatteryDTO> batteryDTOS) {
    List<Battery> batteries = new ArrayList<>();
    for (BatteryDTO batteryInventory : batteryDTOS) {
      batteries.add(assembleBattery(batteryInventory));
    }
    return batteries;
  }
}
