package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.annotation.Priority;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(1)
public class CatchJsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
  private static final int STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String ERROR = "INVALID_JSON_FORMAT";

  @Override
  public Response toResponse(JsonMappingException e) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(ERROR, e.getOriginalMessage()))
        .build();
  }
}
