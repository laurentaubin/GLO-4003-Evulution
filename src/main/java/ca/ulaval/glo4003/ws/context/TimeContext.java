package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.infrastructure.schedule.TimeSimulator;
import ca.ulaval.glo4003.ws.service.assembly.AssemblyLineService;
import java.util.Timer;

public class TimeContext implements Context {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private static final String defaultSecondsPerWeek = "30";
  private static final String SECONDS_PER_WEEK_JAVA_OPTION = "secondsPerWeek";

  @Override
  public void registerContext() {
    registerTimeSimulator();
  }

  private void registerTimeSimulator() {
    AssemblyLineService assemblyLine = serviceLocator.resolve(AssemblyLineService.class);
    TimeSimulator timeSimulator =
        new TimeSimulator(
            assemblyLine, new Timer(), serviceLocator.resolve(LocalDateProvider.class));

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
