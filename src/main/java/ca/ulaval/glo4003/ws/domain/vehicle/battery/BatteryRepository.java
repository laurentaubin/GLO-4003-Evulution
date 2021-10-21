package ca.ulaval.glo4003.ws.domain.vehicle.battery;

import java.util.Collection;

public interface BatteryRepository {
  Battery findByType(String batteryType);

  Collection<Battery> findAllBatteries();
}
