package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.transaction.dto.VehicleRequest;
import jakarta.annotation.Resource;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/sales")
@Resource
public interface TransactionResource {

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  Response createTransaction(@Context ContainerRequestContext containerRequestContext);

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/{transactionId}/vehicle")
  Response addVehicle(
      @Context ContainerRequestContext containerRequestContext,
      @PathParam("transactionId") String transactionId,
      VehicleRequest vehicleRequest);
}
