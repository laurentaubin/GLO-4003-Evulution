package ca.ulaval.glo4003.ws.api.util;

import ca.ulaval.glo4003.ws.domain.exception.EmptyTokenHeaderException;

public class TokenExtractor {

  String authorizationHeaderName;

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
