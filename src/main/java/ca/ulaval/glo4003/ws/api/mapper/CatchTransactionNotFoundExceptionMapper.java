package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.exception.TransactionNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CatchTransactionNotFoundExceptionMapper
    implements ExceptionMapper<TransactionNotFoundException> {
  private static final int STATUS_CODE = Response.Status.NOT_FOUND.getStatusCode();
  private static final String ERROR = "TRANSACTION_NOT_FOUND";
  private static final String DESCRIPTION = "Could not find transaction id %s.";

  @Override
  public Response toResponse(TransactionNotFoundException e) {
    return Response.status(STATUS_CODE)
        .entity(
            new ExceptionResponse(
                ERROR, String.format(DESCRIPTION, e.getTransactionId().toString())))
        .build();
  }
}
