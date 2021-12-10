package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;

public class TokenDtoAssembler {

    public TokenDto assemble(String token) {
        return new TokenDto(token);
    }
}
