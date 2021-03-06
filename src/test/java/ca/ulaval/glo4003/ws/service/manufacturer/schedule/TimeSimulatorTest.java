package ca.ulaval.glo4003.ws.service.manufacturer.schedule;


import ca.ulaval.glo4003.ws.domain.report.ReportsService;
import ca.ulaval.glo4003.ws.domain.shared.LocalDateProvider;
import ca.ulaval.glo4003.ws.service.manufacturer.ManufacturerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Timer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TimeSimulatorTest {
  private static final int ANY_NUMBER_OF_SECONDS = 7;

  private TimeSimulator timeSimulator;

  @Mock private ReportsService reportsService;
  @Mock private ManufacturerService manufacturerService;
  @Mock private Timer timer;
  @Mock private LocalDateProvider localDateProvider;

  @BeforeEach
  public void setUp() {
    timeSimulator =
        new TimeSimulator(reportsService, manufacturerService, timer, localDateProvider);
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
