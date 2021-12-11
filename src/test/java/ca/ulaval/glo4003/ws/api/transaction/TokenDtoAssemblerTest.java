package ca.ulaval.glo4003.ws.api.transaction;

import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class TokenDtoAssemblerTest {

    private static final String TOKEN = "a_token";

    private TokenDtoAssembler tokenDtoAssembler;

    @BeforeEach
    public void setUp() {
        tokenDtoAssembler = new TokenDtoAssembler();
    }

    @Test
    public void whenAssemble_thenReturnACorrectlyAssembledTokenDto() {
        // when
        TokenDto tokenDto =
                tokenDtoAssembler.assemble(TOKEN);

        // then
        assertThat(tokenDto.getToken()).isEqualTo(TOKEN);
    }

}