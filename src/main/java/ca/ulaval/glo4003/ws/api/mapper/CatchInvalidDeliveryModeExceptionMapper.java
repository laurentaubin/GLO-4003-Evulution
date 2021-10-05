package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.delivery.exception.InvalidDeliveryModeException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchInvalidDeliveryModeExceptionMapper
    implements ExceptionMapper<InvalidDeliveryModeException> {
  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String ERROR = "INVALID_DELIVERY_MODE";
  private static final String DESCRIPTION =
      "Delivery mode must be one of the following mode: {%s}.";

  @Override
  public Response toResponse(InvalidDeliveryModeException e) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(ERROR, String.format(DESCRIPTION, e.getDeliveryModes())))
        .build();
  }
}
