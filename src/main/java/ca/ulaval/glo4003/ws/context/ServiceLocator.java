package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.context.exception.ContractAlreadyRegisteredException;
import ca.ulaval.glo4003.ws.context.exception.UnresolvedContractException;

import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {
  private static ServiceLocator locator;
  private final Map<Class<?>, Object> instances = new HashMap<>();

  public static ServiceLocator getInstance() {
    if (locator == null) {
      locator = new ServiceLocator();
    }
    return locator;
  }

  public <T> void register(Class<T> contract, T instance) {
    if (instances.containsKey(contract)) {
      throw new ContractAlreadyRegisteredException(contract);
    }
    instances.put(contract, instance);
  }

  public <T> T resolve(Class<T> contract) {
    T instance = (T) instances.get(contract);
    if (instance == null) {
      throw new UnresolvedContractException(contract);
    }
    return instance;
  }
}
