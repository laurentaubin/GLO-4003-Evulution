package ca.ulaval.glo4003.ws.service.user;

import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionAdministrator;
import ca.ulaval.glo4003.ws.domain.auth.SessionToken;
import ca.ulaval.glo4003.ws.domain.delivery.DeliveryId;
import ca.ulaval.glo4003.ws.domain.transaction.TransactionId;
import ca.ulaval.glo4003.ws.domain.user.OwnershipDomainService;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import ca.ulaval.glo4003.ws.fixture.UserFixture;
import ca.ulaval.glo4003.ws.service.user.dto.RegisterUserDto;
import ca.ulaval.glo4003.ws.service.user.dto.SessionDto;
import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  private static final String AN_EMAIL = "an@email.com";
  private static final String A_PASSWORD = "pass123";
  private static final String A_FIELD = "dummy";
  private static final SessionToken A_TOKEN = new SessionToken("token");
  private static final TransactionId A_TRANSACTION_ID = TransactionId.fromString("transaction_id");
  private static final DeliveryId A_DELIVERY_ID = DeliveryId.fromString("delivery_id");
  private static final List<Role> PRIVILEGED_ROLES =
          new ArrayList<>(List.of(Role.CUSTOMER, Role.PRODUCTION_MANAGER));

  @Mock private SessionAdministrator sessionAdministrator;
  @Mock private UserAssembler userAssembler;
  @Mock private SessionDtoAssembler sessionDtoAssembler;
  @Mock private OwnershipDomainService ownershipDomainService;
  @Mock private UserRepository userRepository;
  @Mock private TokenDto tokenDto;
  @Mock private Session aSession;
  @Mock private User user;

  private UserService userService;
  private RegisterUserDto registerUserDto;

  @BeforeEach
  public void setUp() {
    registerUserDto = createRegisterUserDto();
    userService =
        new UserService(
            userRepository,
            sessionAdministrator,
            userAssembler,
            sessionDtoAssembler,
            ownershipDomainService);
  }

  @Test
  public void whenRegisterUser_thenUserIsRegistered() {
    // given
    User user = new UserFixture().build();
    given(userAssembler.assemble(registerUserDto)).willReturn(user);

    // when
    userService.registerUser(registerUserDto);

    // then
    verify(sessionAdministrator).registerUser(user, A_PASSWORD);
  }

  @Test
  public void whenLogin_thenReturnGeneratedToken() {
    // given
    Session aSession = new Session(A_TOKEN, AN_EMAIL);
    given(sessionAdministrator.login(AN_EMAIL, A_PASSWORD)).willReturn(aSession);
    given(sessionDtoAssembler.assemble(aSession)).willReturn(createSessionDto());

    // when
    SessionDto sessionDto = userService.login(AN_EMAIL, A_PASSWORD);

    // then
    assertThat(sessionDto.getToken()).isEqualTo(A_TOKEN.getTokenValue());
  }

  @Test public void whenLogin_thenLoginWithSessionAdministrator() {
    // given
    Session aSession = new Session(A_TOKEN, AN_EMAIL);
    given(sessionAdministrator.login(AN_EMAIL, A_PASSWORD)).willReturn(aSession);
    given(sessionDtoAssembler.assemble(aSession)).willReturn(createSessionDto());

    // when
    userService.login(AN_EMAIL, A_PASSWORD);

    // then
    verify(sessionAdministrator).login(AN_EMAIL, A_PASSWORD);
  }

  @Test public void whenIsAllowed_thenValidatePermissions() {
    // when
    userService.isAllowed(tokenDto, PRIVILEGED_ROLES);

    // then
    verify(sessionAdministrator).validatePermissions(tokenDto, PRIVILEGED_ROLES);
  }

  @Test public void whenMapDeliveryIdToTransactionId_thenAddTransactionDeliveryToUser() {
    // given
    given(aSession.getEmail()).willReturn(AN_EMAIL);
    given(sessionAdministrator.retrieveSession(tokenDto)).willReturn(aSession);
    given(userRepository.findUser(AN_EMAIL)).willReturn(user);

    // when
    userService.mapDeliveryIdToTransactionId(tokenDto, A_TRANSACTION_ID, A_DELIVERY_ID);

    // then
    verify(user).addTransactionDelivery(A_TRANSACTION_ID, A_DELIVERY_ID);
  }

  @Test public void whenMapDeliveryIdToTransactionId_thenSaveUser() {
    // given
    given(aSession.getEmail()).willReturn(AN_EMAIL);
    given(sessionAdministrator.retrieveSession(tokenDto)).willReturn(aSession);
    given(userRepository.findUser(AN_EMAIL)).willReturn(user);

    // when
    userService.mapDeliveryIdToTransactionId(tokenDto, A_TRANSACTION_ID, A_DELIVERY_ID);

    // then
    verify(userRepository).update(user);
  }

  @Test public void whenGetTransactionIdFromDeliveryId_thenGetTransactionFromUser() {
    // given
    given(aSession.getEmail()).willReturn(AN_EMAIL);
    given(sessionAdministrator.retrieveSession(tokenDto)).willReturn(aSession);
    given(userRepository.findUser(AN_EMAIL)).willReturn(user);

    // when
    userService.getTransactionIdFromDeliveryId(tokenDto, A_DELIVERY_ID);

    // then
    verify(user).getTransactionIdFromDeliveryId(A_DELIVERY_ID);
  }

  @Test public void whenValidateTransactionOwnerShip_thenValidateTransactionOwnership() {
    // given
    given(aSession.getEmail()).willReturn(AN_EMAIL);
    given(sessionAdministrator.retrieveSession(tokenDto)).willReturn(aSession);
    given(userRepository.findUser(AN_EMAIL)).willReturn(user);

    // when
    userService.validateTransactionOwnership(tokenDto, A_TRANSACTION_ID, PRIVILEGED_ROLES);

    // then
    verify(ownershipDomainService).validateTransactionOwnership(user, A_TRANSACTION_ID);
  }

  @Test public void whenValidateTransactionOwnerShip_thenVerifyUserIsAllowed() {
    // given
    given(aSession.getEmail()).willReturn(AN_EMAIL);
    given(sessionAdministrator.retrieveSession(tokenDto)).willReturn(aSession);
    given(userRepository.findUser(AN_EMAIL)).willReturn(user);

    // when
    userService.validateTransactionOwnership(tokenDto, A_TRANSACTION_ID, PRIVILEGED_ROLES);

    // then
    verify(sessionAdministrator).validatePermissions(tokenDto, PRIVILEGED_ROLES);
  }

  @Test public void whenValidateDeliveryOwnerShip_thenValidateDeliveryOwnership() {
    // given
    given(aSession.getEmail()).willReturn(AN_EMAIL);
    given(sessionAdministrator.retrieveSession(tokenDto)).willReturn(aSession);
    given(userRepository.findUser(AN_EMAIL)).willReturn(user);

    // when
    userService.validateDeliveryOwnership(tokenDto, A_DELIVERY_ID, PRIVILEGED_ROLES);

    // then
    verify(ownershipDomainService).validateDeliveryOwnership(user, A_DELIVERY_ID);
  }

  @Test public void whenValidateDeliveryOwnerShip_thenVerifyUserIsAllowed() {
    // given
    given(aSession.getEmail()).willReturn(AN_EMAIL);
    given(sessionAdministrator.retrieveSession(tokenDto)).willReturn(aSession);
    given(userRepository.findUser(AN_EMAIL)).willReturn(user);

    // when
    userService.validateDeliveryOwnership(tokenDto, A_DELIVERY_ID, PRIVILEGED_ROLES);

    // then
    verify(sessionAdministrator).validatePermissions(tokenDto, PRIVILEGED_ROLES);
  }


  private RegisterUserDto createRegisterUserDto() {
    return new RegisterUserDto(A_FIELD, A_FIELD, A_FIELD, AN_EMAIL, A_PASSWORD);
  }

  private SessionDto createSessionDto() {
    return new SessionDto(A_TOKEN.getTokenValue());
  }
}
