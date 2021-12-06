package ca.ulaval.glo4003.ws.api.production;

import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.service.assembly.AssemblyLineService;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class ProductionLineResourceImpl implements ProductionLineResource {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();
  private static final List<Role> PRIVILEGED_ROLES = new ArrayList<>(List.of(Role.ADMIN));

  private final AssemblyLineService assemblyLineService;
  private final RoleHandler roleHandler;

  public ProductionLineResourceImpl() {
    this(
        serviceLocator.resolve(AssemblyLineService.class),
        serviceLocator.resolve(RoleHandler.class));
  }

  public ProductionLineResourceImpl(
      AssemblyLineService assemblyLineService, RoleHandler roleHandler) {
    this.assemblyLineService = assemblyLineService;
    this.roleHandler = roleHandler;
  }

  @Override
  public Response shutdown(ContainerRequestContext containerRequestContext) {
    validateRole(containerRequestContext);
    assemblyLineService.shutdown();
    return Response.ok().build();
  }

  @Override
  public Response reactivate(ContainerRequestContext containerRequestContext) {
    validateRole(containerRequestContext);
    assemblyLineService.activate();
    return Response.ok().build();
  }

  private void validateRole(ContainerRequestContext containerRequestContext) {
    roleHandler.retrieveSession(containerRequestContext, PRIVILEGED_ROLES);
  }
}
