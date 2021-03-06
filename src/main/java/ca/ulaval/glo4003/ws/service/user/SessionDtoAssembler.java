package ca.ulaval.glo4003.ws.service.user;

import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.service.user.dto.SessionDto;

public class SessionDtoAssembler {
  public SessionDto assemble(Session session) {
    return new SessionDto(session.getToken().getTokenValue());
  }
}
