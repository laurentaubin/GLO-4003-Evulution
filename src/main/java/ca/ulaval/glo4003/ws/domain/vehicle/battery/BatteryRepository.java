package ca.ulaval.glo4003.ws.domain.vehicle.battery;

public interface BatteryRepository {
  Battery findByType(String batteryType);
}