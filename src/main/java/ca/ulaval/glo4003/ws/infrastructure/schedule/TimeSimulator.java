package ca.ulaval.glo4003.ws.infrastructure.schedule;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import java.util.Timer;

public class TimeSimulator {
  private final Timer timer;
  private final AssemblyLine assemblyLine;

  public TimeSimulator(AssemblyLine assemblyLine, Timer timer) {
    this.assemblyLine = assemblyLine;
    this.timer = timer;
  }

  public void schedule(int secondsPerWeek) {
    timer.schedule(new WeeklyTaskExecutor(assemblyLine), 0, secondsPerWeek * 1000L);
  }
}
