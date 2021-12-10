package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.delivery.exception.InvalidLocationException;
import jakarta.ws.rs.core.Response;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchInvalidLocationExceptionMapperTest {
  private static final Set<String> SOME_LOCATIONS = Set.of("Vachon", "Desjardins");
  private static final int EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String EXPECTED_ERROR = "INVALID_CAMPUS_LOCATION";
  private static final String EXPECTED_DESCRIPTION =
      String.format("Location must be one of the following building: {%s}.", SOME_LOCATIONS);

  private CatchInvalidLocationExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    exceptionMapper = new CatchInvalidLocationExceptionMapper();
  }

  @Test
  void givenEmptyTokenHeaderException_whenToResponse_thenResponseHasRightErrorAndDescription() {
    // given
    InvalidLocationException exception = new InvalidLocationException(SOME_LOCATIONS);

    // when
    Response response = exceptionMapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
