package ca.ulaval.glo4003.ws.infrastructure.schedule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import java.util.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TimeSimulatorTest {
  private static final int ANY_NUMBER_OF_SECONDS = 7;

  private TimeSimulator timeSimulator;

  @Mock private AssemblyLine assemblyLine;
  @Mock private Timer timer;

  @BeforeEach
  public void setUp() {
    timeSimulator = new TimeSimulator(assemblyLine, timer);
  }

  @Test
  public void whenSchedule_thenTimeSchedulerIsCalled() {
    // when
    timeSimulator.schedule(ANY_NUMBER_OF_SECONDS);

    // then
    verify(timer, times(1))
        .schedule(any(WeeklyTaskExecutor.class), any(Long.class), any(Long.class));
  }
}
