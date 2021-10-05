package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.exception.IncompleteTransactionException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchIncompleteTransactionExceptionMapper
    implements ExceptionMapper<IncompleteTransactionException> {
  private static final int STATUS_CODE = Status.BAD_REQUEST.getStatusCode();
  private static final String ERROR = "TRANSACTION_INCOMPLETE";
  private static final String DESCRIPTION = "Transaction is missing a vehicle and/or battery";

  @Override
  public Response toResponse(IncompleteTransactionException exception) {
    return Response.status(STATUS_CODE).entity(new ExceptionResponse(ERROR, DESCRIPTION)).build();
  }
}
