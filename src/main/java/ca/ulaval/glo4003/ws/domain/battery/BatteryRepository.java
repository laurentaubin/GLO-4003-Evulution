package ca.ulaval.glo4003.ws.domain.battery;

public interface BatteryRepository {
  Battery findByType(String batteryType);
}
