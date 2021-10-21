package ca.ulaval.glo4003.ws.domain.assembly;

public enum AssemblyStatus {
  RECEIVED("RECEIVED"),
  IN_PROGRESS("IN_PROGRESS"),
  ASSEMBLED("ASSEMBLED"),
  DOES_NOT_EXIST("DOES_NOT_EXIST");

  private String message;

  private AssemblyStatus(String message) {
    this.message = message;
  }
}
