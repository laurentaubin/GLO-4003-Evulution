package ca.ulaval.glo4003.ws.service.manufacturer;

import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.domain.manufacturer.ManufacturerScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManufacturerServiceTest {
  @Mock private ManufacturerScheduler manufacturerScheduler;

  private ManufacturerService manufacturerService;

  @BeforeEach
  public void setUp() {
    manufacturerService = new ManufacturerService(manufacturerScheduler);
  }

  @Test
  public void whenShutdown_thenShutdownVehiclePartManufacturer() {
    // when
    manufacturerService.shutdown();

    // then
    verify(manufacturerScheduler).shutdown();
  }

  @Test
  public void whenAdvanceTime_thenAdvanceTimeOnVehiclePartManufacturer() {
    // when
    manufacturerService.advanceTime();

    // then
    verify(manufacturerScheduler).advanceTime();
  }

  @Test
  public void whenReactivate_thenReactivateVehiclePartManufacturer() {
    // when
    manufacturerService.reactivate();

    // then
    verify(manufacturerScheduler).reactive();
  }
}
