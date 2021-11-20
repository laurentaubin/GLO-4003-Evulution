package ca.ulaval.glo4003.ws.infrastructure.schedule;

import ca.ulaval.glo4003.ws.api.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import java.util.TimerTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WeeklyTaskExecutor extends TimerTask {
  private static final Logger LOGGER = LogManager.getLogger();

  private final AssemblyLine assemblyLine;
  private final LocalDateProvider localDateProvider;

  public WeeklyTaskExecutor(AssemblyLine assemblyLine, LocalDateProvider localDateProvider) {
    this.assemblyLine = assemblyLine;
    this.localDateProvider = localDateProvider;
  }

  public void run() {
    try {
      assemblyLine.advance();
      localDateProvider.advance();
    } catch (Exception exception) {
      LOGGER.error(
          "There was an error when advancing assembly line. See trace for more detail", exception);
    }
  }
}
