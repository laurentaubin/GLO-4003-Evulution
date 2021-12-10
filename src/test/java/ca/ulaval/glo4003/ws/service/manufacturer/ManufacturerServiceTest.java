package ca.ulaval.glo4003.ws.service.manufacturer;

import ca.ulaval.glo4003.ws.domain.manufacturer.ManufacturerScheduler;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.service.user.UserService;
import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManufacturerServiceTest {
  private static final List<Role> PRIVILEGED_ROLES = List.of(Role.PRODUCTION_MANAGER);

  @Mock private ManufacturerScheduler manufacturerScheduler;
  @Mock private TokenDto tokenDto;
  @Mock private UserService userService;


  private ManufacturerService manufacturerService;

  @BeforeEach
  public void setUp() {
    manufacturerService = new ManufacturerService(manufacturerScheduler, userService);
  }

  @Test
  public void whenShutdown_thenShutdownVehiclePartManufacturer() {
    // when
    manufacturerService.shutdown(tokenDto);

    // then
    verify(manufacturerScheduler).shutdown();
  }

  @Test
  public void whenShutdown_thenVerifyIfTokenIsAllowed() {
    // when
    manufacturerService.shutdown(tokenDto);

    // then
    verify(userService).isAllowed(tokenDto, PRIVILEGED_ROLES);
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
    manufacturerService.reactivate(tokenDto);

    // then
    verify(manufacturerScheduler).reactive();
  }

  @Test
  public void whenReactivate_thenVerifyIfTokenIsAllowed() {
    // when
    manufacturerService.reactivate(tokenDto);

    // then
    verify(userService).isAllowed(tokenDto, PRIVILEGED_ROLES);
  }
}
