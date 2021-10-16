package ca.ulaval.glo4003.ws.infrastructure.schedule;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import java.util.TimerTask;

public class WeeklyTaskExecutor extends TimerTask {
  private final AssemblyLine assemblyLine;

  public WeeklyTaskExecutor(AssemblyLine assemblyLine) {
    this.assemblyLine = assemblyLine;
  }

  public void run() {
    assemblyLine.advance();
  }
}
