package ca.ulaval.glo4003.ws.infrastructure.exception;

public class UserNotFoundException extends RuntimeException {
  private static final String error = "USER_NOT_FOUND";
  private static final String description = "This user does not exist.";

  public UserNotFoundException() {
    super();
  }

  public static String getDescription() {
    return description;
  }

  public static String getError() {
    return error;
  }
}
