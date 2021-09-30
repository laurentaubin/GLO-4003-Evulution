package ca.ulaval.glo4003.ws.domain.battery;

import java.util.Map;

public interface BatteryRepository {
  void save(Map<String, Battery> batteries);

  Battery findByType(String batteryType);
}
