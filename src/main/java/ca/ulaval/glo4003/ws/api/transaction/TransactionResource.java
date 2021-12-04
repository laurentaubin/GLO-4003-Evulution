package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.api.transaction.dto.BatteryRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.PaymentRequest;
import ca.ulaval.glo4003.ws.api.transaction.dto.VehicleRequest;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import jakarta.annotation.Resource;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
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
      @PathParam("transactionId") TransactionId transactionId,
      VehicleRequest vehicleRequest);

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/{transactionId}/battery")
  Response addBattery(
      @Context ContainerRequestContext containerRequestContext,
      @PathParam("transactionId") TransactionId transactionId,
      BatteryRequest batteryRequest);

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/{transactionId}/complete")
  Response completeTransaction(
      @Context ContainerRequestContext containerRequestContext,
      @PathParam("transactionId") TransactionId transactionId,
      PaymentRequest paymentRequest);
}
