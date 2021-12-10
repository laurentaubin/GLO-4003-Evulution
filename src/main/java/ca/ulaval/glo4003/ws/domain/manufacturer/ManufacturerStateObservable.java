package ca.ulaval.glo4003.ws.domain.manufacturer;

import java.util.ArrayList;
import java.util.List;

public class ManufacturerStateObservable {
  private final List<ShutdownObserver> shutdownObservers = new ArrayList<>();
  private final List<ReactivateObserver> reactivateObservers = new ArrayList<>();

  public void registerShutdownObserver(ShutdownObserver observer) {
    shutdownObservers.add(observer);
  }

  public void registerReactivateObserver(ReactivateObserver observer) {
    reactivateObservers.add(observer);
  }

  public void notifyShutdown() {
    for (ShutdownObserver observer : shutdownObservers) {
      observer.listenToAssemblyShutdown();
    }
  }

  public void notifyReactivation() {
    for (ReactivateObserver observer : reactivateObservers) {
      observer.listenToAssemblyReactivation();
    }
  }
}
