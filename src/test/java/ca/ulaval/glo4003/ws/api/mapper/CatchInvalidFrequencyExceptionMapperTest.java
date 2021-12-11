package ca.ulaval.glo4003.ws.api.mapper;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.exception.InvalidFrequencyException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.google.common.truth.Truth.assertThat;

class CatchInvalidFrequencyExceptionMapperTest {
  private static final Set<String> SOME_FREQUENCIES = Set.of("monthly", "annually");
  private static final int EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String EXPECTED_ERROR = "INVALID_FREQUENCY";
  private static final String EXPECTED_DESCRIPTION =
      String.format("Frequency must be one of the following type: {%s}.", SOME_FREQUENCIES);

  private CatchInvalidFrequencyExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchInvalidFrequencyExceptionMapper();
  }

  @Test
  void givenInvalidFrequencyException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    InvalidFrequencyException exception = new InvalidFrequencyException(SOME_FREQUENCIES);

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
