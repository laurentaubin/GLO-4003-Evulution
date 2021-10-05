package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.exception.DuplicateTransactionException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchDuplicateTransactionExceptionMapper
    implements ExceptionMapper<DuplicateTransactionException> {
  private static final int STATUS_CODE = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
  private static final String ERROR = "DUPLICATE_TRANSACTION";
  private static final String DESCRIPTION = "Transaction with id %s already in repository.";

  @Override
  public Response toResponse(DuplicateTransactionException e) {
    return Response.status(STATUS_CODE)
        .entity(new ExceptionResponse(ERROR, String.format(DESCRIPTION, e.getTransactionId())))
        .build();
  }
}
