package ca.ulaval.glo4003.ws.service.manufacturer;

import ca.ulaval.glo4003.ws.domain.manufacturer.ManufacturerScheduler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ManufacturerService {
  private static final Logger LOGGER = LogManager.getLogger();

  private final ManufacturerScheduler manufacturerScheduler;

  public ManufacturerService(ManufacturerScheduler manufacturerScheduler) {
    this.manufacturerScheduler = manufacturerScheduler;
  }

  public void advanceTime() {
    LOGGER.info("");
    LOGGER.info("Advancing time: +1 week");
    manufacturerScheduler.advanceTime();
  }

  public void shutdown() {
    manufacturerScheduler.shutdown();
  }

  public void reactivate() {
    manufacturerScheduler.reactive();
  }
}
