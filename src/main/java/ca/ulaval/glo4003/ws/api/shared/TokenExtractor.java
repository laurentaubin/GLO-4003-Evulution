package ca.ulaval.glo4003.ws.api.shared;

import ca.ulaval.glo4003.ws.api.shared.exception.EmptyTokenHeaderException;

public class TokenExtractor {

  private final String authorizationHeaderName;

  public TokenExtractor(String authorizationHeaderName) {
    this.authorizationHeaderName = authorizationHeaderName;
  }

  public String extract(String authorizationHeader) {
    if (authorizationHeader == null) {
      throw new EmptyTokenHeaderException();
    }
    return authorizationHeader.substring(authorizationHeaderName.length()).trim();
  }
}
