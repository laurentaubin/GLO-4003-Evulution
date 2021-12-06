package ca.ulaval.glo4003.ws.service.user;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionToken;
import ca.ulaval.glo4003.ws.service.user.dto.LoginResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginResponseAssemblerTest {
  private static final String AN_EMAIL = "anEmail@mail.com";
  private static final SessionToken A_TOKEN = new SessionToken("token");

  private LoginResponseAssembler assembler;

  @BeforeEach
  public void setUp() {
    assembler = new LoginResponseAssembler();
  }

  @Test
  public void givenLoginToken_whenAssemble_thenReturnLoginResponseDtoWithRightToken() {
    // given
    Session aSession = new Session(A_TOKEN, AN_EMAIL);

    // when
    LoginResponseDto loginResponseDto = assembler.assemble(aSession);

    // then
    assertThat(loginResponseDto.getToken()).matches(aSession.getToken().getTokenValue());
  }
}
