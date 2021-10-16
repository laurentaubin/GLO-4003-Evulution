package ca.ulaval.glo4003.ws.infrastructure.schedule;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeeklyTaskExecutorTest {

  private WeeklyTaskExecutor weeklyTaskExecutor;

  @Mock private AssemblyLine assemblyLine;

  @BeforeEach
  public void setUp() {
    weeklyTaskExecutor = new WeeklyTaskExecutor(assemblyLine);
  }

  @Test
  public void whenRun_thenAssemblyStrategyIsAdvanced() {
    // when
    weeklyTaskExecutor.run();

    verify(assemblyLine, times(1)).advance();
  }
}
