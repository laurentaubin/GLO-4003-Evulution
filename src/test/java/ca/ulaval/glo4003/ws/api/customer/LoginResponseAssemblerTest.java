package ca.ulaval.glo4003.ws.api.customer;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.customer.dto.LoginResponseDto;
import ca.ulaval.glo4003.ws.domain.auth.LoginToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginResponseAssemblerTest {
  private static final String A_TOKEN = "token";

  private LoginResponseAssembler assembler;

  @BeforeEach
  public void setUp() {
    assembler = new LoginResponseAssembler();
  }

  @Test
  public void givenLoginToken_whenAssemble_thenReturnLoginResponseDtoWithRightToken() {
    // given
    LoginToken aLoginToken = new LoginToken(A_TOKEN);

    // when
    LoginResponseDto loginResponseDto = assembler.assemble(aLoginToken);

    // then
    assertThat(loginResponseDto.getToken()).matches(aLoginToken.getTokenValue());
  }
}
