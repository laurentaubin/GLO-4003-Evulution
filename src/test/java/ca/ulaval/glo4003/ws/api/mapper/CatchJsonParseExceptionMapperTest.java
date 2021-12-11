package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import com.fasterxml.jackson.core.JsonParseException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CatchJsonParseExceptionMapperTest {
  private static final String AN_INVALID_JSON_FORMAT_ERROR = "INVALID_JSON_FORMAT";
  private static final String A_DESCRIPTION = "sdidsauiha";

  @Mock private JsonParseException jsonParseException;

  @Test
  public void
      givenExceptionMessage_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    CatchJsonParseExceptionMapper exceptionMapper = new CatchJsonParseExceptionMapper();
    given(jsonParseException.getOriginalMessage()).willReturn(A_DESCRIPTION);

    // when
    Response response = exceptionMapper.toResponse(jsonParseException);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    assertThat(exceptionResponse.getError()).matches(AN_INVALID_JSON_FORMAT_ERROR);
    assertThat(exceptionResponse.getDescription()).matches(A_DESCRIPTION);
  }
}
