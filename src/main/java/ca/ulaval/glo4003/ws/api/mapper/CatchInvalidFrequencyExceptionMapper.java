package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.exception.InvalidFrequencyException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchInvalidFrequencyExceptionMapper
    implements ExceptionMapper<InvalidFrequencyException> {
  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String ERROR = "INVALID_FREQUENCY";
  private static final String DESCRIPTION = "Frequency must be one of the following type: {%s}.";

  @Override
  public Response toResponse(InvalidFrequencyException e) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(ERROR, String.format(DESCRIPTION, e.getFrequencies())))
        .build();
  }
}
