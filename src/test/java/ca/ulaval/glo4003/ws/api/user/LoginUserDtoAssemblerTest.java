package ca.ulaval.glo4003.ws.api.user;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.user.response.LoginResponse;
import ca.ulaval.glo4003.ws.service.user.dto.SessionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginUserDtoAssemblerTest {
  private static final String A_TOKEN = "token";

  private LoginUserDtoAssembler loginUserDtoAssembler;

  @BeforeEach
  void setUp() {
    loginUserDtoAssembler = new LoginUserDtoAssembler();
  }

  @Test
  void givenSessionDto_whenAssemble_thenReturnCorrectLoginResponse() {
    // given
    SessionDto sessionDto = new SessionDto(A_TOKEN);

    // when
    LoginResponse loginResponse = loginUserDtoAssembler.assemble(sessionDto);

    // then
    assertThat(loginResponse.getToken()).matches(A_TOKEN);
  }
}
