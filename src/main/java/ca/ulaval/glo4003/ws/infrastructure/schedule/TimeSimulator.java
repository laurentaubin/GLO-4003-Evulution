package ca.ulaval.glo4003.ws.infrastructure.schedule;

import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.service.AssemblyLineService;
import java.util.Timer;

public class TimeSimulator {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final Timer timer;
  private final AssemblyLineService assemblyLineService;
  private final LocalDateProvider localDateProvider;

  public TimeSimulator() {
    this(
        serviceLocator.resolve(AssemblyLineService.class),
        new Timer(),
        serviceLocator.resolve(LocalDateProvider.class));
  }

  public TimeSimulator(
      AssemblyLineService assemblyLineService, Timer timer, LocalDateProvider localDateProvider) {
    this.assemblyLineService = assemblyLineService;
    this.timer = timer;
    this.localDateProvider = localDateProvider;
  }

  public void schedule(int secondsPerWeek) {
    timer.schedule(
        new WeeklyTaskExecutor(assemblyLineService, localDateProvider),
        secondsPerWeek * 1000L,
        secondsPerWeek * 1000L);
  }
}
