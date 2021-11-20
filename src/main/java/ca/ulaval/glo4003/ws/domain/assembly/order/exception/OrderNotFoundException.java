package ca.ulaval.glo4003.ws.domain.assembly.order.exception;

public class OrderNotFoundException extends RuntimeException {

  public OrderNotFoundException(String message) {
    super(message);
  }
}
