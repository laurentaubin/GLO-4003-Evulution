package ca.ulaval.glo4003.ws.infrastructure.schedule;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.TimerTask;

public class WeeklyTaskExecutor extends TimerTask {
  private static final Logger LOGGER = LogManager.getLogger();

  private final AssemblyLine assemblyLine;

  public WeeklyTaskExecutor(AssemblyLine assemblyLine) {
    this.assemblyLine = assemblyLine;
  }

  public void run() {
    try {
      assemblyLine.advance();
    } catch (Exception exception) {
      LOGGER.error(
          "There was an error when advancing assembly line. See trace for more detail", exception);
    }
  }
}
