package ca.ulaval.glo4003.ws.api.delivery;

import ca.ulaval.glo4003.ws.api.delivery.dto.DeliveryLocationRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/deliveries")
public interface DeliveryResource {
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{deliveryId}/location")
  Response addDeliveryLocation(
      @Context ContainerRequestContext containerRequestContext,
      @PathParam("deliveryId") String deliveryId,
      DeliveryLocationRequest deliveryLocationRequest);
}
