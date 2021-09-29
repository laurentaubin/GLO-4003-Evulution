package ca.ulaval.glo4003.ws.domain.exception;

public class UnallowedUserException extends RuntimeException {
  private static final String error = "UNALLOWED_USER";
  private static final String description =
      "This user does not have the permissions to access this resource.";

  public UnallowedUserException() {
    super();
  }

  public static String getDescription() {
    return description;
  }

  public static String getError() {
    return error;
  }
}
