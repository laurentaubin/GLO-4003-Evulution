package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.domain.report.ReportsService;
import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.service.manufacturer.schedule.TimeSimulator;
import ca.ulaval.glo4003.ws.service.manufacturer.ManufacturerService;

import java.util.Timer;

public class TimeContext implements Context {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private static final String defaultSecondsPerWeek = "2";
  private static final String SECONDS_PER_WEEK_JAVA_OPTION = "secondsPerWeek";

  @Override
  public void registerContext() {
    registerTimeSimulator();
  }

  private void registerTimeSimulator() {
    ManufacturerService manufacturerService = serviceLocator.resolve(ManufacturerService.class);
    ReportsService reportsService = serviceLocator.resolve(ReportsService.class);
    TimeSimulator timeSimulator =
        new TimeSimulator(
            reportsService,
            manufacturerService,
            new Timer(),
            serviceLocator.resolve(LocalDateProvider.class));

    timeSimulator.schedule(getSecondsPerWeekFromArgs());
    serviceLocator.register(TimeSimulator.class, timeSimulator);
  }

  private static int getSecondsPerWeekFromArgs() {
    String secondsPerWeek = System.getProperty(SECONDS_PER_WEEK_JAVA_OPTION);

    if (secondsPerWeek == null || secondsPerWeek.isEmpty()) {
      secondsPerWeek = defaultSecondsPerWeek;
    }
    return Integer.parseInt(secondsPerWeek);
  }
}
