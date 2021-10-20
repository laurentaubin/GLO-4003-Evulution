package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.domain.vehicle.battery.Battery;
import ca.ulaval.glo4003.ws.domain.vehicle.battery.BatteryRepository;
import ca.ulaval.glo4003.ws.domain.vehicle.model.Model;
import ca.ulaval.glo4003.ws.domain.vehicle.model.ModelRepository;
import ca.ulaval.glo4003.ws.infrastructure.battery.BatteryDto;
import ca.ulaval.glo4003.ws.infrastructure.battery.BatteryDtoAssembler;
import ca.ulaval.glo4003.ws.infrastructure.battery.InMemoryBatteryRepository;
import ca.ulaval.glo4003.ws.infrastructure.model.InMemoryModelRepository;
import ca.ulaval.glo4003.ws.infrastructure.model.ModelDto;
import ca.ulaval.glo4003.ws.infrastructure.model.ModelDtoAssembler;
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

  public static ServiceLocator serviceLocator = ServiceLocator.getInstance();

  @Override
  public void registerContext() {
    registerBatteryInventory();
    registerModelInventory();
  }

  private void registerBatteryInventory() {
    serviceLocator.register(BatteryDtoAssembler.class, new BatteryDtoAssembler());

    var batteryInventory = setupBatteryInventory(serviceLocator.resolve(BatteryDtoAssembler.class));
    serviceLocator.register(
        BatteryRepository.class, new InMemoryBatteryRepository(batteryInventory));
  }

  private void registerModelInventory() {
    serviceLocator.register(ModelDtoAssembler.class, new ModelDtoAssembler());

    var modelsInventory = setUpModelInventory(serviceLocator.resolve(ModelDtoAssembler.class));
    serviceLocator.register(ModelRepository.class, new InMemoryModelRepository(modelsInventory));
  }

  private static Map<String, Battery> setupBatteryInventory(
      BatteryDtoAssembler batteryDTOAssembler) {
    Map<String, Battery> batteriesInventory = new HashMap<>();
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      List<Battery> batteriesListFromContext =
          batteryDTOAssembler.assembleBatteries(
              objectMapper.readValue(BATTERY_INFO_FILE, new TypeReference<List<BatteryDto>>() {}));
      for (Battery battery : batteriesListFromContext) {
        batteriesInventory.put(battery.getType(), battery);
      }
      return batteriesInventory;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return batteriesInventory;
  }

  private static Map<String, Model> setUpModelInventory(ModelDtoAssembler modelDtoAssembler) {
    Map<String, Model> modelInventory = new HashMap<>();
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      List<Model> modelListFromContext =
          modelDtoAssembler.assembleModels(
              objectMapper.readValue(MODEL_INVENTORY, new TypeReference<List<ModelDto>>() {}));
      for (Model model : modelListFromContext) {
        modelInventory.put(model.getName(), model);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return modelInventory;
  }
}
