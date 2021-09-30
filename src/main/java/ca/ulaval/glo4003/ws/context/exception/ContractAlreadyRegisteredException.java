package ca.ulaval.glo4003.ws.context.exception;

public class ContractAlreadyRegisteredException extends RuntimeException {
  public <T> ContractAlreadyRegisteredException(Class<T> contract) {
    super("A service implementation was already provided for " + contract.getName());
  }
}
