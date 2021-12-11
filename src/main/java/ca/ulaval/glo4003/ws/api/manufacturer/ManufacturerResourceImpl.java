package ca.ulaval.glo4003.ws.api.manufacturer;

import ca.ulaval.glo4003.ws.api.shared.TokenExtractor;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.service.manufacturer.ManufacturerService;
import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

public class ManufacturerResourceImpl implements ManufacturerResource {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();
  private final TokenExtractor tokenExtractor;

  private final ManufacturerService manufacturerService;

  public ManufacturerResourceImpl() {
    this(
        serviceLocator.resolve(ManufacturerService.class),
            serviceLocator.resolve(TokenExtractor.class));
  }

  public ManufacturerResourceImpl(
      ManufacturerService manufacturerService, TokenExtractor tokenExtractor) {
    this.manufacturerService = manufacturerService;
    this.tokenExtractor = tokenExtractor;
  }

  @Override
  public Response shutdown(ContainerRequestContext containerRequestContext) {
    TokenDto tokenDto = tokenExtractor.extract(containerRequestContext);
    manufacturerService.shutdown(tokenDto);
    return Response.ok().build();
  }

  @Override
  public Response reactivate(ContainerRequestContext containerRequestContext) {
    TokenDto tokenDto = tokenExtractor.extract(containerRequestContext);
    manufacturerService.reactivate(tokenDto);
    return Response.ok().build();
  }
}
