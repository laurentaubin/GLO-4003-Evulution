package ca.ulaval.glo4003.ws.api.shared;

import ca.ulaval.glo4003.ws.api.shared.exception.EmptyTokenHeaderException;
import ca.ulaval.glo4003.ws.api.transaction.TokenDtoAssembler;
import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TokenExtractorTest {
  private static final String A_AUTH_HEADER_NAME = "Bearer";
  private static final TokenDto A_AUTH_TOKEN = new TokenDto("some_token_value");
  private static final String A_AUTH_HEADER = A_AUTH_HEADER_NAME + " " + A_AUTH_TOKEN.getToken();

  @Mock private TokenDtoAssembler tokenDtoAssembler;
  @Mock private ContainerRequestContext containerRequestContext;

  @Test
  public void whenExtract_thenTokenIsExtractedFromHeader() {
    // given
    given(containerRequestContext.getHeaderString(any())).willReturn(A_AUTH_HEADER);
    given(tokenDtoAssembler.assemble(any())).willReturn(A_AUTH_TOKEN);
    TokenExtractor tokenExtractor = new TokenExtractor(A_AUTH_HEADER_NAME, tokenDtoAssembler);

    // when
    TokenDto extractedToken = tokenExtractor.extract(containerRequestContext);

    // then
    assertThat(extractedToken).isEqualTo(A_AUTH_TOKEN);
  }

  @Test
  public void givenAbsentHeader_whenExtract_thenTokenThrowEmptyHeaderException() {
    // given
    TokenExtractor tokenExtractor = new TokenExtractor(A_AUTH_HEADER_NAME, tokenDtoAssembler);

    // when
    Executable execution = () -> tokenExtractor.extract(containerRequestContext);

    // then
    assertThrows(EmptyTokenHeaderException.class, execution);
  }
}
