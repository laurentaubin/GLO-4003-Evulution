package ca.ulaval.glo4003.ws.infrastructure.schedule;

import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.service.manufacturer.ManufacturerService;
import java.util.TimerTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WeeklyTaskExecutor extends TimerTask {
  private static final Logger LOGGER = LogManager.getLogger();

  private final ManufacturerService manufacturerService;
  private final LocalDateProvider localDateProvider;

  public WeeklyTaskExecutor(
      ManufacturerService manufacturerService, LocalDateProvider localDateProvider) {
    this.manufacturerService = manufacturerService;
    this.localDateProvider = localDateProvider;
  }

  public void run() {
    try {
      manufacturerService.advanceTime();
      localDateProvider.advance();
    } catch (Exception exception) {
      LOGGER.error(
          "There was an error when advancing assembly line. See trace for more detail", exception);
    }
  }
}
