package ca.ulaval.glo4003.ws.api.handler.exception;

public class UnauthorizedUserException extends RuntimeException {
  private static final String error = "UNALLOWED_USER";
  private static final String description =
      "This user does not have the permissions to access this resource.";

  public UnauthorizedUserException() {
    super();
  }

  public String getDescription() {
    return description;
  }

  public String getError() {
    return error;
  }
}
