package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DuplicateDeliveryException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchDuplicateDeliveryExceptionMapper
    implements ExceptionMapper<DuplicateDeliveryException> {
  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();

  @Override
  public Response toResponse(DuplicateDeliveryException e) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(e.error, e.description))
        .build();
  }
}