package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DeliveryNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchDeliveryNotFoundExceptionMapper
    implements ExceptionMapper<DeliveryNotFoundException> {
  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String ERROR = "DELIVERY_NOT_FOUND";
  private static final String DESCRIPTION = "Could not find delivery with id %s.";

  @Override
  public Response toResponse(DeliveryNotFoundException e) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(ERROR, String.format(DESCRIPTION, e.getDeliveryId())))
        .build();
  }
}
