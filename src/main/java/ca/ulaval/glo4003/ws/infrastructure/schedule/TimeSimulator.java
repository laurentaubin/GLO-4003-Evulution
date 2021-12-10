package ca.ulaval.glo4003.ws.infrastructure.schedule;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.report.ReportsService;
import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.service.manufacturer.ManufacturerService;
import java.util.Timer;

public class TimeSimulator {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final Timer timer;
  private final ReportsService reportsService;
  private final ManufacturerService manufacturerService;
  private final LocalDateProvider localDateProvider;

  public TimeSimulator() {
    this(
        serviceLocator.resolve(ReportsService.class),
        serviceLocator.resolve(ManufacturerService.class),
        new Timer(),
        serviceLocator.resolve(LocalDateProvider.class));
  }

  public TimeSimulator(
      ReportsService reportsService,
      ManufacturerService manufacturerService, Timer timer, LocalDateProvider localDateProvider) {
    this.reportsService = reportsService;
    this.manufacturerService = manufacturerService;
    this.timer = timer;
    this.localDateProvider = localDateProvider;
  }

  public void schedule(int secondsPerWeek) {
    timer.schedule(
        new WeeklyTaskExecutor(reportsService, manufacturerService, localDateProvider),
        secondsPerWeek * 1000L,
        secondsPerWeek * 1000L);
  }
}
