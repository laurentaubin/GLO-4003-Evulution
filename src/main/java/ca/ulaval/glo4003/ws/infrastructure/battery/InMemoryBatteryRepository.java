package ca.ulaval.glo4003.ws.infrastructure.battery;

import ca.ulaval.glo4003.ws.domain.battery.Battery;
import ca.ulaval.glo4003.ws.domain.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.battery.InvalidBatteryException;
import java.util.Map;

public class InMemoryBatteryRepository implements BatteryRepository {
  private final Map<String, Battery> existingBatteries;

  public InMemoryBatteryRepository(Map<String, Battery> batteries) {
    this.existingBatteries = batteries;
  }

  public Battery findByType(String batteryType) {
    if (existingBatteries.containsKey(batteryType.toUpperCase())) {
      return existingBatteries.get(batteryType.toUpperCase());
    }
    throw new InvalidBatteryException();
  }
}
