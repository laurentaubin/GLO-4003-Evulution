package ca.ulaval.glo4003.ws.service.manufacturer.schedule;


import ca.ulaval.glo4003.ws.domain.report.ReportsService;
import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.service.manufacturer.ManufacturerService;
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

  @Mock private ReportsService reportsService;
  @Mock private ManufacturerService manufacturerService;
  @Mock private LocalDateProvider localDateProvider;

  @BeforeEach
  public void setUp() {
    weeklyTaskExecutor = new WeeklyTaskExecutor(reportsService, manufacturerService, localDateProvider);
  }

  @Test
  public void whenRun_thenAssemblyStrategyIsAdvanced() {
    // when
    weeklyTaskExecutor.run();

    // then
    verify(manufacturerService, times(1)).advanceTime();
  }

  @Test
  public void whenRun_thenReportsIssuerIsAdvanced() {
    // when
    weeklyTaskExecutor.run();

    // then
    verify(reportsService).advance();
  }

  @Test
  public void whenRun_thenLocalDateProviderIsAdvanced() {
    // when
    weeklyTaskExecutor.run();

    // then
    verify(localDateProvider, times(1)).advance();
  }

  @Test
  public void givenAdvanceThrowsException_whenRun_thenDoNotThrowExceptionAndKeepGoing() {
    // given
    doThrow(new RuntimeException()).when(manufacturerService).advanceTime();

    // when
    Executable running = () -> weeklyTaskExecutor.run();

    // then
    assertDoesNotThrow(running);
  }
}
