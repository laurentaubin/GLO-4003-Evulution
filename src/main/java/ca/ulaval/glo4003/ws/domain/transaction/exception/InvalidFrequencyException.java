package ca.ulaval.glo4003.ws.domain.transaction.exception;

import java.util.Set;

public class InvalidFrequencyException extends RuntimeException {
  private final Set<String> frequencies;

  public InvalidFrequencyException(Set<String> frequencies) {
    this.frequencies = frequencies;
  }

  public Set<String> getFrequencies() {
    return frequencies;
  }
}
