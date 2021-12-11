package ca.ulaval.glo4003.ws.service.manufacturer;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.manufacturer.ManufacturerScheduler;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.service.user.UserService;
import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ManufacturerService {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();
  private static final Logger LOGGER = LogManager.getLogger();

  private final ManufacturerScheduler manufacturerScheduler;
  private final UserService userService;

  private final List<Role> PRIVILEGED_ROLES = List.of(Role.PRODUCTION_MANAGER);

  public ManufacturerService() {
    this(
        serviceLocator.resolve(ManufacturerScheduler.class),
        serviceLocator.resolve(UserService.class));
  }

  public ManufacturerService(ManufacturerScheduler manufacturerScheduler, UserService userService) {
    this.manufacturerScheduler = manufacturerScheduler;
    this.userService = userService;
  }

  public void advanceTime() {
    LOGGER.info("");
    LOGGER.info("Advancing time: +1 week");
    manufacturerScheduler.advanceTime();
  }

  public void shutdown(TokenDto tokenDto) {
    userService.isAllowed(tokenDto, PRIVILEGED_ROLES);
    manufacturerScheduler.shutdown();
  }

  public void reactivate(TokenDto tokenDto) {
    userService.isAllowed(tokenDto, PRIVILEGED_ROLES);
    manufacturerScheduler.reactive();
  }
}
