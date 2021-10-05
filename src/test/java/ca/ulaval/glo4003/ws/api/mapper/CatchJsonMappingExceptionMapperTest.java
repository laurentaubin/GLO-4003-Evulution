package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatchJsonMappingExceptionMapperTest {
  private static final String INVALID_JSON_FORMAT_ERROR = "INVALID_JSON_FORMAT";
  private static final String A_DESCRIPTION = "sdidsauiha";

  @Mock private JsonMappingException jsonMappingException;

  @Test
  public void
      givenExceptionMessage_whenToResponse_ThenReturnResponseWithRightErrorAndDescription() {
    // given
    CatchJsonMappingExceptionMapper exceptionMapper = new CatchJsonMappingExceptionMapper();
    given(jsonMappingException.getOriginalMessage()).willReturn(A_DESCRIPTION);

    // when
    Response response = exceptionMapper.toResponse(jsonMappingException);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    assertThat(exceptionResponse.getError()).matches(INVALID_JSON_FORMAT_ERROR);
    assertThat(exceptionResponse.getDescription()).matches(A_DESCRIPTION);
  }
}