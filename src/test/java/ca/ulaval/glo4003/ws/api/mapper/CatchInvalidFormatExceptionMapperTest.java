package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class CatchInvalidFormatExceptionMapperTest {
  private static final String A_PROPERTY = "Property";
  private static final int EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String EXPECTED_ERROR = "INVALID_FORMAT";

  private CatchInvalidFormatExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchInvalidFormatExceptionMapper();
  }

  @Test
  void givenInvalidFormatException_whenToResponse_thenReturnRightResponse() {
    // given
    InvalidFormatException exception = new InvalidFormatException(A_PROPERTY);

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(exception.getDescription());
  }
}
