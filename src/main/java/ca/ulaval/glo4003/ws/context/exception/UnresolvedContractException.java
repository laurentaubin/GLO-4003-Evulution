package ca.ulaval.glo4003.ws.context.exception;

public class UnresolvedContractException extends RuntimeException {
  public <T> UnresolvedContractException(Class<T> contract) {
    super("No implementation was registered for contract " + contract.getName());
  }
}
