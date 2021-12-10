package ca.ulaval.glo4003.ws.api.manufacturer;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/productions")
public interface ManufacturerResource {
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/shutdown")
  Response shutdown(@Context ContainerRequestContext containerRequestContext);

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/reactivate")
  Response reactivate(@Context ContainerRequestContext containerRequestContext);
}
