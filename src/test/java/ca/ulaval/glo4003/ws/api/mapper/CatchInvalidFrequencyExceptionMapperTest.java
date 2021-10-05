package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.transaction.exception.InvalidFrequencyException;
import jakarta.ws.rs.core.Response;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchInvalidFrequencyExceptionMapperTest {
  private static final Set<String> FREQUENCIES = Set.of("monthly", "annually");
  private static final int EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String EXPECTED_ERROR = "INVALID_FREQUENCY";
  private static final String EXPECTED_DESCRIPTION =
      String.format("Frequency must be one of the following type: {%s}.", FREQUENCIES);

  private CatchInvalidFrequencyExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchInvalidFrequencyExceptionMapper();
  }

  @Test
  void givenInvalidFrequencyException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    InvalidFrequencyException exception = new InvalidFrequencyException(FREQUENCIES);

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
