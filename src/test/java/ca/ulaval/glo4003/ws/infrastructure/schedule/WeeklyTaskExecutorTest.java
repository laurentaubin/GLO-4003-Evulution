package ca.ulaval.glo4003.ws.infrastructure.schedule;

import ca.ulaval.glo4003.ws.domain.assembly.AssemblyLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

  @Test
  public void givenAdvanceThrowsException_whenRun_thenDoNotThrowExceptionAndKeepGoing() {
    // given
    doThrow(new RuntimeException()).when(assemblyLine).advance();

    // when
    Executable running = () -> weeklyTaskExecutor.run();

    // then
    assertDoesNotThrow(running);
  }
}
