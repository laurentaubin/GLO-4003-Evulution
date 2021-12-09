package ca.ulaval.glo4003.ws.api.user;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.api.user.request.RegisterUserRequest;
import ca.ulaval.glo4003.ws.fixture.RegisterUserRequestBuilder;
import ca.ulaval.glo4003.ws.service.user.dto.RegisterUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegisterUserDtoAssemblerTest {
  private RegisterUserDtoAssembler registerUserDtoAssembler;

  @BeforeEach
  void setUp() {
    registerUserDtoAssembler = new RegisterUserDtoAssembler();
  }

  @Test
  void givenRegisterUserRequest_whenAssemble_thenReturnCorrectRegisterUserDto() {
    // given
    RegisterUserRequest request = new RegisterUserRequestBuilder().build();

    // when
    RegisterUserDto registerUserDto = registerUserDtoAssembler.assemble(request);

    // then
    assertThat(registerUserDto.getBirthDate()).matches(request.getBirthDate());
    assertThat(registerUserDto.getEmail()).matches(request.getEmail());
    assertThat(registerUserDto.getName()).matches(request.getName());
    assertThat(registerUserDto.getSex()).matches(request.getSex());
    assertThat(registerUserDto.getPassword()).matches(request.getPassword());
  }
}
