package ca.ulaval.glo4003.ws.context;

import ca.ulaval.glo4003.ws.service.manufacturer.schedule.TimeSimulator;

public class TimeContext implements Context {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private static final String defaultSecondsPerWeek = "30";
  private static final String SECONDS_PER_WEEK_JAVA_OPTION = "secondsPerWeek";

  @Override
  public void registerContext() {
    registerTimeSimulator();
  }

  private void registerTimeSimulator() {
    TimeSimulator timeSimulator = new TimeSimulator();
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
