package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.domain.vehicle.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.vehicle.model.ModelRepository;
import ca.ulaval.glo4003.ws.infrastructure.battery.BatteryAssembler;
import ca.ulaval.glo4003.ws.infrastructure.battery.BatteryDto;
import ca.ulaval.glo4003.ws.infrastructure.battery.InMemoryBatteryRepository;
import ca.ulaval.glo4003.ws.infrastructure.model.InMemoryModelRepository;
import ca.ulaval.glo4003.ws.infrastructure.model.ModelAssembler;
import ca.ulaval.glo4003.ws.infrastructure.model.ModelDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryContext implements Context {
  private static final File BATTERY_INFO_FILE = new File("./target/classes/batteries.json");
  private static final File MODEL_INVENTORY = new File("./target/classes/models.json");

  public static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  @Override
  public void registerContext() {
    registerBatteryInventory();
    registerModelInventory();
  }

  private void registerBatteryInventory() {
    var batteryInventory = setupBatteryInventory();
    serviceLocator.register(
        BatteryRepository.class,
        new InMemoryBatteryRepository(batteryInventory, new BatteryAssembler()));
  }

  private void registerModelInventory() {
    var modelsInventory = setUpModelInventory();
    serviceLocator.register(
        ModelRepository.class, new InMemoryModelRepository(modelsInventory, new ModelAssembler()));
  }

  private static Map<String, BatteryDto> setupBatteryInventory() {
    Map<String, BatteryDto> batteriesInventory = new HashMap<>();
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      List<BatteryDto> batteriesListFromContext =
          objectMapper.readValue(BATTERY_INFO_FILE, new TypeReference<>() {});
      for (BatteryDto battery : batteriesListFromContext) {
        batteriesInventory.put(battery.type.toUpperCase(), battery);
      }
      return batteriesInventory;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return batteriesInventory;
  }

  private static Map<String, ModelDto> setUpModelInventory() {
    Map<String, ModelDto> modelInventory = new HashMap<>();
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      List<ModelDto> modelDtos = objectMapper.readValue(MODEL_INVENTORY, new TypeReference<>() {});
      for (ModelDto model : modelDtos) {
        modelInventory.put(model.name.toUpperCase(), model);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return modelInventory;
  }
}
