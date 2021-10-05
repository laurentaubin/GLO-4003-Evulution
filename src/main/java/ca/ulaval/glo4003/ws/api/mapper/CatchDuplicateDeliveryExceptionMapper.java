package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.delivery.exception.DuplicateDeliveryException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchDuplicateDeliveryExceptionMapper
    implements ExceptionMapper<DuplicateDeliveryException> {
  private static final int STATUS_CODE = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
  private static final String ERROR = "DUPLICATE_DELIVERY";
  private static final String DESCRIPTION = "Delivery with id %s already exists.";

  @Override
  public Response toResponse(DuplicateDeliveryException e) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(ERROR, String.format(DESCRIPTION, e.getDeliveryId())))
        .build();
  }
}
