package ca.ulaval.glo4003.ws.infrastructure.schedule;

import ca.ulaval.glo4003.ws.api.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;

import java.util.Timer;

public class TimeSimulator {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final Timer timer;
  private final AssemblyLine assemblyLine;
  private final LocalDateProvider localDateProvider;

  public TimeSimulator() {
    this(
        serviceLocator.resolve(AssemblyLine.class),
        new Timer(),
        serviceLocator.resolve(LocalDateProvider.class));
  }

  public TimeSimulator(
      AssemblyLine assemblyLine, Timer timer, LocalDateProvider localDateProvider) {
    this.assemblyLine = assemblyLine;
    this.timer = timer;
    this.localDateProvider = localDateProvider;
  }

  public void schedule(int secondsPerWeek) {
    timer.schedule(
        new WeeklyTaskExecutor(assemblyLine, localDateProvider),
        secondsPerWeek * 1000L,
        secondsPerWeek * 1000L);
  }
}
