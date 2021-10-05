package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.exception.ModelNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchModelNotFoundExceptionMapper implements ExceptionMapper<ModelNotFoundException> {
  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String ERROR = "INVALID_MODEL";
  private static final String DESCRIPTION = "Model must be one of the following type: {%s}.";

  @Override
  public Response toResponse(ModelNotFoundException e) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(ERROR, String.format(DESCRIPTION, e.getModels())))
        .build();
  }
}
