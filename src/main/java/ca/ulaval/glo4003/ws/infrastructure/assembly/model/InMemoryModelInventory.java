package ca.ulaval.glo4003.ws.infrastructure.assembly.model;

import ca.ulaval.glo4003.ws.domain.assembly.strategy.accumulate.model.ModelInventory;
import ca.ulaval.glo4003.ws.domain.assembly.strategy.accumulate.model.NotAvailableModelException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InMemoryModelInventory implements ModelInventory {
  private static final Logger LOGGER = LogManager.getLogger();

  private Map<String, Integer> quantityPerModelType = new HashMap<>();

  @Override
  public void addOne(String modelType) {
    if (!quantityPerModelType.containsKey(modelType)) {
      quantityPerModelType.put(modelType, 1);
    } else {
      quantityPerModelType.put(modelType, quantityPerModelType.get(modelType) + 1);
    }
  }

  @Override
  public boolean isInStock(String modelType) {
    return isAtLeastOneOfModelTypeInStock(modelType);
  }

  @Override
  public void removeOne(String modelType) {
    if (isAtLeastOneOfModelTypeInStock(modelType)) {
      quantityPerModelType.put(modelType, quantityPerModelType.get(modelType) - 1);
    } else {
      LOGGER.info(
          String.format("Model type %s not in stock, cannot remove one from inventory", modelType));
      throw new NotAvailableModelException(
          String.format("No available model of type: %s", modelType));
    }
  }

  private boolean isAtLeastOneOfModelTypeInStock(String modelType) {
    return quantityPerModelType.containsKey(modelType) && quantityPerModelType.get(modelType) > 0;
  }
}
