package ca.ulaval.glo4003.ws.api.production;

import ca.ulaval.glo4003.ws.api.handler.RoleHandler;
import ca.ulaval.glo4003.ws.domain.production.ProductionLineService;
import ca.ulaval.glo4003.ws.domain.user.Role;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

public class ProductionLineResourceImpl implements ProductionLineResource {
  private static final List<Role> PRIVILEGED_ROLES = new ArrayList<>(List.of(Role.ADMIN));

  private final ProductionLineService productionLineService;
  private final RoleHandler roleHandler;

  public ProductionLineResourceImpl(
      ProductionLineService productionLineService, RoleHandler roleHandler) {
    this.productionLineService = productionLineService;
    this.roleHandler = roleHandler;
  }

  @Override
  public Response shutdown(ContainerRequestContext containerRequestContext) {
    validateRole(containerRequestContext);
    productionLineService.shutdown();
    return Response.ok().build();
  }

  @Override
  public Response reactivate(ContainerRequestContext containerRequestContext) {
    validateRole(containerRequestContext);
    productionLineService.reactivate();
    return Response.ok().build();
  }

  private void validateRole(ContainerRequestContext containerRequestContext) {
    roleHandler.retrieveSession(containerRequestContext, PRIVILEGED_ROLES);
  }
}
