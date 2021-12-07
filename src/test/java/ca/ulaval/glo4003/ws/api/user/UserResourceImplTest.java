package ca.ulaval.glo4003.ws.api.user;

import ca.ulaval.glo4003.ws.api.shared.RequestValidator;
import ca.ulaval.glo4003.ws.api.shared.exception.InvalidFormatException;
import ca.ulaval.glo4003.ws.api.user.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.ws.domain.user.exception.LoginFailedException;
import ca.ulaval.glo4003.ws.service.user.UserService;
import ca.ulaval.glo4003.ws.service.user.dto.LoginResponseDto;
import ca.ulaval.glo4003.ws.service.user.dto.LoginUserDto;
import ca.ulaval.glo4003.ws.service.user.dto.RegisterUserDto;
import ca.ulaval.glo4003.ws.testUtil.LoginResponseDtoBuilder;
import ca.ulaval.glo4003.ws.testUtil.LoginUserDtoBuilder;
import ca.ulaval.glo4003.ws.testUtil.RegisterUserDtoBuilder;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserResourceImplTest {

  @Mock private UserService userService;

  @Mock private RequestValidator registerUserDtoValidator;

  private UserResourceImpl userResource;

  @BeforeEach
  public void setUp() {
    userResource = new UserResourceImpl(userService, registerUserDtoValidator);
  }

  @Test
  public void givenValidRegisterUserDto_whenRegisterUser_thenUserIsRegistered() {
    // given
    RegisterUserDto aUserDto = new RegisterUserDtoBuilder().build();

    // when
    userResource.registerUser(aUserDto);

    // then
    verify(userService).registerUser(aUserDto);
  }

  @Test
  public void givenInvalidRegisterUserDto_whenRegisterUser_thenThrowInvalidFormatException() {
    // given
    RegisterUserDto aUserDto = new RegisterUserDtoBuilder().build();
    doThrow(InvalidFormatException.class).when(registerUserDtoValidator).validate(aUserDto);

    // when
    Executable registeringUser = () -> userResource.registerUser(aUserDto);

    // then
    assertThrows(InvalidFormatException.class, registeringUser);
  }

  @Test
  public void
      givenEmailAlreadyAssociatedToUser_whenRegisterUser_thenThrowEmailAlreadyInUseException() {
    // given
    RegisterUserDto aUserDto = new RegisterUserDtoBuilder().build();
    doThrow(EmailAlreadyInUseException.class).when(userService).registerUser(aUserDto);

    // when
    Executable registeringUser = () -> userResource.registerUser(aUserDto);

    // then
    assertThrows(EmailAlreadyInUseException.class, registeringUser);
  }

  @Test
  public void givenSuccessfulLogin_whenLogin_thenReturn200WithLoginToken() {
    // given
    LoginUserDto aLoginDto = new LoginUserDtoBuilder().build();
    LoginResponseDto aLoginResponseDto = new LoginResponseDtoBuilder().build();
    given(userService.login(aLoginDto.getEmail(), aLoginDto.getPassword()))
        .willReturn(aLoginResponseDto);

    // when
    Response response = userResource.login(aLoginDto);
    LoginResponseDto actualLoginResponseDto = (LoginResponseDto) response.getEntity();

    // then
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    assertThat(actualLoginResponseDto.getToken()).matches(aLoginResponseDto.getToken());
  }

  @Test
  public void givenWrongEmailPasswordCombination_whenLogin_thenThrowLoginFailedException() {
    // given
    LoginUserDto aLoginDto = new LoginUserDtoBuilder().build();
    given(userService.login(aLoginDto.getEmail(), aLoginDto.getPassword()))
        .willThrow(new LoginFailedException());

    // when
    Executable loggingIn = () -> userResource.login(aLoginDto);

    // then
    assertThrows(LoginFailedException.class, loggingIn);
  }
}
