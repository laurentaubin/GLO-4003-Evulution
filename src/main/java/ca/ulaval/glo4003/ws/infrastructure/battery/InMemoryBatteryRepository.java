package ca.ulaval.glo4003.ws.infrastructure.battery;

import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.domain.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.battery.InvalidBatteryException;
import java.util.HashMap;
import java.util.Map;

public class InMemoryBatteryRepository implements BatteryRepository {
  Map<String, Battery> existingBatteries = new HashMap<>();

  public void save(Map<String, Battery> batteries) {
    this.existingBatteries = batteries;
  }

  public Battery findByType(String batteryType) {
    if (!existingBatteries.containsKey(batteryType)) {
      throw new InvalidBatteryException();
    }
    return existingBatteries.get(batteryType);
  }
}
