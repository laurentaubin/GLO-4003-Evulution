package ca.ulaval.glo4003.ws.api.shared;

import ca.ulaval.glo4003.ws.api.shared.exception.EmptyTokenHeaderException;
import ca.ulaval.glo4003.ws.api.transaction.TokenDtoAssembler;
import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

public class TokenExtractor {

  private final String authorizationHeaderName;
  private final TokenDtoAssembler tokenDtoAssembler;

  public TokenExtractor(String authorizationHeaderName, TokenDtoAssembler tokenDtoAssembler) {
    this.authorizationHeaderName = authorizationHeaderName;
    this.tokenDtoAssembler = tokenDtoAssembler;
  }

  public TokenDto extract(ContainerRequestContext containerRequestContext) {
    String authorizationHeader = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
    if (authorizationHeader == null) {
      throw new EmptyTokenHeaderException();
    }
    return tokenDtoAssembler.assemble(authorizationHeader.substring(authorizationHeaderName.length()).trim());
  }
}
