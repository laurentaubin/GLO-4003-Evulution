package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DeliveryNotReadyException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchDeliveryNotReadyExceptionMapper
    implements ExceptionMapper<DeliveryNotReadyException> {
  private static final int STATUS_CODE = Status.BAD_REQUEST.getStatusCode();
  private static final String ERROR = "DELIVERY_NOT_READY";
  private static final String DESCRIPTION = "The requested delivery is not ready";

  @Override
  public Response toResponse(DeliveryNotReadyException e) {
    return Response.status(STATUS_CODE).entity(new ExceptionResponse(ERROR, DESCRIPTION)).build();
  }
}
