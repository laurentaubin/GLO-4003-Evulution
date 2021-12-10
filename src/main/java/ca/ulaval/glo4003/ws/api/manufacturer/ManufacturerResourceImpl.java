package ca.ulaval.glo4003.ws.api.manufacturer;

import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.service.manufacturer.ManufacturerService;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class ManufacturerResourceImpl implements ManufacturerResource {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();
  private static final List<Role> PRIVILEGED_ROLES = new ArrayList<>(List.of(Role.ADMIN));

  private final ManufacturerService manufacturerService;
  private final RoleHandler roleHandler;

  public ManufacturerResourceImpl() {
    this(
        serviceLocator.resolve(ManufacturerService.class),
        serviceLocator.resolve(RoleHandler.class));
  }

  public ManufacturerResourceImpl(
      ManufacturerService manufacturerService, RoleHandler roleHandler) {
    this.manufacturerService = manufacturerService;
    this.roleHandler = roleHandler;
  }

  @Override
  public Response shutdown(ContainerRequestContext containerRequestContext) {
    validateRole(containerRequestContext);
    manufacturerService.shutdown();
    return Response.ok().build();
  }

  @Override
  public Response reactivate(ContainerRequestContext containerRequestContext) {
    validateRole(containerRequestContext);
    manufacturerService.reactivate();
    return Response.ok().build();
  }

  private void validateRole(ContainerRequestContext containerRequestContext) {
    roleHandler.retrieveSession(containerRequestContext, PRIVILEGED_ROLES);
  }
}
