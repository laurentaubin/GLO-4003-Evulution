package ca.ulaval.glo4003.ws.infrastructure.battery;

import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.exception.InvalidBatteryException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryBatteryRepository implements BatteryRepository {
  private final Map<String, BatteryDto> existingBatteries;
  private final BatteryAssembler batteryAssembler;

  public InMemoryBatteryRepository(
      Map<String, BatteryDto> batteries, BatteryAssembler batteryAssembler) {
    this.existingBatteries = batteries;
    this.batteryAssembler = batteryAssembler;
  }

  public Battery findByType(String batteryType) {
    if (existingBatteries.containsKey(batteryType.toUpperCase())) {
      return batteryAssembler.assembleBattery(existingBatteries.get(batteryType.toUpperCase()));
    }
    throw new InvalidBatteryException(existingBatteries.keySet());
  }

  @Override
  public Collection<Battery> findAllBatteries() {
    return existingBatteries.values().stream()
        .map(batteryAssembler::assembleBattery)
        .collect(Collectors.toList());
  }
}
