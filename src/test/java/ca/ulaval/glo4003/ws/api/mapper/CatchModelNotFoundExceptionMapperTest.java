package ca.ulaval.glo4003.ws.api.mapper;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.shared.ExceptionResponse;
import ca.ulaval.glo4003.ws.domain.vehicle.exception.ModelNotFoundException;
import jakarta.ws.rs.core.Response;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchModelNotFoundExceptionMapperTest {
  private static final Set<String> MODELS = Set.of("Vandry", "Pouliot");
  private static final int EXPECTED_STATUS_CODE = Response.Status.BAD_REQUEST.getStatusCode();
  private static final String EXPECTED_ERROR = "INVALID_MODEL";
  private static final String EXPECTED_DESCRIPTION =
      String.format("Model must be one of the following type: {%s}.", MODELS);

  private CatchModelNotFoundExceptionMapper mapper;

  @BeforeEach
  public void setUp() {
    mapper = new CatchModelNotFoundExceptionMapper();
  }

  @Test
  public void givenModelNotFoundException_whenToResponse_thenReturnRightResponse() {
    // given
    ModelNotFoundException exception = new ModelNotFoundException(MODELS);

    // when
    Response response = mapper.toResponse(exception);
    ExceptionResponse exceptionResponse = (ExceptionResponse) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(EXPECTED_STATUS_CODE);
    assertThat(exceptionResponse.getError()).isEqualTo(EXPECTED_ERROR);
    assertThat(exceptionResponse.getDescription()).isEqualTo(EXPECTED_DESCRIPTION);
  }
}
